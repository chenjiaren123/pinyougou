//控制层
app.controller('userController', function ($scope, $controller, userService) {
    $scope.reg=function () {
        if ($scope.entity.password!=$scope.password){
            alert("密码不一致,请重新输入");
            $scope.entity.password="";
            $scope.password="";
            return;
        }
        userService.add($scope.entity,$scope.smsCode).success(function (resopnse) {
            alert(resopnse.message)
        });

    }

    $scope.sendCode=function () {
        if ($scope.entity.phone==null){
            alert("请输入手机号");
            return;
        }
        userService.sendCode($scope.entity.phone).success(function (response) {
            alert(response.message)
        });
    }


});