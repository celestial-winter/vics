angular
  .module('canvass')
.controller('dashboardController', function ($interval, $scope, statsService, toastr, geoService, calendar, $uibModal, wardService, constituencyService, $analytics) {
  $analytics.pageTrack('/dashboard');

  var referendumDate = new Date(2016, 5, 23, 22, 0),
      secondsInDay = 86400,
      secondsInHour = 3600,
      leaderboardMoreLimit = 50,
      countdownHandle;
    $scope.showCanvassedGraph = true;
    $scope.hideCharts = true;

    $scope.stats = {};

    $scope.onWardInputKeypress = function () {
      wardService.searchRestricted($scope.wardSearchModel)
        .success(function (response) {
          $scope.wards = response;
        });
    };

    $scope.onConstituencyInputKeypress = function () {
      constituencyService.searchRestricted($scope.constituencySearchModel)
        .success(function (response) {
          $scope.constituencies = response;
        });
    };

    $scope.showTop50Activists = function () {
      $scope.topMore = [];

      statsService
        .topCanvassers(leaderboardMoreLimit)
        .success(function (response) {
          $scope.stats.topMore = response;
        });
    };

    $scope.showTop50Constituencies = function () {
      $scope.topMore = [];

      statsService
        .topConstituencies(leaderboardMoreLimit)
        .success(function (response) {
          $scope.stats.topMore = response;
        });
    };

    $scope.showTop50Wards = function () {
      $scope.topMore = [];

      statsService
        .topWards(leaderboardMoreLimit)
        .success(function (response) {
          $scope.stats.topMore = response;
        });
    };

    $scope.currentTab = 'activists';
    $scope.constituencyName = '';
    $scope.wardName = '';
    $scope.showPledgesPieChart = false;
    $scope.graphLabel = '';

    $scope.map = {
      center: {
        latitude: 52.400251,
        longitude: -1.4497
      },
      control: {
        zoomControl: true,
        mapTypeControl: false,
        scaleControl: false,
        streetViewControl: false,
        rotateControl: false,
        fullscreenControl: false
      },
      zoom: 12
    };
    $scope.markers = [];

    $scope.$on('$viewContentLoaded', function () {
      $("#postcodeMap .angular-google-map-container").height(250);
    });

    statsService
      .topCanvassers()
      .success(function (response) {
        $scope.stats.topCanvassers = response;
      });

    constituencyService.firstUserConstituency()
      .success(function (response) {
        if (!_.isEmpty(response.constituencies)) {
          $scope.constituencySearchModel = response.constituencies[0];
          $scope.onSetConstituency();
          $scope.hideCharts = false;
        } else {
          wardService.firstUserWard()
            .success(function (response) {
              if (!_.isEmpty(response.wards)) {
                $scope.wardSearchModel = response.wards[0];
                $scope.onSetWard();
                $scope.hideCharts = false;
              }
            });
        }
      });

    $scope.changeLeaderboardTab = function (tabName) {
      $scope.currentTab = tabName;
      if (tabName === 'wards' && _.isUndefined($scope.stats.topWards)) {
        loadTopWards();
      } else if (tabName === 'constituencies' && _.isUndefined($scope.stats.topConstituencies)) {
        loadTopConstituencies();
      }
    };

    function loadTopWards() {
      statsService
        .topWards()
        .success(function (response) {
          $scope.stats.topWards = response;
        });
    }

    function loadTopConstituencies() {
      statsService
        .topConstituencies()
        .success(function (response) {
          $scope.stats.topConstituencies = response;
        });
    }

    $scope.findWard = function (postCode, suppressError) {
      if (_.isEmpty(postCode)) {
        return;
      }

      geoService.findWardFromPostCode(postCode)
        .success(function (response) {
          if (response && response.electoral_ward_name) {
            $scope.constituencyName = response.parliamentary_constituency_name;
            $scope.wardName = response.electoral_ward_name;
          }

          $scope.map = {
            center: {
              latitude: response.latitude_etrs89,
              longitude: response.longitude_etrs89
            },
            zoom: 12
          };
          $scope.markers[0] = {id: 1, latitude: response.latitude_etrs89, longitude: response.longitude_etrs89};
        })
        .error(function () {
          if (!suppressError) {
            toastr.info('We could not find a location for that post code', 'Sorry');
          }
        });
    };

    /**
     * Attempt to set the area to the users location
     */
    function getCurrentLocation() {
      var options = {
        enableHighAccuracy: true
      };

      if (navigator && navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(function (pos) {
            $scope.position = new google.maps.LatLng(pos.coords.latitude, pos.coords.longitude);
            var point = JSON.parse(JSON.stringify($scope.position));
            if (_.isObject(point)) {
              $scope.map.center = {
                latitude: point.lat,
                longitude: point.lng
              };
              geoService.findPostCodeFromCoordinates(point.lat, point.lng)
                .success(function (response) {
                  var result = _.head(response.result);
                  if (result) {
                    $scope.postCode = result.postcode;
                    $scope.findWard(result.postcode, true);
                  }
                });
            }
          },
          function () {
          }, options);
      }
    }

    $scope.sumIntentions = function (intentions) {
      var sum = 0;
      _.forOwn(intentions, function (val) {
        sum += val;
      });
      return sum;
    };

    $scope.onSetWard = function () {
      if ($scope.wardSearchModel && $scope.wardSearchModel.id) {

        statsService.wardStats($scope.wardSearchModel.code)
          .success(function (response) {
            $scope.statsTable = response;
            _.defer(function () {
              var percentVoted = _.parseInt($scope.statsTable.voted.pledged / $scope.statsTable.pledged * 100);
              $scope.pieData = [
                {
                  key: "Pledges Voted",
                  y: percentVoted
                },
                {
                  key: "Pledges Not Voted",
                  y: 100 - percentVoted
                }
              ];
            });
          });

        statsService.canvassedByWeekAndWard($scope.wardSearchModel.code)
          .success(function (response) {
            $scope.showCanvassedGraph = true;
            var data = [{
              key: "Total Canvassed",
              values: mapWeeklyCanvassStatsToCalendar(response)
            }];
            _.defer(function () {
              $scope.api.updateWithData(data);
            });
            $scope.graphLabel = $scope.wardSearchModel.name;
            $scope.wardSearchModel = null;
          });
      } else {
        $scope.invalidWard = true;
      }
    };

    $scope.onSetConstituency = function () {
      if ($scope.constituencySearchModel && $scope.constituencySearchModel.id) {

        statsService.constituencyStats($scope.constituencySearchModel.code)
          .success(function (response) {
            $scope.statsTable = response;
            _.defer(function () {
              var percentVoted = _.parseInt($scope.statsTable.voted.pledged / $scope.statsTable.pledged * 100);
              $scope.pieData = [
                {
                  key: "Pledges Voted",
                  y: percentVoted
                },
                {
                  key: "Pledges Not Voted",
                  y: 100 - percentVoted
                }
              ];
            });
          });

        statsService.canvassedByWeekAndConstituency($scope.constituencySearchModel.code)
          .success(function (response) {
            $scope.showCanvassedGraph = true;
            var data = [{
              key: "Total Canvassed",
              values: mapWeeklyCanvassStatsToCalendar(response)
            }];
            _.defer(function () {
              $scope.api.updateWithData(data);
            });
            $scope.graphLabel = $scope.constituencySearchModel.name;
            $scope.constituencySearchModel = null;
          });
      } else {
        $scope.invalidConstituency = true;
      }
    };

    function updateCountdown() {
      var now = new Date();
      if (now.getTime() > referendumDate.getTime()) {
        resetClock();
      } else {
        // get total seconds between the times
        var delta = Math.abs(referendumDate - now) / 1000;

        // calculate (and subtract) whole days
        $scope.days = Math.floor(delta / secondsInDay);
        delta -= $scope.days * secondsInDay;

        // calculate (and subtract) whole hours
        $scope.hours = Math.floor(delta / secondsInHour) % 24;
        delta -= $scope.hours * secondsInHour;

        // calculate (and subtract) whole minutes
        $scope.minutes = Math.floor(delta / 60) % 60;
        delta -= $scope.minutes * 60;

        // what's left is seconds
        $scope.seconds = delta % 60;
      }
    }

    function resetClock() {
      $scope.days = 0;
      $scope.hours = 0;
      $scope.minutes = 0;
      $scope.seconds = 0;
      $interval.cancel(countdownHandle);
    }

    updateCountdown();
    countdownHandle = $interval(updateCountdown, 250, 0, true);
    $scope.$on('$destroy', function () {
      $interval.cancel(countdownHandle);
    });

    getCurrentLocation();

    function mapWeeklyCanvassStatsToCalendar(canvassedByWeek) {
      var stats = _.map(canvassedByWeek, function (e) {
        return {
          count: _.parseInt(e[0]),
          week: _.parseInt(e[1])
        };
      });

      return calendar.campaignWeeks().map(function (calendarWeek) {
        var currWeek = _.find(stats, function (stat) {
          return stat.week === calendarWeek.week;
        });
        return {
          week: ('' + calendarWeek.week).substring(4),
          start: calendarWeek.start,
          count: _.isUndefined(currWeek) ? 0 : currWeek.count
        };
      });
    }

    $scope.onShowWeeklyStatsConfig = function () {
      $scope.showCanvassedGraph = !$scope.showCanvassedGraph;
    };

    $scope.onShowPledgesPieChart = function () {
      $scope.showPledgesPieChart = !$scope.showPledgesPieChart;
    };

    $scope.pieOptions = {
      chart: {
        type: 'pieChart',
        donut: true,
        donutRatio: ".4",
        showLabels: true,
        labelsOutside: true,
        labelType: "percent",
        color: [
          "#C5443B",
          "#e5e6e6"
        ],
        height: 250,
        x: function (d) {
          return d.key;
        },
        y: function (d) {
          return d.y;
        },
        duration: 500,
        labelThreshold: 0.01,
        legend: {
          margin: {
            top: 5,
            right: 35,
            bottom: 5,
            left: 0
          }
        }
      }
    };

    $scope.options = {
      chart: {
        type: 'discreteBarChart',
        height: 250,
        color: [
          "#C5443B"
        ],
        x: function (d) {
          return d.week;
        },
        y: function (d) {
          return d.count;
        },
        tooltip: {
          contentGenerator: function (e) {
            var series = e.series[0];
            if (series.value === null) {
              return;
            }

            if (e.series) {
              var rows =
                "<tr>" +
                "<td class='key'>" + 'Start: ' + "</td>" +
                "<td class='x-value'>" + e.data.start + "</td>" +
                "</tr>" +
                "<tr>" +
                "<td class='key'>" + 'Voter Contacts: ' + "</td>" +
                "<td class='x-value'><strong>" + series.value + "</strong></td>" +
                "</tr>";

              var header =
                "<thead>" +
                "<tr>" +
                "<td class='legend-color-guide'><div style='background-color: " + series.color + ";'></div></td>" +
                "<td class='key'>Week&nbsp;<strong>" + e.data.week + "</strong></td>" +
                "</tr>" +
                "</thead>";

              return "<table>" +
                header +
                "<tbody>" +
                rows +
                "</tbody>" +
                "</table>";
            }
          }
        },
        useInteractiveGuideline: true,
        showValues: false,
        xAxis: {
          axisLabel: 'Week'
        },
        yAxis: {
          axisLabel: 'Voter Contacts',
          axisLabelDistance: -10,
          tickFormat: _.identity
        }
      }
    };
  });
