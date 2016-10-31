
angular
  .module('canvass')
  .service('permissionService', function(config, $http) {
  var api = {},
    apiUrl = config.apiUrl;

    api.permissions = function() {
      return $http({
        url: apiUrl + '/privilege',
        method: 'GET',
        withCredentials: true
      });
    };

    api.associatePermission = function(permissionID, userID) {
      return $http({
        url: apiUrl + '/privilege/' + permissionID + '/user/' + userID,
        method: 'POST',
        withCredentials: true
      });
    };

    api.removePermission = function(permissionID, userID) {
      return $http({
        url: apiUrl + '/privilege/' + permissionID + '/user/' + userID,
        method: 'DELETE',
        withCredentials: true
      });
    };

    return api;
  });
