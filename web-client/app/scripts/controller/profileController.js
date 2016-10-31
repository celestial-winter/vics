
angular
  .module('canvass')
  .controller('profileController', function($scope, userService) {
    userService.retrieveCurrentUser()
      .success(function(response) {
        $scope.user = response;
      });
  });
