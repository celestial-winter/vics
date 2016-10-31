
/**
 * Directive that searches constituencies then wards based on user input bound to a model
 * (uses a typeahead internally)
 */
angular
  .module('canvass')
  .directive('wardSearch', function (constituencyService, wardService, $timeout) {
    return {
      templateUrl: 'views/partials/wardsearch.html',
      restrict: 'EA',
      scope: {
        onSelectWard: '&',
        onSelectConstituency: '&',
        onLoadedConstituencies: '&'
      },
      link: function (scope) {
        scope.onSelectWard = scope.onSelectWard();
        scope.onSelectConstituency = scope.onSelectConstituency();
        scope.onLoadedConstituencies = scope.onLoadedConstituencies();

        // preload the constituencies associated to the active user
        constituencyService.retrieveByUser()
          .success(function (response) {
            scope.constituencies = response.constituencies;
            scope.onLoadedConstituencies(scope.constituencies);
          });

        /**
         * Reloads wards when a constituency typeahead is matched
         */
        scope.onSelectConstituencyInternal = function () {
          scope.directiveModel.ward = '';
          wardService.findWardsWithinConstituency(scope.directiveModel.constituency.id)
            .success(function (response) {
              scope.wards = response.wards;
              scope.onSelectConstituency(scope.directiveModel.constituency);
            });
          $timeout(function() {
            $('#wardInputID').focus();
          }, 100);
        };

        /**
         * Triggered when the user selects searched
         */
        scope.onSelectWardInternal = function() {
          if (scope.directiveModel.ward && scope.directiveModel.ward.id) {
            scope.onSelectWard(scope.directiveModel);
          }
        };

      }
    };
  });
