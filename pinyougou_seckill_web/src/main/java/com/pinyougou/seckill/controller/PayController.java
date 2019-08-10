package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {
    @Reference
    private WeixinPayService weixinPayService;

    @Reference
    private SeckillOrderService seckillOrderService;

    /**
     * 生成二维码
     *
     * @return
     */
    @RequestMapping("/createNative")
    public Map createNative() {
        TbSeckillOrder seckillOrder = seckillOrderService.searchOrderFromRedisByUserId(SecurityContextHolder.getContext().getAuthentication().getName());
        if (seckillOrder != null)
            return weixinPayService.createNative(seckillOrder.getId() + "", (long) (seckillOrder.getMoney().doubleValue() * 100) + "");
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
                seckillOrderService.saveOrderFromRedisToDB(SecurityContextHolder.getContext().getAuthentication().getName(), Long.valueOf(out_trade_no), map.get("transaction_id"));
                return new Result(true, "支付成功");
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (++i >= 100) {
                Result result = new Result(false, "二维码超时");
                Map<String, String> payResult = weixinPayService.closePay(out_trade_no);
                if (!"SUCCESS".equals(payResult.get("return_code"))) {
                    if ("ORDERPAID".equals(payResult.get("err_code"))) {
                        seckillOrderService.saveOrderFromRedisToDB(SecurityContextHolder.getContext().getAuthentication().getName(), Long.valueOf(out_trade_no), map.get("transaction_id"));
                        result = new Result(true, "支付成功");
                    }
                }
                if (result.isSuccess()==false)
                    seckillOrderService.deleteOrderFromRedis(SecurityContextHolder.getContext().getAuthentication().getName(), Long.valueOf(out_trade_no));
                return result;
            }
        }
    }
}
