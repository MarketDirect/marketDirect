(function e(t,n,r){function s(o,u){if(!n[o]){if(!t[o]){var a=typeof require=="function"&&require;if(!u&&a)return a(o,!0);if(i)return i(o,!0);throw new Error("Cannot find module '"+o+"'")}var f=n[o]={exports:{}};t[o][0].call(f.exports,function(e){var n=t[o][1][e];return s(n?n:e)},f,f.exports,e,t,n,r)}return n[o].exports}var i=typeof require=="function"&&require;for(var o=0;o<r.length;o++)s(r[o]);return s})({1:[function(require,module,exports){
module.exports = function (app) {
    app.controller('ArtController', ['$scope', '$http', '$location', 'loginService', function ($scope, $http, $location, loginService) {


      
    }]);
}

},{}],2:[function(require,module,exports){
module.exports = function (app) {
    app.controller('CraftedController', ['$scope', '$http', '$location', 'loginService', function ($scope, $http, $location, loginService) {


      
    }]);
}

},{}],3:[function(require,module,exports){
module.exports = function (app) {
    app.controller('ExploreController', ['$scope', '$http', '$location', 'loginService', function ($scope, $http, $location, loginService) {


        // $scope.login = function () {
        //     console.log(`${$scope.name} in as we speak`);
        //     loginService.userLogin($scope.name, $scope.password);
        //     $location.path('/explore');
        // };
    }]);
}

},{}],4:[function(require,module,exports){
module.exports = function (app) {
    app.controller('InventoryController', ['$scope', '$http', '$location', 'loginService', function ($scope, $http, $location, loginService) {


        // $scope.login = function () {
        //     console.log(`${$scope.name} in as we speak`);
        //     loginService.userLogin($scope.name, $scope.password);
        //     $location.path('/explore');
        // };
    }]);
}

},{}],5:[function(require,module,exports){
module.exports = function (app) {
    app.controller('LoginController', ['$scope', '$http', '$location', 'loginService', function ($scope, $http, $location, loginService) {
        $scope.name = '';
        $scope.password = '';

        $scope.login = function () {
            console.log(`${$scope.name} in as we speak`);
            loginService.userLogin($scope.name, $scope.password);
            $location.path('/explore');
        };
    }]);
}

},{}],6:[function(require,module,exports){
module.exports = function (app) {
    app.controller('MiscController', ['$scope', '$http', '$location', 'loginService', function ($scope, $http, $location, loginService) {



    }]);
}

},{}],7:[function(require,module,exports){
module.exports = function (app) {
    app.controller('NewUserController', ['$scope', '$http', '$location', 'newUserService', function ($scope, $http, $location, newUserService) {
      $scope.name = '';
      $scope.password = '';

      $scope.createUser = function () {
          console.log(`${$scope.name} is a new user`);
          newUserService.userLogin($scope.name, $scope.password);
          $location.path('/explore');
      };

    }]);
};

},{}],8:[function(require,module,exports){
module.exports = function (app) {
    app.controller('ProduceController', ['$scope', '$http', '$location', 'loginService', function ($scope, $http, $location, loginService) {

      $scope.login = function () {
          console.log(`${$scope.name} in as we speak`);
          loginService.userLogin($scope.name, $scope.password);
        };

    }]);
}

},{}],9:[function(require,module,exports){
module.exports = function (app) {
    app.controller('ProfileController', ['$scope', '$http', '$location', 'newUserService', function ($scope, $http, $location, newUserService) {
      $scope.name = '';
      $scope.password = '';

      $scope.createUser = function () {
          console.log(`${$scope.name} is a new user`);
          newUserService.userLogin($scope.name, $scope.password);
          $location.path('/explore');
      };

    }]);
};

},{}],10:[function(require,module,exports){

module.exports = function (app) {
    app.controller('ShoppinglistController', ['$scope', '$http', '$location', function ($scope, $http, $location) {



    }]);
}

},{}],11:[function(require,module,exports){
let app = angular.module('MarketApp', ['ngRoute', 'MarketControllers', 'MarketServices', 'MarketDirectives']);
angular.module('MarketControllers', []);       // create empty module
angular.module('MarketServices', []);          // create empty module
angular.module('MarketDirectives', []);

app.config(['$routeProvider', function ($routeProvider) {
  $routeProvider
    .when('/', {
      controller: 'LoginController',
      templateUrl: 'templates/login.html',
    })
    .when('/login', {
      controller: 'LoginController',
      templateUrl: 'templates/login.html',
    })
    .when('/profile', {
      controller: 'ProfileController',
      templateUrl: 'templates/profile.html',
    })
    .when('/newuser', {
      controller: 'NewUserController',
      templateUrl: 'templates/newuser.html',
    })
    .when('/explore', {
      controller: 'ExploreController',
      templateUrl: 'templates/explore.html',
    })
    .when('/art', {
      controller: 'ArtController',
      templateUrl: 'templates/art.html',
    })
    .when('/produce', {
      controller: 'ProduceController',
      templateUrl: 'templates/produce.html',
    })
    .when('/handCrafted', {
      controller: 'CraftedController',
      templateUrl: 'templates/handCrafted.html',
    })
    .when('/misc', {
      controller: 'MiscController',
      templateUrl: 'templates/misc.html',
    })
    .when('/inventory', {
      controller: 'InventoryController',
      templateUrl: 'templates/inventory.html',
    })
    .when('/shoppinglist', {
      controller: 'InventoryController',
      templateUrl: 'templates/shoppinglist.html',
    })

}])





function onSignIn(googleUser) {
     // Useful data for your client-side scripts:
     var profile = googleUser.getBasicProfile();
     console.log("ID: " + profile.getId()); // Don't send this directly to your server!
     console.log('Full Name: ' + profile.getName());
     console.log('Given Name: ' + profile.getGivenName());
     console.log('Family Name: ' + profile.getFamilyName());
     console.log("Image URL: " + profile.getImageUrl());
     console.log("Email: " + profile.getEmail());

     // The ID token you need to pass to your backend:
     var id_token = googleUser.getAuthResponse().id_token;
     console.log("ID Token: " + id_token);
   };

//controllers
// require('./controllers/LibraryController.js')(app);
require('./controllers/LoginController.js')(app);
require('./controllers/NewUserController.js')(app);
require('./controllers/ExploreController.js')(app);
require('./controllers/CraftedController.js')(app);
require('./controllers/InventoryController.js')(app);
require('./controllers/ShoppinglistController.js')(app);
require('./controllers/ProfileController.js')(app);
require('./controllers/ProduceController.js')(app);
require('./controllers/ArtController.js')(app);
require('./controllers/MiscController.js')(app);
// require('./controllers/VideoController.js')(app);
// services
require('./services/login.js')(app);
require('./services/newUser.js')(app);



document.getElementById('getval').addEventListener('change', readURL, true);
function readURL(){
    var file = document.getElementById("getval").files[0];
    var reader = new FileReader();
    reader.onloadend = function(){
        document.getElementById('clock').style.backgroundImage = "url(" + reader.result + ")";
    }
    if(file){
        reader.readAsDataURL(file);
    }else{
    }
}
require('./services/shoppinglist.js')(app);

},{"./controllers/ArtController.js":1,"./controllers/CraftedController.js":2,"./controllers/ExploreController.js":3,"./controllers/InventoryController.js":4,"./controllers/LoginController.js":5,"./controllers/MiscController.js":6,"./controllers/NewUserController.js":7,"./controllers/ProduceController.js":8,"./controllers/ProfileController.js":9,"./controllers/ShoppinglistController.js":10,"./services/login.js":12,"./services/newUser.js":13,"./services/shoppinglist.js":14}],12:[function(require,module,exports){
module.exports = function(app) {
    app.factory('loginService', ['$http', function($http) {
        let username = "";

        return {
            userLogin: function(name, password) {
              console.log("login might be working")
                username = name;
                return $http({
                    method: 'POST',
                    url: '/login',
                    data: {
                        username: name,
                        password: password
                    }
                })
            },
            getUserName: function() {
                return username;
            },
        }
    }])
}

},{}],13:[function(require,module,exports){
module.exports = function(app) {
    app.factory('newUserService', ['$http', function($http) {
        let username = "";

        return {
            userLogin: function(name, password) {
              console.log("login might be working")
                username = name;
                return $http({
                    method: 'POST',
                    url: '/create-user',
                    data: {
                        username: name,
                        password: password
                    }
                }).then(function(response) {
                    console.log('getting the response', response);
                    // username = name;
                    console.log(username);
                })
            },
            getUserName: function() {
                return username;
            },
        }
    }])
}

},{}],14:[function(require,module,exports){
let current = angular.module('MarketServices');

current.factory('ShoppingListService', ['$http', function ($http) {
    let slItems = [];

    return {
        /* GET request for book list */
        getSLItems: function () {
            $http({
                method: 'get',
                url: 'get-shopping-list'
            }).then(function (response) {
                console.table(response.data.Item);
                angular.copy(response.data.Item, slItems);
            });

            return slItems;
        },
        /* POST request to update one book */
        borrowBook: function (book) {

        },
        /* POST request to update one book */
        returnBook: function (book) {

        },
    };
}]);

// testing

},{}]},{},[11])