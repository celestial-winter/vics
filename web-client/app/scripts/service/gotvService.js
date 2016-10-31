
angular
  .module('canvass')
  .service('gotvService', function(config, $http) {
    var api = {}, apiUrl = config.apiUrl;

    /**
     * Retrieves the households and electors in the given streets
     */
    api.retrievePdfOfElectorsByStreets = function (wardCode, data, isLabels) {
      var url;
      if (isLabels) {
        url = apiUrl + '/gotv/ward/' + wardCode + '/labels';
      } else {
        url = apiUrl + '/gotv/ward/' + wardCode;
      }
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
