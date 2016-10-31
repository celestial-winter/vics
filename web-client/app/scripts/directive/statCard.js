/**
 * Reusable UI component that displays health bars
 */
angular
    .module('canvass')
    .directive('statCard', function() {
        return {
            restrict: 'EA',
            templateUrl: 'views/partials/stat-card.html',
            scope: {
              label: '@',
              count: '@',
              meta: '@',
              metaTooltip: '@'
            },
            link: function() {
            }
        };
    });
