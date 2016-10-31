/**
 * Service to retrieve wards and constituencies
 */
angular
  .module('canvass')
  .service('wardService', function ($http, config) {
    var api = {},
      apiUrl = config.apiUrl;

    /**
     * Finds the wards within constituency
     * @param {String} id the constituency id
     */
    api.findWardsWithinConstituency = function (id) {
      return $http({
        method: 'GET',
        url: apiUrl + '/ward/constituency/' + id,
        withCredentials: true
      });
    };

    api.findStreetsByWard = function(wardCode) {
      return $http({
        method: 'GET',
        url: apiUrl + '/ward/' + wardCode + '/street',
        withCredentials: true
      });
    };

    /**
     * Finds all wards (the server will restrict the result set to
     * the wards the current user has access to)
     */
    api.findAll = function() {
      return $http({
        method: 'GET',
        url: apiUrl + '/ward',
        withCredentials: true
      });
    };

    api.userHasAssociationsTest = function() {
      return $http({
        method: 'GET',
        url: apiUrl + '/ward/test',
        withCredentials: true
      });
    };

    /**
     * Finds all wards in short format without associated objects like constituency
     * (the server will restrict the result set to
     * the wards the current user has access to)
     */
    api.findAllSummarized = function() {
      return $http({
        method: 'GET',
        url: apiUrl + '/ward?summary=true',
        withCredentials: true
      });
    };

    /**
     * Search wards by name (server performs a contains string search ignoring case)
     * @param {String} name - the ward name to search
     * @param {Number} limit - number of params to return
     */
    api.search = function(name, limit) {
      var paramsEncoded = $.param({
        limit: limit,
        name: name
      });

      return $http({
        method: 'GET',
        url: apiUrl + '/ward/search?' + paramsEncoded,
        withCredentials: true
      });
    };

    api.searchRestricted = function(name) {
      return $http({
        method: 'GET',
        url: apiUrl + '/ward/search/restricted?q=' + name,
        withCredentials: true
      });
    };

    api.associateToUser = function(wardID, userID) {
      return $http({
        method: 'POST',
        url: apiUrl + '/ward/' + wardID + '/user/' + userID,
        withCredentials: true
      });
    };

    api.associateToUserByUsername = function(wardCode, username) {
      return $http({
        method: 'POST',
        url: apiUrl + '/ward/associate',
        data: {
          wardCode: wardCode,
          username: username
        },
        withCredentials: true
      });
    };

    api.firstUserWard = function() {
      return $http({
        method: 'GET',
        url: apiUrl + '/ward/restricted?limit=1',
        withCredentials: true
      });
    };

    api.removeUserAssociation = function(wardID, userID) {
      return $http({
        method: 'DELETE',
        url: apiUrl + '/ward/' + wardID + '/user/' + userID,
        withCredentials: true
      });
    };

    return api;
  });
