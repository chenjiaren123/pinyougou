//商品详细页（控制层）
app.controller('itemController', function ($scope , $http) {
    //数量操作
    $scope.addNum = function (x) {
        $scope.num = $scope.num + x;
        if ($scope.num < 1) {
            $scope.num = 1;
        }
    };

    //用户选择规格
    $scope.selectSpecification = function (name, value) {
        $scope.specificationItems[name] = value;
        searchSku();
    };

    $scope.isSelected = function (name, value) {
        if ($scope.specificationItems[name] == value) {
            return true;
        } else {
            return false;
        }
    };

    $scope.loadSku = function () {
        $scope.sku = skuList[0];
        $scope.specificationItems = JSON.parse(JSON.stringify($scope.sku.spec));
    };

    matchObject=function (map1,map2) {
        for (var k in map1){
            if (map1[k]!=map2[k]){
                return false;
            }
        }

        for (var k in map2){
            if (map2[k]!=map1[k]){
                return false;
            }
        }
        return true;
    };

    searchSku=function () {
        for(var i=0;i<skuList.length;i++){
            if (matchObject(skuList[i].spec,$scope.specificationItems)){
                $scope.sku=skuList[i];
                return;
            }
        }
        $scope.sku={id:0,title:'------',price:0};
    }
    
    $scope.addToCart=function () {
        $http.get('http://localhost:9107/cart/addGoodsToCartList.do?itemId='
            + $scope.sku.id +'&num='+$scope.num,{'withCredentials':true}).success(
            function(response){
                if (response.success){
                    location.href='http://localhost:9107/cart.html';//跳转到购物车页面
                }else{
                    alert(response.message);
                }
            }
        );
    }
});