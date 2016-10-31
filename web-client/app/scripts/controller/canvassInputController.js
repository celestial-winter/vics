/**
 * Controls the data input form to record a canvasser input
 */
angular
  .module('canvass')
  .controller('canvassInputController', function (util, $scope, electorService, RingBuffer, wardService) {
    var logSize = 5;

    $scope.issues = [''];
    $scope.searchResults = null;
    $scope.logs = RingBuffer.newInstance(logSize);

    $scope.searchForm = {
      surname: '',
      postcode: ''
    };

    function initForm() {
      var prevWard = $scope.inputRecordModel && $scope.inputRecordModel.ward;
      $scope.ignored = {
        moved: false
      };
      $scope.inputRecordModel = {
        ern: {
          pollingDistrict: '',
          number: '',
          suffix: ''
        },
        intention: null,
        likelihood: null,
        cost: false,
        border: false,
        sovereignty: false,
        hasVoted: false,
        hasPV: false,
        wantsPV: false,
        poster: false,
        lift: false,
        deceased: false,
        inaccessible: false,
        ward: null,
        telephone: null,
        email: null
      };

      $scope.moved = false;

      if (prevWard) {
        $scope.inputRecordModel.ward = prevWard;
      }
    }

    initForm();

    wardService.findAllSummarized()
      .success(function (response) {
        $scope.wards = response;
        $scope.userHasNoAssociations = _.isEmpty($scope.wards);
        $scope.inputRecordModel.ward = $scope.wards[0];
      })
      .finally(function () {
        $scope.contentLoaded = true;
      });

    $scope.showPrintMenu = function() {
      return $scope.streets && $scope.streets.length;
    };

    $scope.onSearchVoter = function () {
      $scope.invalidSurname = false;
      $scope.invalidPostcode = false;
      $scope.searchResults = null;

      function handleSuccess(voters) {
        $scope.searchResults = voters;
      }

      function handleError() {
        $scope.searchResults = [];
      }

      if (!$scope.searchForm.surname) {
        $scope.invalidSurname = true;
      } else if (!$scope.searchForm.postcode) {
        $scope.invalidPostcode = true;
      } else {
        electorService.search($scope.searchForm.surname, $scope.searchForm.postcode, $scope.inputRecordModel.ward.code)
          .success(handleSuccess)
          .error(handleError);
      }
    };

    $scope.onSetSearchedVoter = function(voter) {
      var ern = util.destructureErn(voter.ern);
      $scope.inputRecordModel.ern.pollingDistrict = ern.pollingDistrict;
      $scope.inputRecordModel.ern.number = ern.number;
      $scope.inputRecordModel.ern.suffix = ern.suffix;
    };

    $scope.onUndo = function(model) {
      electorService.undoCanvassInput(model.fullErn, model.contactId, model.localId)
        .success(function() {
          model.reason = 'Undone';
        })
        .error(function() {
          model.reason = 'Failed to undo';
          model.success = false;
        });
    };

    /**
     * Submits the entry form and adds the result to the log.
     */
    $scope.onSubmitRecord = function () {
      $scope.errors = validateForm();

      if (_.isEmpty($scope.errors)) {
        var requestData = mapFormToRequest($scope.inputRecordModel);
        var ern = constructErnFromParts($scope.inputRecordModel.ern, $scope.inputRecordModel.ward.code);
        electorService.submitCanvassInput(ern, requestData)
          .success(function(response) {
            $scope.elector = response;
            $scope.logs.push({
              ern: stripWardCodeFromErn(response.ern),
              reason: '-',
              fullErn: ern,
              contactId: response.contactId,
              localId: response.localId,
              success: true
            });
            resetForm();
          })
          .error(function (err) {
            resetForm();
            if (err && err.type === 'PafApiNotFoundFailure') {
              $scope.logs.push({
                ern: stripWardCodeFromErn(ern),
                fullErn: ern,
                reason: 'Voter not found',
                success: false
              });
            } else {
              $scope.logs.push({
                ern: stripWardCodeFromErn(ern),
                fullErn: ern,
                reason: 'Failed to record contact',
                success: false
              });
            }
          });
      }
      scrollToInput();
    };

    function scrollToInput() {
      $("html, body").animate({scrollTop: $('#canvassEntry').offset().top}, 500);
      $('#electorNum').focus();
    }

    function mapFormToRequest(formModel) {
      var copy = $.extend(true, {}, formModel);
      copy.intention = _.parseInt(formModel.intention);
      copy.likelihood = _.parseInt(formModel.likelihood);
      delete copy.ward;

      return copy;
    }

    function stripWardCodeFromErn(fullErn) {
      var parts = fullErn.split("-");
      parts.splice(0, 1);
      return parts.join("-");
    }

    function resetForm() {
      var prefix = $scope.inputRecordModel.ern.pollingDistrict,
        suffix = $scope.inputRecordModel.ern.suffix,
        number = $scope.inputRecordModel.ern.number;
      initForm();
      $scope.inputRecordModel.ern.pollingDistrict = prefix;
      $scope.inputRecordModel.ern.suffix = suffix;
      $scope.inputRecordModel.ern.number = number;
    }

    function constructErnFromParts(model, wardCode) {
      return wardCode + '-' + model.pollingDistrict + '-' +
        model.number + '-' +
        model.suffix;
    }

    function validateForm() {
      var errors = [];

      var ern = constructErnFromParts($scope.inputRecordModel.ern, $scope.inputRecordModel.ward.code);

      if (!util.validErn(ern)) {
        errors.push("Elector ID is invalid");
      }

      if (!$scope.inputRecordModel.intention) {
        errors.push("Please set the voting intention");
      }

      if (!$scope.inputRecordModel.likelihood) {
        errors.push("Please set the voting likelihood");
      }

      return errors;
    }
  });
