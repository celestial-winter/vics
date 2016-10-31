/**
 * Root controller of the application
 */
angular
  .module('canvass')
  .controller('parentController', function ($location, $scope, authService, $rootScope) {
    $rootScope.showPasswordReset = false;

    function resetMenu() {
      // Added to enforce menus are removed on logout
      if (!$scope.$$phase) {
        $scope.$apply();
      }
    }

    resetMenu();

    $scope.logout = function () {
      authService.logout()
        .finally(function () {
          $location.path('/login');
          _.defer(function () {
            resetMenu();
            $rootScope.currentUser = null;
          });
        });
    };

    $scope.hasPermission = function(permission) {
      if (!$rootScope.currentUser) {
        return false;
      } else {
        return _.some($rootScope.currentUser.permissions, function(p) {
          return p.permission === permission;
        });
      }
    };

    // set the margins depending on the page type
    var fullPageRoutes = ['/login', '/resetpassword', '/newpassword'];
    $scope.$on('$routeChangeStart', function () {
      var currentRoute = $location.path(),
        isFullPage = false;

      $rootScope.showPasswordReset = _.startsWith(currentRoute, '/resetpassword') ||
        _.startsWith(currentRoute, '/newpassword');

      _.forEach(fullPageRoutes, function (fullPageRoute) {
        if (_.startsWith(currentRoute, fullPageRoute)) {
          isFullPage = true;
        }
      });

      if (isFullPage) {
        $('#base').attr('class', 'fullPageMargins');
      } else {
        $('#base').removeClass('fullPageMargins');
      }
      resetMenu();
    });
  });
