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
