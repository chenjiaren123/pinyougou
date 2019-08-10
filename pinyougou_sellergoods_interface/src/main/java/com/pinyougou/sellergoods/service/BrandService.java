package com.pinyougou.sellergoods.service;
/**
 * 品牌接口
 * @author 陈家仁
 *
 */

import java.util.List;
import java.util.Map;

import com.pinyougou.pojo.TbBrand;

import entity.PageResult;

public interface BrandService {
	List<TbBrand> findAll();
	/**
	 * 品牌分页
	 * @param pageNum 当前页码
	 * @param pageSize 数据条数
	 * @return
	 */
	PageResult findPage(int pageNum, int pageSize);
	
	/**
	 *增加品牌 
	 * @param brand
	 */
	void add(TbBrand brand);
	/**
	 * 修改
	 * @param brand
	 */
	void update(TbBrand brand);
	
	/**
	 * 根据id获取实体
	 * @param id
	 * @return
	 */
	TbBrand findOne(Long id);
	/**
	 * 批量删除
	 * @param ids
	 */
	void delete(Long[] ids);
	
	/**
	 * 品牌分页,条件查询
	 * @param pageNum 当前页码
	 * @param pageSize 数据条数
	 * @return
	 */
	PageResult findPage(TbBrand brand, int pageNum, int pageSize);
	
	/**
	 * 品牌下拉框数据
	 * @return
	 */
	List<Map> selectOptionList();
}
