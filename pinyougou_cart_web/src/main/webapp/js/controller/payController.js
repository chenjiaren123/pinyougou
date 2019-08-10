app.controller('payController', function ($scope,$location, payService) {
    $scope.createNative = function () {
        payService.createNative().success(function (response) {
            $scope.money = (response.total_fee / 100).fix(2);
            $scope.out_trade_no = response.out_trade_no;//订单号
            var qr = new QRious(
                element
        :
            document.getElementById('qrious'),
                size
        :
            250,
                leval
        :
            'H',
                value
        :
            response.code_url
        );
            queryPayStatus($scope.out_trade_no);
        });
    }

    //查询支付状态
    queryPayStatus = function (out_trade_no) {
        payService.queryPayStatus(out_trade_no).success(function (response) {
            if (response.success) {
                location.href = "paysuccess.html#?money="+$scope.money;
            } else {
                if (response.message=='二维码超时'){
                    $scope.createNative();
                }else {
                    location.href = "payfail.html";
                }
            }
        });
    }

    $scope.getMoney=function () {
        return $location.search()['money'];
    }

});