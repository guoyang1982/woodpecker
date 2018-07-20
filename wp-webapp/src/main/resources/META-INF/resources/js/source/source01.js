/**
 * 
 * 2016-08-10
 * @author zhailiaokuo
 */

define(function(require, exports, module){
//	var Letv = {
//        init : function(){
//        	this._initDom();
//        	this._initEvent();
//        	this._initPage();
//        },
//        _initDom : function(){
//        	this.$table = $('#table') ;
//        },
//        _initEvent : function(){
//        },
//        _initPage : function(){
//        },
//	    _queryParams : function(){
//	    	return {} ;
//	    }
//	};
//	Letv.init();
	
	$('#table').bootstrapTable({
		height: source.getHeight(),
		locale:'zh-CN',
		pagination:true,
		url: '/lms/source/queryAllNames',
		method:'post',
		toolbar: '#toolbar',
		search:true,
		showColumns:true,
		showRefresh:true,
		pageSize:1,
		pageList:[1,3],
//		queryParams:this._queryParams(),
		columns: [{
			width:50,
			field: 'NO',
	        title: '序列号',
	        formatter:function(value,row,index){
	        	return index;
	        }
		},
		{
	        field: 'id',
	        title: 'ID'
	    }, {
	        field: 'name',
	        title: '来源名称'
	    }, {
	        field: 'status',
	        title: '状态',
	        formatter:function(value,row,index){
	        	if( value == '0' ){
		    		return "下线" ;
		    	}else if( value == '1' ){
		    		return "上线" ;
		    	}else{
		    		return "上线" ;
		    	}
	        }
	    }, {
	        field: 'operation',
	        title: '操作',
	        width:200,
	        formatter:function(value,row,index){
	        	var html='<a href="#" class="btn btn-link" ng-click="update(' + row.id+ ')" >编辑</a> | <a href="#" class="btn btn-link" ng-click="sourceCtrl.del(' + row.id+ ')" >删除</a>' ;
	        	var template = angular.element(html);
	        	var mobileDialogElement = $compile(template)($scope);
	        	angular.element(document.body).append(mobileDialogElement);
	        	return '' ;
	        }
	    }]
	});
	
	var sourceApp = angular.module('sourceApp', []) ;
	sourceApp.controller('sourceCtrl', function($scope, $http) {
		$scope.status = '1' ;
		$scope.add=function(){
			$scope.id=0;
			$scope.name="1";
			$scope.url="url";
			$scope.status='0';
	    };
	    $scope.update=function(o){
	    	alert("哈哈  我要修改了" + o);
	    };
	    $scope.del = function(o){
	    	alert("哈哈 我要删除" + $(o).attr("action"));
	    }
	});
	sourceApp.directive("sourceInfo",function(){
		return {
			restrict:'AE',
			scope:{
				id:'@',
				name:'@',
				url:'@',
				status:'@'
			},
			controller: function($scope,$element){
				$scope.sts = [{label : "0", url : "下线"}, {label : "1", url : "上线"}];
			},
			template:'<table><tbody>'+
			'<tr><td>ID</td><td>{{id}}</td><td>来源名称</td><td><input type="text" value="{{name}}"/></td></tr>'
			+ '<tr><td>直播内容获取URL</td><td><input type="text" value="{{url}}"/></td></tr>'
			+ '<tr><td>状态</td><td><select ng-model="status" ><option ng-repeat="x in sts" value="{{x.label}}">{{x.url}}</option></select></td></tr>'
			+'</tbody></table>'
		}
	});
	angular.bootstrap($('#container'), ['sourceApp']);
	
});

var source = {
		update : function(id) {
	    	alert(id);
	    } ,
	    del : function(id){
	    	alert(id);
	    },
	    getHeight : function() {  //可用于固定表头
	        //return $(window).height() - $('h1').outerHeight(true);
	        return 300 ;
	    }
}

$(window).resize(function () {
	$('#table').bootstrapTable('resetView', {
        height: source.getHeight()
    });
});

