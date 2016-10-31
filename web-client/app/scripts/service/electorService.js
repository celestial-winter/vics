angular
  .module('canvass')
  .service('electorService', function (config, $http) {
    var api = {}, apiUrl = config.apiUrl;

    api.retrieveLocalElectorsByWards = function (wardCode) {
      return $http({
        url: apiUrl + '/ward/' + wardCode + '/elector',
        method: 'GET',
        withCredentials: true
      });
    };

    api.retrieveElectorByErn = function (ern) {
      return $http({
        url: apiUrl + '/elector/' + ern,
        method: 'GET',
        withCredentials: true
      });
    };

    api.submitCanvassInput = function (ern, data) {
      return $http({
        url: apiUrl + '/elector/' + ern + '/contact',
        method: 'POST',
        data: data,
        withCredentials: true
      });
    };

    api.undoCanvassInput = function (ern, contactId, localId) {
      return $http({
        url: apiUrl + '/elector/' + ern + '/contact/' + contactId + '/localId/' + localId,
        method: 'DELETE',
        withCredentials: true
      });
    };

    api.search = function (surname, postcode, wardCode) {
      return $http({
        url: apiUrl + '/elector',
        method: 'GET',
        params: {
          surname: surname,
          postcode: postcode,
          wardCode: wardCode
        },
        withCredentials: true
      });
    };

    /**
     * Retrieves the households and electors in the given streets
     */
    api.retrievePdfOfElectorsByStreets = function (wardCode, data, isLabels) {
      var url = isLabels ? apiUrl + '/canvass/ward/' + wardCode + '/gotpv' : apiUrl + '/canvass/ward/' + wardCode;
      return $http({
          url: url,
          method: 'POST',
          responseType: 'arraybuffer',
          headers: {
            accept: 'application/*'
          },
          withCredentials: true,
          data: data,
          transformResponse: function (data, header, status) {
            if (status === 404) {
              return {
                status: 404,
                message: 'No voters for streets'
              };
            }
            return data;
          }
        }
      );
    };

    return api;
  });
