
angular
  .module('canvass')
  .service('resetPasswordService', function ($http, config) {
    var api = {},
      apiUrl = config.apiUrl;

    api.resetPassword = function (username) {
      return $http({
        method: 'POST',
        url: apiUrl + '/user/passwordreset',
        data: {
          username: username
        }
      });
    };

    api.generatePasswordFromToken = function (username, token) {
      return $http({
        method: 'POST',
        url: apiUrl + '/user/generatepassword',
        data: {
          username: username,
          token: token
        }
      });
    };

    return api;
  });
