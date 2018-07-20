/**
 * 
 * 2016-08-10
 * @author zhailiaokuo
 */

define(function(require, exports, module){
	var app = angular.module('sourceApp', ['ngDialog']);
	app.config(function (ngDialogProvider) {
	    ngDialogProvider.setOpenOnePerName(true);
	});
	
	app.controller('sourceCtrl', function($scope, $http,ngDialog) {
		$http.get("/lms/source/queryAllNames") .success(function (response) {$scope.sources = response;});
	    $scope.add=function(){
	    	$scope.id=0;
			$scope.status='1';
			openDialog();
	    }
	    $scope.update=function(o){
	    	$http.get("/lms/source/queryById/"+o) .success(function (response) {
	    		$scope.id=response.data.id ;
	    		$scope.name = response.data.name;
	    		$scope.url = response.data.url ;
	    		$scope.status = response.data.status ;
	    		openDialog();
	    	});
	    };
	    $scope.del = function(o){
	    	ngDialog.openConfirm({
                template: '<p>是否确认删除？<p>\
                    <div class="ngdialog-buttons">\
                    <button type="button" class="ngdialog-button ngdialog-button-secondary" ng-click="closeThisDialog(0)">No</button>\
                    <button type="button" class="ngdialog-button ngdialog-button-primary" ng-click="confirm(1)">Yes</button>\
                </div>',
                plain: true,
                className: 'ngdialog-theme-default',
                overlay:false,
                name:'delSource',
                closeByEscape:false,
		        closeByDocument:false,
		        width: 300,
		        height: 600
            }).then(function (value) {
            	del(o);
            }, function (reason) {
            });
	    };
	    $scope.search = function(){
	    	$http.get("/lms/source/queryAllNames") .success(function (response) {$scope.sources = response;});
	    }
	    $scope.status='1';
	    $scope.save = function(){
	    	$http({
	    		url:'/lms/source/save',
	    		method:'POST',
	    		data:{id:$scope.id,name:$scope.name,url:$scope.url,status:$scope.status}
	    	}).success(function(response){
	    		alert("save ");
	    		console.log("save") ;
	    	});
	    };
	    $scope.close = function(){
	    	ngDialog.close("sourceOperation") ;
	    };
	    
	    function openDialog(){
	    	ngDialog.open({
		        template: '/ng/source/sourceDialog.html',
		        className: 'ngdialog-theme-default',
		        overlay:false,
		        width: 600,
		        height: 400,
		        closeByEscape:false,
		        closeByDocument:false,
		        showClose: true,
		        name:'sourceOperation',
		        controller:'sourceCtrl',
		        scope: $scope
		    });
	    }
	    
	    function del(o){
	    	$http({
	    		url:'/lms/source/deleteById/' + o,
	    		method:'DELETE'
	    	}).success(function(response){
            	$http.get("/lms/source/queryAllNames") .success(function (response) {$scope.sources = response;});
            });
	    }
	});
	app.service('showStatus', function() {
	    this.converStatus = function (x) {
	    	if( x == '0' ){
	    		return "下线" ;
	    	}else if( x == '1' ){
	    		return "上线" ;
	    	}else{
	    		return "上线" ;
	    	}
	    }
	});
	app.filter('converToString',['showStatus',function(showStatus){
		return function(x){
		    return showStatus.converStatus(x);	
		};
	}]);
	app.directive("sourceInfo",function(){
		return {
			restrict:'AE',
			scope:{
				id:'@',
				name:'=',
				url:'=',
				status:'='
			},
			controller: function($scope,$element){
				$scope.sts = [{id : "0", status : "下线"}, {id : "1", status : "上线"}];
			},
			template:'<form class="form-horizontal" role="form"> \
				<div class="form-group">\
				<label class="col-md-2 control-label">ID</label>\
			    <div class="col-md-10">\
			      <p class="form-control-static">{{id}}</p>\
			    </div>\
			    </div>\
			    <div class="form-group">\
			    <label for="inputName" class="col-md-2 control-label">来源名称</label>\
			    <div class="col-md-10">\
			      <input type="text" class="form-control" id="inputName" ng-model="name" >\
			    </div>\
			  </div>\
			  <div class="form-group">\
			    <label for="inputUrl" class="col-md-2 control-label">直播内容获取URL</label>\
			    <div class="col-md-10">\
			      <input type="text" class="form-control" id="inputUrl" ng-model="url" >\
			    </div>\
			  </div>\
			  <div class="form-group">\
			    <label for="inputName" class="col-md-2 control-label">状态</label>\
			    <div class="col-md-10">\
			      <select ng-model="status" class="form-control"><option ng-repeat="x in sts" value="{{x.id}}">{{x.status}}</option></select>\
			    </div>\
			  </div>\
			  </form>'
		}
	});
	
	angular.bootstrap($('#container'), ['sourceApp']);
});

