/**
 * Service to init plugins on demand
 * Would be better to wrap these in angular directives
 */
angular
  .module('canvass')
  .service('plugins', function () {
    var api = {};

    api.initFloatingLabels = function () {
      $('.floating-label .form-control').on('keyup change', function (e) {
        var input = $(e.currentTarget);

        if ($.trim(input.val()) !== '') {
          input.addClass('dirty').removeClass('static');
        } else {
          input.removeClass('dirty').removeClass('static');
        }
      });

      $('.floating-label .form-control').each(function () {
        var input = $(this);

        if ($.trim(input.val()) !== '') {
          input.addClass('static').addClass('dirty');
        }
      });

      $('.form-horizontal .form-control').each(function () {
        $(this).after('<div class="form-control-line"></div>');
      });
    };

    api.initValidation = function() {
      if (!$.isFunction($.fn.validate)) {
        return;
      }
      $.validator.setDefaults({
        highlight: function (element) {
          $(element).closest('.form-group').addClass('has-error');
        },
        unhighlight: function (element) {
          $(element).closest('.form-group').removeClass('has-error');
        },
        errorElement: 'span',
        errorClass: 'help-block',
        errorPlacement: function (error, element) {
          if (element.parent('.input-group').length) {
            error.insertAfter(element.parent());
          }
          else if (element.parent('label').length) {
            error.insertAfter(element.parent());
          }
          else {
            error.insertAfter(element);
          }
        }
      });

      $('.form-validate').each(function () {
        var validator = $(this).validate();
        $(this).data('validator', validator);
      });
    };

    return api;
  });
