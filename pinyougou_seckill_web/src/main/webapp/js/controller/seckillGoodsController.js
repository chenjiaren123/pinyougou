//控制层
app.controller('seckillGoodsController' ,function($scope,$location,$interval,seckillGoodsService){
    //读取列表数据绑定到表单中
    $scope.findList=function(){
        seckillGoodsService.findList().success(
            function(response){
                $scope.list=response;
            }
        );
    }

    //查询实体
    $scope.findOne=function(){
        seckillGoodsService.findOne($location.search()['id']).success(
            function(response){
                $scope.entity= response;
                allSecond =Math.floor((new Date($scope.entity.endTime).getTime()-new Date().getTime())/1000);
                time = $interval(function () {
                    if (allSecond>0){
                        allSecond = allSecond-1;
                        $scope.timeStr = convertTimeString(allSecond);
                    }else {
                        $interval.cancel();
                        alert("秒杀活动结束")
                    }
                },1000);
            }
        );
    }

    convertTimeString = function (allSecond) {
        var day = Math.floor(allSecond/(24*60*60));

        var hour = Math.floor(allSecond/(60*60)-day*24);

        var min = Math.floor(allSecond/60-day*24*60-hour*60);

        var second = Math.floor(allSecond-day*24*60*60-hour*60*60-min*60);

        if (day>0){
            timeString = day+"天";
        }

        return timeString+hour+":"+min+":"+second
    }

    //提交订单
    $scope.submitOrder=function(){
        seckillGoodsService.submitOrder($scope.entity.id).success(
            function(response){
                if(response.success){
                    alert("下单成功，请在 1 分钟内完成支付");
                    location.href="pay.html";
                }else{
                    alert(response.message);
                }
            }
        );
    }
});