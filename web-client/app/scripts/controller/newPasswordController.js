
angular
  .module('canvass')
  .controller('newPasswordController', function ($scope, resetPasswordService) {
    $scope.onResetPassword = function() {
      $scope.invalidCredentials = false;
      resetPasswordService.generatePasswordFromToken($scope.username, $scope.token)
        .success(function(response) {
          $scope.password = response.password;
          $scope.showNewCredentials = true;
        })
        .error(function() {
          $scope.invalidCredentials = true;
        });
    };
  });
