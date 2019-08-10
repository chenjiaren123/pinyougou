package com.pinyougou.cart.service;

import com.pinyougou.pojogroup.Cart;

import java.util.List;

/**
 * 购物车服务接口
 */
public interface CartService {

    /**
     * 添加商品到购物车列表
     *
     * @param cartList 购物车列表
     * @param itemId   商品id
     * @param num      商品购买的数量
     * @return
     */
    List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num);

    /**
     * 从redis中提取购物车
     *
     * @param username
     * @return
     */
    List<Cart> findCartListFromRedis(String username);

    /**
     * 保存购物车列表到redis
     *
     * @param username
     * @param cartList
     */
    void saveCartListToRedis(String username, List<Cart> cartList);

    /**
     * 合并购物车
     * @param cartList1
     * @param cartList2
     * @return
     */
    List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2);
}
