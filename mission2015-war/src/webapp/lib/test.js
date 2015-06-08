var myApp = angular.module('user');
var host = "https://127.0.0.1:9443";
<!-- var host = "https://192.168.1.40:9443"; -->
<!-- var host = "https://192.168.1.68:9443"; -->

myApp.controller('test', function ($scope, $http, $timeout) {
    $scope.getNothing = function(){
      $http.get(host + '/my/url').
          success(function(data) {
              $scope.test = data;
      });
    };
    
    
    // Function to replicate setInterval using $timeout service.
    $scope.intervalFunction = function(callback){
      $timeout(function() {
        callback();
        $scope.intervalFunction(callback);
      }, 30000)
    };
    
    $scope.intervalFunction = function(callback, arg){
      $timeout(function() {
        callback(arg);
        $scope.intervalFunction(callback, arg);
      }, 5000)
    };    
});