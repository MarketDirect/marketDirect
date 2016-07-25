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
