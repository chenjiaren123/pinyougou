package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbPayLog;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utils.IdWorker;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {
    @Reference
    private WeixinPayService weixinPayService;

    @Reference
    private OrderService orderService;

    /**
     * 生成二维码
     *
     *
     * @return
     */
    @RequestMapping("/createNative")
    public Map createNative() {
        TbPayLog payLog = orderService.searchPayLogFromRedis(SecurityContextHolder.getContext().getAuthentication().getName());
        if (payLog != null)
            return weixinPayService.createNative(payLog.getOutTradeNo() + "", payLog.getTotalFee() + "");
        else return new HashMap();
    }

    /**
     * 查询支付状态
     *
     * @return
     */
    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no) {
        int i = 0;
        while (true) {
            Map<String, String> map = weixinPayService.queryPayStatus(out_trade_no);
            if (map == null)
                return new Result(false, "支付出错");

            if ("SUCCESS".equals(map.get("trade_state"))) {
                orderService.updateOrderStatus(out_trade_no, map.get("transaction_id"));
                return new Result(true, "支付成功");
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (++i >= 100)
                return new Result(false, "二维码超时");
        }
    }
}
