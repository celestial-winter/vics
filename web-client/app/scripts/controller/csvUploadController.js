angular
  .module('canvass')
  .controller('csvUploadController', function ($scope, userService, util, $timeout, $anchorScroll, $location, constituencyService, wardService) {

    $scope.currentTab = 'users';

    function csvModel() {
      return {
        content: null,
        header: true,
        headerVisible: true,
        separator: ',',
        separatorVisible: true,
        result: null,
        encoding: 'ISO-8859-1'
      };
    }
    $scope.csvUser = csvModel();
    $scope.csvConstituencies = csvModel();
    $scope.csvWards = csvModel();

    // workaround to set file upload style
    function styleFileUploadButton() {
      $timeout(function() {
        $("input").addClass("btn-sm").addClass("btn-danger");
      }, 1);
    }
    styleFileUploadButton();

    $scope.scrollTo = function (tabName) {
      $scope.currentTab = tabName;
      styleFileUploadButton();
    };

    $scope.active = function(path) {
      return $location.hash() === path;
    };

    $scope.onAssociateConstituencies = function() {
      if (!_.isEmpty($scope.csvConstituencies.result)) {
        _.each($scope.csvConstituencies.result, function (elem) {
          constituencyService.associateToUserByUsername(elem.constituency_code, elem.username)
            .success(function () {
              _.forEach($scope.csvConstituencies.result, function(e) {
                if (elem.constituency_code === e.constituency_code) {
                  e.outcome = true;
                }
              });
            })
            .error(function() {
              _.forEach($scope.csvConstituencies.result, function(e) {
                if (elem.constituency_code === e.constituency_code) {
                  e.outcome = false;
                }
              });
            });
        });
      }
    };

    $scope.onAssociateWards = function() {
      if (!_.isEmpty($scope.csvWards.result)) {
        _.each($scope.csvWards.result, function (elem) {
          wardService.associateToUserByUsername(elem.ward_code, elem.username)
            .success(function () {
              _.forEach($scope.csvWards.result, function(e) {
                if (elem.ward_code === e.ward_code) {
                  e.outcome = true;
                }
              });
            })
            .error(function() {
              _.forEach($scope.csvWards.result, function(e) {
                if (elem.ward_code === e.ward_code) {
                  e.outcome = false;
                }
              });
            });
        });
      }
    };

    $scope.onCreateUsers = function () {
      if (!_.isEmpty($scope.csvUser.result)) {
        _.each($scope.csvUser.result, function (elem) {
          var modelWithPassword = mapUsersToRequest(elem);

          userService.create(modelWithPassword)
            .success(function () {
              var found = _.find($scope.csvUser.result, {username: modelWithPassword.username});
              found.outcome = modelWithPassword.password;
            })
            .error(function (e) {
              var found = _.find($scope.csvUser.result, {username: modelWithPassword.username});
              if (e && e.message) {
                found.outcome = "User already exists";
              } else {
                found.outcome = "Failed. Check server log for errors";
              }
              if (!$scope.$$phase) {
                $scope.$apply();
              }
            });
        });
      }
    };

    function mapUsersToRequest(model) {
      var password = util.generatePassword();
      return {
        username: model.username,
        firstName: model.first_name,
        lastName: model.last_name,
        password: password,
        repeatPassword: password,
        role: "USER",
        writeAccess: model.write_access ? true : false
      };
    }
  });
