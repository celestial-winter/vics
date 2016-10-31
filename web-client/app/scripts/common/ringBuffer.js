
angular
  .module('canvass')
  .factory('RingBuffer', function () {
    var RingBuffer = function (size) {
      this.size = size;
      this.elements = [];
    };

    RingBuffer.prototype.push = function(element) {
      this.elements.push(element);
      if (this.elements.length > this.size) {
        this.elements.shift();
      }
    };

    /**
     * Return a new instance for each invocation
     */
    return {
      newInstance: function (elements) {
        return new RingBuffer(elements);
      }
    };
  });
