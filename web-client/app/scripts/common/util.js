angular
  .module('canvass')
  .service('util', function () {
    var api = {},
      passwordLength = 18,
      chars = "abcdefghjknopqrstuvwxyz",
      uppers = "ABCDEFGHJKMNPQRSTUVWXYZ",
      numbers = "23456789",
      emailRegex = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/,
      ernRegex = /^\w{1,9}-\w{1,7}-\d{1,7}-\w{1,2}$/;

    api.uuid = function () {
      function s4() {
        return Math.floor((1 + Math.random()) * 0x10000)
          .toString(16)
          .substring(1);
      }

      return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
        s4() + '-' + s4() + s4() + s4();
    };

    api.isValidEmail = function (email) {
      return emailRegex.test(email);
    };

    api.validErn = function (electorID) {
      return electorID && ernRegex.test(electorID);
    };

    api.notEmpty = function (underTest) {
      return !_.isEmpty(underTest);
    };

    api.destructureErn = function(longFormErn) {
      var ern = longFormErn.split("-");
      return {
        wardCode: ern[0],
        pollingDistrict: ern[1],
        number: _.parseInt(ern[2]),
        suffix: ern[3]
      };
    };

    api.ernShortToLongFormConverter = function(wardCode, ernShortForm) {
      return wardCode + "-" + ernShortForm;
    };

    api.ernLongToShortFormConverter = function(ernLongForm) {
      var ern = api.destructureErn(ernLongForm);
      return ern.pollingDistrict + "-" + ern.number + "-" + ern.suffix;
    };

    api.viewportDimensions = function() {
      var w = Math.max(document.documentElement.clientWidth, window.innerWidth || 0),
        h = Math.max(document.documentElement.clientHeight, window.innerHeight || 0);

      return {
        width: w,
        height: h
      };
    };

    api.generatePassword = function () {
      var password = "";
      password += _.sample(chars);
      password += _.sample(uppers);
      password += _.sample(numbers);

      var all = chars + uppers + numbers;
      for (var i = 0; i < passwordLength - 3; ++i) {
        password += _.sample(all);
      }
      return _.shuffle(password).join("");
    };

    return api;
  });
