angular
  .module('canvass')
  .service('geoService', function (config, $http) {
    var api = {}, apiUrl = config.apiUrl;

    api.reverseLookupAddresses = function (addresses) {
      return $http({
        method: 'POST',
        url: apiUrl + '/geo/addresslookup',
        data: {addresses: addresses},
        withCredentials: true
      });
    };

    api.findWardFromPostCode = function (postCode) {
      return $http({
        method: 'GET',
        url: apiUrl + '/geo/postcode/' + postCode + '/meta',
        withCredentials: true
      });
    };

    api.findPostCodeFromCoordinates = function(lat, lng) {
      return $http({
        method: 'GET',
        url: 'https://postcodes.io/postcodes?lat=' + lat + '&lon=' + lng
      });
    };

    api.constituencyStatsTopoJsonMap = function(region) {
      return $http({
        method: 'GET',
        params: {
          region: region
        },
        url: apiUrl + '/geo/constituency',
        withCredentials: true
      });
    };

    return api;
  });
