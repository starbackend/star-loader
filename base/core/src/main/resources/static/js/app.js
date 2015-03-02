var app = angular.module('starLoaderApp', [
    'ui.router',
    'ui.bootstrap',
    'angularFileUpload'
]);

app.config(function($stateProvider, $urlRouterProvider) {
    $urlRouterProvider.otherwise("/home");

    $stateProvider.state('home', {
        url : "/home",
        templateUrl : "partials/home.html"
    }).state('ovr-mediterranean', {
        url : "/ovr-mediterranean",
        templateUrl : "partials/ovr-mediterranean.html"
    });
});

app.controller('MediterraneanController', [
    '$scope',
    'FileUploader',
    function($scope, FileUploader) {
        var uploader = $scope.uploader = new FileUploader({
            url : 'starupload/mediterranean',
            autoUpload : true
        });
    }
]);