angular
  .module('canvass')
  .controller('gotpvController', function ($scope, wardService, $filter, geoService, electorService, gotvService, $window, toastr, $uibModal) {

    $scope.numStreetsSelected = 0;
    $scope.currentSort = "Priority DESC";

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
      function removeStreetsWithoutVoters(streetsResponse) {
        return {
          stats: streetsResponse.stats,
          streets: _.filter(streetsResponse.streets, function (street) {
            return street.numVoters !== 0;
          })
        };
      }

      $scope.ward = directiveModel.ward;
      $scope.constituency = directiveModel.constituency;
      $scope.numStreetsSelected = 0;

      wardService.findStreetsByWard($scope.ward.code)
        .success(function (streets) {
          var streetsTx = removeStreetsWithoutVoters(streets);
          $scope.streets = _.orderBy(streetsTx.streets, 'priority', 'desc');
          $scope.streetStats = streets.stats;
          scrollToPrintSection();
        });
    };

    $scope.getNumStreetsSelected = function () {
      if ($scope.streets && $scope.streets.length) {
        $scope.numStreetsSelected = _.size(_.filter($scope.streets, function (s) {
          return s.selected;
        }));
      }
    };

    $scope.onPrintLabels = function () {
      var selected = _.filter($scope.streets, function (s) {
        return s.selected;
      });
      doPrint($scope.ward.code, selected, true);
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

    function doPrint(wardCode, streets, isLabels) {
      if (_.isEmpty(streets)) {
        toastr.warning('Please select the streets you wish to generate labels for', 'No streets selected');
      }

      gotvService.retrievePdfOfElectorsByStreets(wardCode, {streets: streets}, isLabels)
        .success(function (response) {
          var file = new Blob([response], {type: 'application/pdf'});
          saveAs(file, $scope.ward.code + '.pdf');
        })
        .error(function (error) {
          if (error && error.status === 404) {
            toastr.info('We did not find any postal voters at the selected streets', 'No postal voters');
          } else {
            toastr.error('Failed to retrieve data from server', 'Error loading data');
          }
        });
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
