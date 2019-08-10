package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojogroup.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private TbItemMapper itemMapper;

    /**
     * 添加商品到购物车列表
     *
     * @param cartList 购物车列表
     * @param itemId   商品id
     * @param num      商品购买的数量
     * @return
     */
    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
        //1、根据商品id获取商品信息
        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        if (item == null) throw new RuntimeException("商品不存在");
        if (!"1".equals(item.getStatus())) throw new RuntimeException("商品状态无效");

        //2、获取商家id
        String sellerId = item.getSellerId();

        //3、判断购物车列表是否包含此商家
        Cart cart = searchCartBySellerId(cartList, sellerId);

        //4、如果不包含此商家,构建该商家购物车,并存入购物车列表中
        if (cart == null) {
            if (num == null) throw new RuntimeException("数量非法");
            cart = new Cart();
            cart.setSellerId(sellerId);
            cart.setSellerName(item.getSeller());
            TbOrderItem orderItem = createOrderItem(item, num);
            List<TbOrderItem> orderItemList = new ArrayList<>();
            orderItemList.add(orderItem);
            cart.setOrderItemList(orderItemList);
            cartList.add(cart);
        } else {
            //5、如果包含此商家
            //判断该商家购物车是否包含此商品
            TbOrderItem orderItem = searchOrderItemByItemId(cart.getOrderItemList(), itemId);
            //如果包含此商品
            if (orderItem != null) {
                orderItem.setNum(num + orderItem.getNum());
                orderItem.setTotalFee(new BigDecimal(orderItem.getNum() * orderItem.getPrice().doubleValue()));

                if (orderItem.getNum() <= 0) cart.getOrderItemList().remove(orderItem);
                if (cart.getOrderItemList().size() <= 0) cartList.remove(cart);
            } else {
                orderItem = createOrderItem(item, num);
                cart.getOrderItemList().add(orderItem);

            }
        }

        return cartList;
    }

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 从redis中提取购物车
     *
     * @param username
     * @return
     */
    @Override
    public List<Cart> findCartListFromRedis(String username) {
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(username);
        if (cartList == null) cartList = new ArrayList<>();
        return cartList;
    }

    /**
     * 保存购物车列表到redis
     *
     * @param username
     * @param cartList
     */
    @Override
    public void saveCartListToRedis(String username, List<Cart> cartList) {
        redisTemplate.boundHashOps("cartList").put(username, cartList);
    }

    /**
     * 合并购物车
     *
     * @param cartList1
     * @param cartList2
     * @return
     */
    @Override
    public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2) {
        for (Cart cart : cartList1) {
            for (TbOrderItem orderItem : cart.getOrderItemList()) {
                cartList2 = addGoodsToCartList(cartList2, orderItem.getItemId(), orderItem.getNum());
            }
        }
        return cartList2;
    }

    /**
     * 创建订单明细
     *
     * @param item
     * @param num
     * @return
     */
    private TbOrderItem createOrderItem(TbItem item, Integer num) {
        TbOrderItem orderItem = new TbOrderItem();
        orderItem.setSellerId(item.getSellerId());
        orderItem.setNum(num);
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setItemId(item.getId());
        orderItem.setPrice(item.getPrice());
        orderItem.setPicPath(item.getImage());
        orderItem.setTitle(item.getTitle());
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue() * num));
        return orderItem;
    }

    /**
     * 根据商家id查询购物车列表中的商家
     *
     * @param cartList
     * @param sellerId
     * @return
     */
    private Cart searchCartBySellerId(List<Cart> cartList, String sellerId) {
        for (Cart cart : cartList) {
            if (cart.getSellerId().equals(sellerId)) {
                return cart;
            }
        }
        return null;
    }

    /**
     * 根据商品id查询购物车中是否包含此商品
     *
     * @param orderItemList
     * @param itemId
     * @return
     */
    private TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItemList, Long itemId) {
        for (TbOrderItem orderItem : orderItemList) {
            if (orderItem.getItemId().longValue() == itemId.longValue()) {
                return orderItem;
            }
        }
        return null;
    }
}
