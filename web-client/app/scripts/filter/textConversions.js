/**
 * Filter to perform text presentation layer transformations
 */
angular
  .module('canvass')
  .filter('wardFormat', function () {
    return function (ward) {
      if (_.isEmpty(ward)) {
        return '';
      }
      return ward.wardCode + ' - ' + ward.wardName;
    };
  })
  .filter('userRoleLabel', function () {
    return function (user) {
      if (_.isEmpty(user)) {
        return '';
      }
      if (user.role === 'ADMIN') {
        return 'Administrator';
      } else {
        return '';
      }
    };
  })
  .filter('voterAddressFormat', function() {
    return function(voter) {
      if (voter && voter.address) {
        var address = voter.address;
        var parts = [address.line_1, address.line_2, _.capitalize(address.post_town)];
        var presentParts =_.filter(parts, function(part) {
          return !_.isEmpty(part);
        });
        return presentParts.join(", ");
      } else {
        return '';
      }
    };
  })
  .filter('ernToShortForm', function() {
    return function (longForm) {
      if (_.isString(longForm)) {
        var parts = longForm.split("-");
        parts.shift();
        return parts.join("-");
      }
    };
  })
  .filter('streetSingleLineFilter', function () {
    function toTitleCase(str) {
      if (!str) {
        return '';
      }
      return str.replace(/\w\S*/g, function (txt) {
        return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();
      });
    }

    return function (address) {
      if (_.isEmpty(address)) {
        return '';
      }
      var parts = [address.mainStreet, address.dependentStreet, address.dependentLocality, toTitleCase(address.postTown)];
      return _
        .chain(parts)
        .filter(function (part) {
          return !_.isEmpty(part);
        })
        .join(', ')
        .value();
    };
  });
