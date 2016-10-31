angular
  .module('canvass')
  .controller('associationsController', function ($routeParams, $scope, $location, userService, constituencyService, wardService, permissionService, toastr) {
    var searchLimit = 50;

    function loadUser() {
      userService.findByID($routeParams.id)
        .success(function (response) {
          setPermissionsSwitches(response);
        })
        .error(function () {
          $location.path("/dashboard");
        });
    }

    function setPermissionsSwitches(response) {
      $scope.user = response;
      _.each($scope.permissions, function (permission) {
        permission.active = _.some($scope.user.permissions, ['permission', permission.permission]);
      });
    }

    permissionService.permissions()
      .success(function (response) {
        $scope.permissions = response;
        loadUser();
      });

    if (!$routeParams.id) {
      $location.path("/dashboard");
    }

    $scope.onChangePermission = function (model) {
      var permissionID = model.id;
      if (model.active) {
        permissionService.associatePermission(permissionID, $scope.user.id)
          .error(function () {
            toastr.error('Failed to create user privilege', 'Error');
          });
      } else {
        permissionService.removePermission(permissionID, $scope.user.id)
          .error(function () {
            toastr.error('Failed to remove user privilege', 'Error');
          });
      }
    };

    $scope.onConstituencyInputKeypress = function () {
      $scope.invalidConstituency = false;
      constituencyService.search($scope.constituencySearchModel, searchLimit)
        .success(function (response) {
          $scope.constituencies = response;
        });
    };

    $scope.onWardInputKeypress = function () {
      $scope.invalidWard = false;
      wardService.search($scope.wardSearchModel, searchLimit)
        .success(function (response) {
          $scope.wards = response;
        });
    };

    $scope.onAddConstituency = function () {
      clearMessages();
      if ($scope.constituencySearchModel && $scope.constituencySearchModel.id) {
        constituencyService.associateToUser($scope.constituencySearchModel.id, $scope.user.id)
          .success(function () {
            loadUser();
          })
          .error(function () {
            $scope.failedToAssociateUser = true;
          });
      } else {
        $scope.invalidConstituency = true;
      }
      $scope.constituencySearchModel = "";
    };

    $scope.onAddWard = function () {
      clearMessages();

      if ($scope.wardSearchModel && $scope.wardSearchModel.id) {
        wardService.associateToUser($scope.wardSearchModel.id, $scope.user.id)
          .success(function () {
            $scope.wardSearchModel = "";
            loadUser();
          })
          .error(function () {
            $scope.failedToAssociateWard = true;
          });
      } else {
        $scope.invalidWard = true;
      }
    };

    $scope.onDeleteConstituency = function (id) {
      clearMessages();
      constituencyService.removeUserAssociation(id, $scope.user.id)
        .success(function () {
          loadUser();
        })
        .error(function () {
          $scope.failedToDeleteConstituency = true;
        });
    };

    $scope.onDeleteWard = function (id) {
      clearMessages();
      wardService.removeUserAssociation(id, $scope.user.id)
        .success(function () {
          loadUser();
        })
        .error(function () {
          $scope.failedToDeleteWard = true;
        });
    };

    function clearMessages() {
      $scope.invalidWard = false;
      $scope.invalidConstituency = false;
      $scope.failedToDeleteConstituency = false;
      $scope.failedToDeleteWard = false;
    }
  });
