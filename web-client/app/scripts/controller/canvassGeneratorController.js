/**
 * Controller for the main canvass generator
 */
angular
  .module('canvass')
  .controller('canvassGeneratorController', function ($window, geoService, $scope, $timeout, $uibModal, wardService,
                                                      constituencyService, electorService, $filter, toastr, util) {

      var streetsContainerPos = null;
      $scope.wards = [];
      $scope.constituencySearchModel = '';
      $scope.wardSearchModel = '';
      $scope.numStreetsSelected = 0;
      $scope.currentSort = "Priority DESC";
      $scope.showFullScreenStreetsMap = false;

      $scope.onSelectConstituency = function () {
        $scope.streets = [];
      };

      $scope.map = {
        center: {
          latitude: 52.400251,
          longitude: -1.4497
        },
        zoom: 12,
        options: {
          fullscreenControl: true,
          fullscreenControlOptions: {
            position: google.maps.ControlPosition.LEFT_TOP
          }
        }
      };
      $scope.markers = [];

      $scope.onLoadedConstituencies = function (constituencies) {
        if (_.isEmpty(constituencies)) {
          $scope.userHasNoAssociations = true;
        }
        $scope.contentLoaded = true;
      };

      function determineIfPrintMenuDisplayed() {
        $scope.showSubMenu = streetsContainerPos && $window.scrollY > streetsContainerPos;
        if (!$scope.$$phase) {
          $scope.$apply();
        }
      }

      var debounce = _.debounce(determineIfPrintMenuDisplayed, 1, false);
      $("#canvassinputcontent").on('mousewheel', function () {
        debounce();
      });

      $scope.onSelectWard = function (model) {
        $scope.streets = [];
        $scope.numStreetsSelected = 0;

        $scope.wardSearchModel = model.ward;
        $scope.constituencySearchModel = model.constituency;

        wardService.findStreetsByWard($scope.wardSearchModel.code)
          .success(function (streets) {
            if (streets && streets.stats) {
              $scope.wardVotersCanvassed = streets.stats.canvassed;
              $scope.wardTotalVoters = streets.stats.voters;
              $scope.streets = _.orderBy(streets.streets, 'priority', 'desc');
              scrollToPrintSection();
            }
          })
          .error(function () {
            toastr.error('Failed to load streets for ward', 'Error');
          });
      };

      function scrollToPrintSection() {
        _.defer(function () {
          streetsContainerPos = $('#streetsList').offset().top;
          $("html, body").animate({scrollTop: $('#printCards').offset().top - 110}, 500);
        });
      }

      $scope.onNoAssociations = function () {
        $scope.userHasNoAssociations = true;
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
          if (!_.isEmpty(postcode)) {
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

      $scope.getRatioCanvassed = function (numCanvassed, numVoters) {
        if (numCanvassed === 0 && numVoters === 0) {
          return 100;
        }
        return Math.round(numCanvassed / numVoters * 100);
      };

      function handleErrorResponse(error) {
        if (error) {
          toastr.warning('Unfortunately we do not have data for the selected streets', 'No data');
        } else {
          toastr.error('Failed to contact server');
        }
      }

      $scope.onPrintStreetsMap = function () {
        var streets = selectedStreets();
        if (!_.isEmpty(streets)) {
          showSelectedStreetsInMap(streets);
        } else {
          toastr.warning('Please select some streets', 'No streets selected');
        }
      };

      function showSelectedStreetsInMap(streets) {
        var addresses = extractStreetInfoForGeocoding(streets);
        geoService.reverseLookupAddresses(addresses)
          .success(function (response) {
            showSelectedStreetsOnMap(response);
          })
          .error(function () {
            toastr.error('Sorry we were unable to geolocate all of the streets.', 'Insufficient address data');
          });
      }

      $scope.onPrintSelected = function () {
        printCanvassCard(selectedStreets());
      };

      $scope.onPrintPriority = function () {
        var priority = _.filter($scope.streets, function (s) {
          return s.priority === 3;
        });
        printCanvassCard(priority);
      };

      function selectedStreets() {
        return _.filter($scope.streets, function (street) {
          return street.selected;
        });
      }

      $scope.onPrintAll = function () {
        printCanvassCard($scope.streets);
      };

      $scope.onClearSelections = function () {
        _.each($scope.streets, function (street) {
          street.selected = false;
        });
        $scope.updateSelectedStreets();
      };

      $scope.onSelectPriority = function () {
        _.each($scope.streets, function (street) {
          street.selected = street.priority === 3;
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

      function showSelectedStreetsOnMap(streets) {
        function extractStreet(addressParts) {
          return _.find(addressParts, function (part) {
            return part.types[0] === "route";
          });
        }

        var i = 0;
        var markers = _.map(streets, function (street) {
          if (!_.isEmpty(street.results)) {
            var firstStreet = street.results[0],
              streetName = extractStreet(firstStreet.address_components);
            return {
              id: i++,
              title: !_.isUndefined(streetName) ? streetName.long_name : '',
              longitude: firstStreet.geometry.location.lng,
              latitude: firstStreet.geometry.location.lat,
              show: true
            };
          }
        });

        $scope.map = {
          center: {
            latitude: markers[0].latitude,
            longitude: markers[0].longitude
          },
          markers: markers,
          options: {
            fullscreenControl: true,
            fullscreenControlOptions: {
              position: google.maps.ControlPosition.RIGHT_TOP
            },
            rotateControl: true,
            scaleControl: true
          },
          zoom: 16
        };
        $scope.showFullScreenStreetsMap = true;
        var pageHeight = util.viewportDimensions().height;
        $("#selectedStreetsMap .angular-google-map-container").height(pageHeight * 0.75);
      }

      $scope.onMapMarkerClick = function (marker, eventName, model) {
        model.show = !model.show;
      };

      $scope.onCloseMap = function () {
        $scope.showFullScreenStreetsMap = false;
      };

      function printCanvassCard(streets) {
        if (!_.isEmpty(streets)) {
          electorService.retrievePdfOfElectorsByStreets($scope.wardSearchModel.code, {streets: streets})
            .success(function (response) {
              var file = new Blob([response], {type: 'application/pdf'});
              saveAs(file, createPdfFileName() + '.pdf');
            })
            .error(function (error) {
              handleErrorResponse(error);
            });
        } else {
          toastr.error('Please change your selection', 'No streets selected');
        }
      }

      function createPdfFileName() {
        return $scope.wardSearchModel.name + ' in ' + $scope.constituencySearchModel.name + ' ' + new Date().toISOString();
      }
    }
  );

/**
 * Display map modal
 */
angular
  .module('canvass')
  .controller('mapModalInstanceCtrl', function ($scope, $uibModalInstance, mapData) {
    $scope.mapData = mapData;

    $scope.render = true;
    $("#popupMap .angular-google-map-container").height(500);

    $scope.cancel = function () {
      $uibModalInstance.dismiss('cancel');
    };
  });
