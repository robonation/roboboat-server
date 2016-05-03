var myApp = angular.module('control');
var host = "http://admin:buoyancy@192.168.1.111:8080";
<!-- var host = "https://192.168.1.40:9443"; -->
<!-- var host = "https://192.168.1.68:9443"; -->

myApp.controller('Teams', function ($scope, $http, $timeout) {
    //$http.defaults.headers.common = {"Access-Control-Request-Headers": "accept, origin, authorization"};
    $http.defaults.headers.common['Authorization'] = 'Basic YWRtaW46YnVveWFuY3k=';
    
    $scope.getTeams = function(){
      $http.get(host + '/admin/teams').
          success(function(data) {
              $scope.teams = data;
      });
    };
    
    $scope.getTeamInWater = function (course){
      $http.get(host + '/admin/'+course.id+'/team').
          success(function(data) {
              var result = $scope.getById($scope.courses, course.id)
              result.teamInWater = eval("data."+course.id);
      });
    };
    
        
    $scope.getEvents = function (course){
      $http.get(host + '/admin/events/'+course.id).
          success(function(data) {
              function isNotHeartbeat(value) {
                  return value.message != "Heartbeat report";
              }
              
              course.events = data;
              course.events.forEach(function(run) {
                  run.start = "".concat(run.start.year, "/", run.start.monthValue, "/", run.start.dayOfMonth, "-", run.start.hour, ":", run.start.minute);
                  run.events = run.events.filter(isNotHeartbeat);
                  run.events.forEach(function(entry) {
                      entry.time = "".concat(entry.time.year, "/", entry.time.monthValue, "/", entry.time.dayOfMonth, "-", entry.time.hour, ":", entry.time.minute);
                  });
              });
      });
    };
    
    $scope.newTeamInWater = function (course, team) {
        $http.put(host + '/admin/'+course.id+'/'+team).
            success(function(data) {
                $scope.getTeamInWater(course);
            }
        );
    };
    
    $scope.hideDebug = function (course) {
        course.hideDebug = !course.hideDebug;
        if (course.hideDebug) {
            course.showText = 'Show';
        } else {
            course.showText = 'Hide';
        }
    };
    
    $scope.newRun = function (course, team) {
        course.newRunButtonClass = 'btn-warning';
        $http.post(host + '/admin/newRun/'+course.id+'/'+team).
            success(function(data) {
                course.runSetup = data;
                course.newRunButtonClass = 'btn-primary';
            }
        );
    };
    
    $scope.endRun = function (course, team) {
        course.endRunButtonClass = 'btn-warning';
        $http.post(host + '/admin/endRun/'+course.id+'/'+team).
            success(function(data) {
                course.endRunButtonClass = 'btn-primary';
            }
        );
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
    
    $scope.getById = function (arr, id) {
        for (var d = 0, len = arr.length; d < len; d += 1) {
            if (arr[d].id === id) {
                return arr[d];
            }
        }
    }
  
  
    // Kick off the interval
    $scope.getTeams();
    $scope.intervalFunction($scope.getTeams);
    $scope.courses =
                [{name:'Course B', id:'courseB', teamInWater: '', runSetup: '', events: [], expandedEvents: {}, hideDebug: true, showText: 'Show', newRunButtonClass: 'btn-primary', endRunButtonClass: 'btn-primary'},
                 {name:'Course A', id:'courseA', teamInWater: '', runSetup: '', events: [], expandedEvents: {}, hideDebug: true, showText: 'Show', newRunButtonClass: 'btn-primary', endRunButtonClass: 'btn-primary'}]
    $scope.courses.forEach(function(entry) {
        $scope.intervalFunction($scope.getTeamInWater, entry);
        $scope.intervalFunction($scope.getEvents, entry);
    });    
});

/*
myApp.controller('LoginCtrl', function ($scope, $location, auth, $log) {
    $scope.$log = $log;

    $scope.go = function (target) {
        $location.path(target);
    };

    $scope.login = function () {
        auth.signin({ popup: true, scope: 'openid name email' })
            .then(function () {
                $log.log('Logged successfully!');
                $location.path('/');
            }, function (err) {
                $log.log('Logged failed!');
                // Oops something went wrong
                window.alert('Oops, invalid credentials');
                $location.path('/login');
            });
    };
    $scope.logout = function () {
        auth.signout();
        $location.path('/login');
    };
});

myApp.controller('RootCtrl', function ($scope, $location, $http, auth) {
    if (!auth.isAuthenticated) {
        // Reject the user
        $location.path('/login');
        return;
    }

    // User is logged in at this point

    $scope.user = auth.profile;
});


myApp.controller('LogoutCtrl', function ($scope, $location, auth, $log) {
    auth.signout();
    $log.log('Logged out!');
    $location.path('/login');
});
*/