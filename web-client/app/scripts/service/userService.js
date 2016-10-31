angular
  .module('canvass')
  .service('userService', function ($http, config) {
    var api = {}, 
      apiUrl = config.apiUrl;

    api.retrieveAllUsers = function() {
      return $http({
        url: apiUrl + '/user',
        method: 'GET',
        withCredentials: true
      });
    };

    api.retrieveCurrentUser = function() {
      return $http({
        url: apiUrl + '/user/current',
        method: 'GET',
        withCredentials: true
      });
    };

    api.create = function(user) {
      return $http({
        url: apiUrl + '/user',
        method: 'POST',
        withCredentials: true,
        data: user
      });
    };

    api.findByID = function(id) {
      return $http({
        url: apiUrl + '/user/' + id,
        method: 'GET',
        withCredentials: true
      });
    };

    api.delete = function(userID) {
      return $http({
        url: apiUrl + '/user/' + userID,
        method: 'DELETE',
        withCredentials: true
      });
    };

    api.update = function(user) {
      return $http({
        url: apiUrl + '/user/' + user.id,
        method: 'PUT',
        data: user,
        withCredentials: true
      });
    };

    return api;
  });
