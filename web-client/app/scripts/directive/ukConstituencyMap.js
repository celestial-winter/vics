angular
  .module('canvass')
  .directive('ukConstituencyMap', function (geoService) {
    return {
      restrict: 'E',
      templateUrl: 'views/partials/map.html',
      link: function (scope) {
        scope.legend = {};
        var scheme = ['#fee5d9', '#fcbba1', '#fc9272', '#fb6a4a', '#ef3b2c', '#cb181d', '#99000d', '#6F000A'],
          turqoise = '#0aa89e';

        function createLegend() {
          var colors = d3.scale.quantize()
            .range(scheme);
          var legend = d3.select('#scale')
            .append('ul')
            .attr('class', 'list-inline');

          var keys = legend.selectAll('li.key')
            .data(colors.range());

          var keyIdx = 0;
          keys.enter().append('li')
            .attr('class', 'key')
            .style('border-top-color', String)
            .text(function () {
              keyIdx += 1;
              if (keyIdx === 1) {
                return '> 0';
              } else if (keyIdx === 2) {
                return 50;
              } else if (keyIdx === 3) {
                return 100;
              } else if (keyIdx === 4) {
                return 250;
              } else if (keyIdx === 5) {
                return 500;
              } else if (keyIdx === 6) {
                return 750;
              } else if (keyIdx === 7) {
                return 1000;
              } else {
                return '2500+';
              }
            });
        }

        geoService
          .constituencyStatsTopoJsonMap('gb')
          .success(function (response) {
            createLegend();

            var map = new google.maps.Map(document.getElementById('map'), {
              zoom: 7,
              zoomControl: true,
              mapTypeControl: false,
              streetViewControl: false,
              fullscreenControl: true,
              zoomControlOptions: {
                position: google.maps.ControlPosition.RIGHT_TOP
              },
              center: {lat: 52.5, lng: -2.0}
            });

            function styleMap() {
              map.set('styles', [
                {
                  featureType: 'all',
                  elementType: 'all',
                  stylers: [
                    {visibility: 'off'}
                  ]
                }
              ]);
            }

            styleMap();
            map.data.addGeoJson(response);
            var legend = document.getElementById('legend');
            legend.index = 1;
            map.controls[google.maps.ControlPosition.RIGHT_TOP].push(legend);

            map.data.setStyle(function (feature) {
              console.log(feature);
              return {
                fillColor: mapCountToFillColour(feature.f.count),
                strokeColor: 'gray',
                strokeWeight: 0.5,
                fillOpacity: 1
              };
            });

            map.data.addListener('mouseover', function (event) {
              scope.legend.constituency = event.feature.f.PCON13NM;
              scope.legend.canvassed = event.feature.f.count;
              if (!scope.$$phase) {
                scope.$apply();
              }

              map.data.revertStyle();
              map.data.overrideStyle(event.feature, {
                fillColor: turqoise,
                fillOpacity: 0.8
              });
            });

            map.data.addListener('mouseout', function () {
              map.data.revertStyle();
            });

          });

        /**
         * Scales based on http://colorbrewer2.org/
         */
        function mapCountToFillColour(count) {
          if (count === 0) {
            return '#ffffff';
          } else if (count > 0 && count <= 50) {
            return '#fee5d9';
          } else if (count > 50 && count <= 100) {
            return '#fcbba1';
          } else if (count > 100 && count <= 250) {
            return '#fc9272';
          } else if (count > 250 && count <= 500) {
            return '#fb6a4a';
          } else if (count > 500 && count <= 750) {
            return '#ef3b2c';
          } else if (count > 750 && count <= 1000) {
            return '#cb181d';
          } else if (count > 1000 && count <= 2500) {
            return '#99000d';
          } else if (count > 2500) {
            return '#6F000A';
          }
        }
      }
    };
  });
