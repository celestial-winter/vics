/**
 * Service to query the health of external systems
 */
angular
    .module('canvass')
    .service('healthService', function(config, $http) {
        var api = {}, 
          apiUrl = config.apiUrl;

        /**
         * Gets the health of the api
         * @returns {Promise<Object>} the health status
         */
        api.retrieveOwnHealthStatus = function() {
          return $http({
            method: 'GET',
            url: apiUrl + '/health',
            withCredentials: true
          });
        };

        return api;
    });
