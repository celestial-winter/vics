angular
  .module('canvass')
  .service('statsService', function ($http, config) {
    var api = {},
      apiUrl = config.apiUrl,
      defaultLeaderboardLimit = 6;

    api.allStats = function () {
      return $http({
        method: 'GET',
        url: apiUrl + '/stats',
        withCredentials: true
      });
    };

    api.topCanvassers = function (limit) {
      var l = limit ? limit : defaultLeaderboardLimit;
      return $http({
        method: 'GET',
        url: apiUrl + '/stats/topcanvassers?limit=' + l,
        withCredentials: true
      });
    };

    api.topWards = function (limit) {
      var l = limit ? limit : defaultLeaderboardLimit;
      return $http({
        method: 'GET',
        url: apiUrl + '/stats/topwards?limit=' + l,
        withCredentials: true
      });
    };

    api.userCounts = function () {
      return $http({
        method: 'GET',
        url: apiUrl + '/stats/users',
        withCredentials: true,
        cache: true
      });
    };

    api.topConstituencies = function (limit) {
      var l = limit ? limit : defaultLeaderboardLimit;
      return $http({
        method: 'GET',
        url: apiUrl + '/stats/topconstituencies?limit=' + l,
        withCredentials: true
      });
    };

    api.canvassedByWeekAndWard = function(code) {
      return $http({
        method: 'GET',
        url: apiUrl + '/stats/ward/' + code + '/weekly',
        withCredentials: true,
        cache: true
      });
    };

    api.canvassedByWeekAndConstituency = function(code) {
      return $http({
        method: 'GET',
        url: apiUrl + '/stats/constituency/' + code + '/weekly',
        withCredentials: true,
        cache: true
      });
    };

    api.wardStats = function(wardCode) {
      return $http({
        method: 'GET',
        url: apiUrl + '/stats/ward/' + wardCode,
        withCredentials: true
      });
    };

    api.adminStats = function() {
      return $http({
        method: 'GET',
        url: apiUrl + '/stats/admin',
        withCredentials: true
      });
    };

    api.constituencyStats = function(constituencyCode) {
      return $http({
        method: 'GET',
        url: apiUrl + '/stats/constituency/' + constituencyCode,
        withCredentials: true
      });
    };

    api.constituenciesStats = function() {
      return $http({
        method: 'GET',
        url: apiUrl + '/stats/constituencies',
        withCredentials: true
      });
    };

    return api;
  });
