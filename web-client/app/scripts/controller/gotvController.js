angular
  .module('canvass')
  .controller('gotvController', function ($scope, wardService, $filter, geoService, electorService, gotvService, $window, toastr, $uibModal) {

    $scope.numStreetsSelected = 0;
    $scope.validationErrors = [];
    $scope.currentSort = "Pledges Not Voted";
    $scope.flags = {
      hasPV: false,
      needsLift: false,
      telephone: false
    };

    function fire() {
      $scope.showSubMenu = $window.scrollY > 100;
      if (!$scope.$$phase) {
        $scope.$apply();
      }
    }

    var debounce = _.debounce(fire, 1, false);
    $("#canvassinputcontent").on('mousewheel', function () {
      debounce();
    });

    $scope.intentionRange = {
      minValue: 4,
      maxValue: 5
    };
    $scope.likelihoodRange = {
      minValue: 0,
      maxValue: 5
    };

    $scope.onSelectConstituency = function () {
      resetErrors();
      $scope.streets = [];
      $scope.numStreetsSelected = 0;
    };

    function scrollToPrintSection() {
      _.defer(function () {
        $("html, body").animate({scrollTop: $('#printCards').offset().top - 75}, 500);
      });
    }

    /**
     * Sorts the streets by the given field
     * @param field - either [mainStreet, numVoters, numCanvassed]
     * @param direction - either asc or desc
     * @param label - visible label to display in the dropdown
     */
    $scope.sortStreets = function (field, direction, label) {
      $scope.currentSort = label;
      $scope.streets = _.orderBy($scope.streets, field, direction);
    };

    $scope.onLoadedConstituencies = function (constituencies) {
      if (_.isEmpty(constituencies)) {
        $scope.userHasNoAssociations = true;
      }
      $scope.contentLoaded = true;
    };

    $scope.onSelectWard = function (directiveModel) {
      function addPledgesNotVoted(streetsResponse) {
        return {
          stats: streetsResponse.stats,
          streets: _.map(streetsResponse.streets, function (street) {
            street.numPledgesNotVoted = street.pledged - street.votedPledges;
            return street;
          })
        };
      }

      $scope.ward = directiveModel.ward;
      $scope.constituency = directiveModel.constituency;
      $scope.numStreetsSelected = 0;

      wardService.findStreetsByWard($scope.ward.code)
        .success(function (streets) {
          var streetsTx = addPledgesNotVoted(streets);
          $scope.streets = _.orderBy(streetsTx.streets, 'numPledgesNotVoted', 'desc');
          $scope.streetStats = streets.stats;
          scrollToPrintSection();
        })
        .error(function() {
          toastr.error('Failed to download streets, please try again later', 'Error');
        });
    };

    $scope.onClearSelections = function () {
      _.each($scope.streets, function (street) {
        street.selected = false;
      });
      $scope.updateSelectedStreets();
    };

    $scope.updateSelectedStreets = function () {
      if ($scope.streets && $scope.streets.length) {
        $scope.numStreetsSelected = _.size(_.filter($scope.streets, function (s) {
          return s.selected;
        }));
      } else {
        $scope.numStreetsSelected = 0;
      }
    };

    $scope.onPrintSelected = function () {
      $scope.errorLoadingData = null;
      var selected = _.filter($scope.streets, function (s) {
        return s.selected;
      });
      doPrint($scope.ward.code, selected, false);
    };

    $scope.onPrintAll = function () {
      $scope.errorLoadingData = null;
      doPrint($scope.ward.code, $scope.streets, false);
    };

    $scope.onSelectAll = function () {
      _.each($scope.streets, function (street) {
        street.selected = true;
      });
      $scope.updateSelectedStreets();
    };

    $scope.onSelectPriority = function () {
      _.each($scope.streets, function (street) {
        street.selected = street.priority === 3;
      });
      $scope.updateSelectedStreets();
    };

    $scope.onSelectPledges = function () {
      _.each($scope.streets, function (street) {
        street.selected = street.pledged > 0 && street.votedPledges < street.pledged;
      });
      $scope.updateSelectedStreets();
    };

    $scope.onPrintLabels = function () {
      var selected = _.filter($scope.streets, function (s) {
        return s.selected;
      });
      doPrint($scope.ward.code, selected, true);
    };

    function doPrint(wardCode, streets, isLabels) {
      if (_.isEmpty(streets)) {
        toastr.warning('Please select some streets and search again.', 'No streets selected');
        return;
      }

      var data = buildRequest(streets);
      gotvService.retrievePdfOfElectorsByStreets(wardCode, data, isLabels)
        .success(function (response) {
          var file = new Blob([response], {type: 'application/pdf'});
          saveAs(file, createPdfFileName() + '.pdf');
        })
        .error(function (error) {
          if (error && error.status === 404) {
            toastr.info('We did not find any voters matching the search criteria.', 'No voters found');
          } else {
            toastr.error('Failed to request voters.', 'Error');
          }
        });
    }

    function createPdfFileName() {
      return $scope.ward.name + ' in ' + $scope.constituency.name + ' ' + new Date().toISOString();
    }

    function buildRequest(streets) {
      var flags = getActiveOptionalFlags();
      flags.intentionFrom = $scope.intentionRange.minValue;
      flags.intentionTo = $scope.intentionRange.maxValue;
      flags.likelihoodFrom = $scope.likelihoodRange.minValue;
      flags.likelihoodTo = $scope.likelihoodRange.maxValue;
      return {
        streets: streets,
        flags: flags
      };
    }

    function getActiveOptionalFlags() {
      var flags = {};
      if ($scope.flags.telephone) {
        flags.telephone = true;
      }
      if ($scope.flags.hasPV) {
        flags.hasPV = true;
      }
      if ($scope.flags.needsLift) {
        flags.needsLift = true;
      }
      return flags;
    }

    $scope.getRatioPledged = function (numVoted, numPledged) {
      if (numVoted === 0 && numPledged === 0) {
        return 100;
      }
      return Math.round(numVoted / numPledged * 100);
    };

    $scope.onShowPopupMap = function (street) {
      var streetLabel = $filter('streetSingleLineFilter')(street);
      geoService.reverseLookupAddresses(extractStreetInfoForGeocoding([street]))
        .success(function (response) {
          if (!_.isEmpty(response) && !_.isEmpty(response[0].results)) {
            var geom = response[0].results[0].geometry;
            var map = {
              center: {
                latitude: geom.location.lat,
                longitude: geom.location.lng
              },
              options: {
                fullscreenControl: true,
                fullscreenControlOptions: {
                  position: google.maps.ControlPosition.RIGHT_TOP
                },
                rotateControl: true,
                scaleControl: true
              },
              zoom: 17,
              street: streetLabel,
              markers: [{
                id: 1,
                latitude: geom.location.lat,
                longitude: geom.location.lng,
                show: true,
                title: streetLabel
              }]
            };

            $uibModal.open({
              animation: true,
              templateUrl: 'mapModal.html',
              controller: 'mapModalInstanceCtrl',
              resolve: {
                mapData: function () {
                  return map;
                }
              }
            });
          } else {
            toastr.error('Sorry we were unable to geocode that street based on the address', 'No coordinates for address');
          }
        });
    };

    function extractStreetInfoForGeocoding(streets) {
      function extractPostcode(postcode) {
        if (!_.isEmpty(postcode.length)) {
          return _.head(postcode);
        } else {
          return "";
        }
      }

      return _.map(streets, function (street) {
        var postcode = extractPostcode(street.postcode);
        return [street.mainStreet, postcode, street.postTown, " UK"].join(" ");
      });
    }

    function resetErrors() {
      $scope.failedToLoadStreets = null;
      $scope.errorLoadingData = null;
    }
  });
