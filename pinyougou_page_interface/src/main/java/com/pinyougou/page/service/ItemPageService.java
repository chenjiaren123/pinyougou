package com.pinyougou.page.service;

/**
 * 商品详细页
 */
public interface ItemPageService {
    /**
     * 生成商品详细页
     *
     * @param goodsId
     * @return
     */
    boolean genItemHtml(Long goodsId);

    /**
     * 删除商品详细页
     * @param goodsIds
     * @return
     */
    boolean deleteItemHtml(Long[] goodsIds);
}
