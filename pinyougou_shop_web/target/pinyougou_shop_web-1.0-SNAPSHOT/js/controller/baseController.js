app.controller('baseController', function($scope){
	//分页控件配置  
	$scope.paginationConf = {
		currentPage : 1,
		totalItems : 10,
		itemsPerPage : 10, //每页记录数
		perPageOptions : [ 10, 20, 30, 40, 50 ], //分页选项
		onChange : function() { //页码变更后自动触发的方法
			$scope.reloadList();//重新加载 
		}
	};
	
	$scope.searchEntity = {};
	
	$scope.reloadList = function() {
		$scope.search($scope.paginationConf.currentPage,
				$scope.paginationConf.itemsPerPage, $scope.searchEntity);
	};
	
	$scope.selectIds = [];
	$scope.updateSelection = function($event, id) {
		if ($event.target.checked) {
			$scope.selectIds.push(id);
		} else {
			var index = $scope.selectIds.indexOf(id);
			$scope.selectIds.splice(index, 1);
		}
	};
	
	$scope.jsonToString=function(jsonString,key){
		var json = JSON.parse(jsonString);//json字符串转换成json对象
		var value="";
		for(var i=0;i<json.length;i++){
			if(i>0){
				value+=",";
			}
			value+=json[i][key];
		}
		return value;
	}
	
	//在list集合中查询某key的值
	$scope.serachObjectByKey=function(list , key ,keyValue){
		for(var i = 0;i<list.length;i++){
			if(list[i][key]==keyValue){
				return list[i];
			}
		}
		return null;
	}
});