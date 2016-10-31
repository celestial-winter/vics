angular
  .module('canvass')
  .controller('recordVoteController', function (util, $timeout, $scope, RingBuffer, voteService, electorService, wardService, toastr) {
    var logSize = 7;

    $scope.searchResults = null;
    $scope.logs = RingBuffer.newInstance(logSize);

    $scope.formModel = {
      ern: {
        pollingDistrict: '',
        number: '',
        suffix: ''
      },
      selectedWard: null
    };

    $scope.searchForm = {
      surname: '',
      postcode: ''
    };

    $scope.wards = [];

    wardService.findAllSummarized()
      .success(function (response) {
        $scope.wards = response;
        $scope.userHasNoAssociations = _.isEmpty($scope.wards);
        $scope.formModel.selectedWard = $scope.wards[0];
      })
      .finally(function () {
        $scope.contentLoaded = true;
      });

    _.times(logSize, function () {
      $scope.logs.push(emptyRow());
    });

    $scope.onVote = function () {
      var ern = $scope.formModel.ern.pollingDistrict + '-' +
        $scope.formModel.ern.number + '-' +
        $scope.formModel.ern.suffix;
      if (isValidElectorID(ern) &&
        isValidWard($scope.formModel.selectedWard)) {

        voteService.recordVote($scope.formModel.selectedWard.code, ern)
          .success(function (response) {
            $scope.logs.push({
              ern: util.ernLongToShortFormConverter(response.ern),
              fullErn: response.ern,
              reason: 'Voted',
              result: 1
            });
          })
          .error(function (error) {
            console.log(error);

            var reason = "-";
            if (error.type === 'PafApiNotFoundFailure') {
              reason = 'Voter not found';
            }
            $scope.logs.push({
              ern: ern,
              reason: reason,
              result: 0
            });
          });
        $scope.formModel.ern.suffix = 0;

        $('#electorNum').focus();
      }
    };

    $scope.onWontVote = function() {
      var wardCode = $scope.formModel.selectedWard.code;
      var ern = $scope.formModel.ern.pollingDistrict + '-' +
        $scope.formModel.ern.number + '-' +
        $scope.formModel.ern.suffix;
      voteService.wontVote(wardCode, ern)
        .success(function(response) {
          $scope.logs.push({
            ern: util.ernLongToShortFormConverter(response.ern),
            reason: 'Won\'t vote',
            fullErn: util.ernShortToLongFormConverter(wardCode, ern),
            contactId: response.id,
            success: true,
            result: 1
          });
        });
    };

    function isValidWard(ward) {
      $scope.invalidWard = false;
      var isValid = ward && !_.isEmpty(ward.code);
      if (isValid) {
        return true;
      } else {
        $scope.invalidWard = true;
        return false;
      }
    }

    function isValidElectorID(electorID) {
      $scope.invalidErn = false;
      var valid = util.validErn($scope.formModel.selectedWard.code + "-" + electorID);
      if (valid) {
        return true;
      } else {
        $scope.invalidErn = true;
        return false;
      }
    }

    $scope.onUndoVoted = function (model) {
      voteService.undoVote(model.fullErn)
        .success(function () {
          model.reason = 'Voted Undone';
        })
        .error(function () {
          toastr.error('Failed to undo voted', 'Error');
        });
    };

    $scope.onUndoWontVote = function (model) {
      voteService.undoWontVote(model.fullErn, model.contactId)
        .success(function() {
          model.reason = 'Won\'t vote Undone';
        })
        .error(function() {
          model.reason = 'Failed to undo won\'t vote';
          model.success = false;
        });
    };

    $scope.onSearchVoter = function () {
      $scope.invalidSurname = false;
      $scope.invalidPostcode = false;
      $scope.searchResults = null;

      if (!$scope.searchForm.surname) {
        $scope.invalidSurname = true;
      } else if (!$scope.searchForm.postcode) {
        $scope.invalidPostcode = true;
      } else {
        electorService.search($scope.searchForm.surname, $scope.searchForm.postcode, $scope.formModel.selectedWard.code)
          .success(function (voters) {
            $scope.searchResults = voters;
          })
          .error(function handleError() {
            $scope.searchResults = [];
          });
      }
    };

    $scope.onSetSearchedVoter = function (voter) {
      var ern = util.destructureErn(voter.ern);

      $scope.formModel.ern = {
        pollingDistrict: ern.pollingDistrict,
        number: ern.number,
        suffix: ern.suffix
      };
    };

    function emptyRow() {
      return {
        wardCode: '-',
        pollingDistrict: '-',
        ern: '-',
        result: -1
      };
    }
  });
