angular
  .module('canvass')
  .service('voteService', function (config, $http, util) {
    var api = {},
      apiUrl = config.apiUrl;

    api.recordVote = function (wardCode, ern) {
      var fullErn = util.ernShortToLongFormConverter(wardCode, ern);
      return $http({
        method: 'POST',
        url: apiUrl + '/elector/' + fullErn + '/voted',
        withCredentials: true
      });
    };

    api.undoVote = function(fullErn) {
      return $http({
        method: 'DELETE',
        url: apiUrl + '/elector/' + fullErn + '/voted',
        withCredentials: true
      });
    };

    api.wontVote = function(wardCode, ern) {
      var fullErn = util.ernShortToLongFormConverter(wardCode, ern);
      return $http({
        method: 'POST',
        url: apiUrl + '/elector/' + fullErn + '/wontvote',
        withCredentials: true
      });
    };

    api.undoWontVote = function (ern, contactId) {
      return $http({
        url: apiUrl + '/elector/' + ern + '/wontvote/' + contactId,
        method: 'DELETE',
        withCredentials: true
      });
    };

    return api;
  });
