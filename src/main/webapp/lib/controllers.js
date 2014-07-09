var myApp = angular.module('control');

myApp.controller('Teams', function ($scope, $http, $timeout) {
    $scope.getTeams = function(){
      $http.get('https://127.0.0.1:9443/admin/teams').
          success(function(data) {
              $scope.teams = data;
      });
    };
    
    $scope.getTeamInWater = function (course){
      $http.get('https://127.0.0.1:9443/admin/'+course+'/team').
          success(function(data) {
              var result = $scope.getById($scope.courses, course)
              result.teamInWater = eval("data."+course);
      });
    };
    
    $scope.newTeamInWater = function (course, team) {
        $http.put('https://127.0.0.1:9443/admin/'+course+'/'+team).
            success(function(data) {
                $scope.getTeamInWater(course);
            }
        );
    };
    
    $scope.newRun = function (course, team) {
        $http.post('https://127.0.0.1:9443/admin/newRun/'+course+'/'+team).
            success(function(data) {
                $scope.runSetup = data;
            }
        );
    };
    
    // Function to replicate setInterval using $timeout service.
    $scope.intervalFunction = function(callback){
      $timeout(function() {
        callback();
        $scope.intervalFunction(callback);
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
    <!--$scope.intervalFunction($scope.getTeamInWater);-->
    
    $scope.courses =
                [{name:'Course B', id:'courseB', teamInWater: ''},
                 {name:'Course A', id:'courseA', teamInWater: ''}]
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