'use strict';

/**
 * @ngdoc overview
 * @name canvass
 * @description
 * # canvass
 *
 * Main module of the application.
 */
angular
  .module('canvass', [
    'ngAnimate',
    'ngCookies',
    'ngResource',
    'ngRoute',
    'ngSanitize',
    'ngTouch',
    'ui.bootstrap',
    'angular-loading-bar',
    'uiSwitch',
    'rzModule',
    'ngCsvImport',
    'toastr',
    'uiGmapgoogle-maps',
    'nvd3',
    'datatables',
    'datatables.columnfilter',
    'datatables.bootstrap',
    'angulartics', 'angulartics.google.analytics'
  ])
  .constant('config', {
    apiUrl: 'http://localhost:18080/api/canvass',
    supportEmail: 'vicssupport@voteleave.uk'
  })
  .run(function ($rootScope, config) {
    $rootScope.supportEmail = config.supportEmail;
  })
  .config(function ($routeProvider, $httpProvider, $analyticsProvider) {
    $analyticsProvider.virtualPageviews(false);

    var authByRoute = [
      {route: '/dashboard', role: 'USER'},
      {route: '/canvass', role: 'USER'},
      {route: '/profile', role: 'USER'},
      {route: '/recordvote', role: 'USER'},
      {route: '/gotv', role: 'USER'},
      {route: '/gotpv', role: 'USER'},
      {route: '/admin', role: 'ADMIN'},
      {route: '/associations', role: 'ADMIN'},
      {route: '/users', role: 'ADMIN'},
      {route: '/csvupload', role: 'ADMIN'},
      {route: '/map', role: 'USER'},
      {route: '/stats', role: 'ADMIN'}
    ];

    /**
     * Reusable auth check function, that will check if a user is logged in before a route
     * can be accessed. This check will use a web service to validate that a user has a certain
     * role before displaying the page.  Any future api authentication is also checked on the
     * server using session tokens.
     */
    var userAuth = function ($q, authService, $location, $rootScope) {
      var deferred = $q.defer(),
        route = _.find(authByRoute, function (route) {
          return _.startsWith($location.path(), route.route);
        });

      authService.test(route.role)
        .then(function (response) {
          $rootScope.currentUser = response.data;
          deferred.resolve(response);
        })
        .catch(function () {
          $rootScope.currentUser = null;
          deferred.reject();
          if ($location.path() === '/dashboard') {
            $location.path('/login');
          } else {
            $location.path('/dashboard');
          }

        });
      return deferred.promise;
    };

    $routeProvider
      .when('/dashboard', {
        templateUrl: 'views/dashboard.html',
        controller: 'dashboardController',
        resolve: {
          auth: userAuth
        }
      })
      .when('/canvass', {
        templateUrl: 'views/canvass.html',
        controller: 'canvassGeneratorController',
        resolve: {
          auth: userAuth
        }
      })
      .when('/admin', {
        templateUrl: 'views/stats.html',
        resolve: {
          auth: userAuth
        }
      })
      .when('/login', {
        templateUrl: 'views/login.html',
        controller: 'loginController'
      })
      .when('/resetpassword', {
        templateUrl: 'views/resetpassword.html',
        controller: 'resetPasswordController'
      })
      .when('/newpassword', {
        templateUrl: 'views/newpassword.html',
        controller: 'newPasswordController'
      })
      .when('/map', {
        templateUrl: 'views/map.html',
        resolve: {
          auth: userAuth
        }
      })
      .when('/users', {
        templateUrl: 'views/users.html',
        controller: 'adminUserController',
        resolve: {
          auth: userAuth
        }
      })
      .when('/gotv', {
        templateUrl: 'views/gotv.html',
        controller: 'gotvController',
        resolve: {
          auth: userAuth
        }
      })
      .when('/gotpv', {
        templateUrl: 'views/gotpv.html',
        controller: 'gotpvController',
        resolve: {
          auth: userAuth
        }
      })
      .when('/recordvote', {
        templateUrl: 'views/recordvote.html',
        controller: 'recordVoteController',
        resolve: {
          auth: userAuth
        }
      })
      .when('/canvassinput', {
        templateUrl: 'views/canvassinput.html',
        controller: 'canvassInputController',
        resolve: {
          auth: userAuth
        }
      })
      .when('/associations/:id', {
        templateUrl: 'views/associations.html',
        controller: 'associationsController',
        resolve: {
          auth: userAuth
        }
      })
      .when('/profile', {
        templateUrl: 'views/profile.html',
        controller: 'profileController',
        resolve: {
          auth: userAuth
        }
      })
      .when('/csvupload', {
        templateUrl: 'views/csvupload.html',
        controller: 'csvUploadController',
        resolve: {
          auth: userAuth
        }
      })
      .when('/stats', {
        templateUrl: 'views/stats.html',
        controller: 'statsController',
        resolve: {
          auth: userAuth
        }
      })
      .when('/admin', {
        templateUrl: 'views/admin-dashboard.html',
        controller: 'adminDashboardController',
        resolve: {
          auth: userAuth
        }
      })
      .otherwise({
        redirectTo: '/dashboard'
      });

    var interceptor = ['$rootScope', '$q', '$location', function ($rootScope, $q, $location) {
      function success(response) {
        return response;
      }

      function error(response) {
        var status = response.status;
        if (status === 401 || status === 403) {
          return $location.path('/login');
        } else {
          return $q.reject(response);
        }
      }

      return function (promise) {
        return promise.then(success, error);
      };
    }];
    $httpProvider.interceptors.push(interceptor);
  });
