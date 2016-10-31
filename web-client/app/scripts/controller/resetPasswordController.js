
angular
  .module('canvass')
  .controller('resetPasswordController', function ($scope, resetPasswordService) {
    $scope.onResetPassword = function() {
      $scope.userDoesNotExist = false;
      resetPasswordService.resetPassword($scope.username)
        .success(function() {
          $scope.successfullyRequestedReset = true;
        })
        .error(function(error) {
          if (error && error.type === 'NotFoundFailure') {
            $scope.userDoesNotExist = true;
          }
        });
    };
  });
