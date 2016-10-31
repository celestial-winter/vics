
angular
  .module('canvass')
  .service('calendar', function() {
    var api = {};

    /**
     * Hardcoded weeks of the campaign (might want to calculate this
     * dynamically from a range stored in config).
     */
    api.campaignWeeks = function() {
      return [
        {week: 201613, start: '2016-03-28'},
        {week: 201614, start: '2016-04-04'},
        {week: 201615, start: '2016-04-11'},
        {week: 201616, start: '2016-04-18'},
        {week: 201617, start: '2016-04-25'},
        {week: 201618, start: '2016-05-02'},
        {week: 201619, start: '2016-05-09'},
        {week: 201620, start: '2016-05-16'},
        {week: 201621, start: '2016-05-23'},
        {week: 201622, start: '2016-05-30'},
        {week: 201623, start: '2016-06-06'},
        {week: 201624, start: '2016-06-13'},
        {week: 201625, start: '2016-06-20'},
        {week: 201626, start: '2016-06-27'}
      ];
    };

    return api;
  });
