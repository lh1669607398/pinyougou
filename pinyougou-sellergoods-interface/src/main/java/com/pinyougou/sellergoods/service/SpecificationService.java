package com.pinyougou.sellergoods.service;
import java.util.List;
import java.util.Map;

import com.pinyougou.entity.PageResult;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojogroup.Specification;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface SpecificationService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbSpecification> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum, int pageSize);


	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(Long[] ids);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(TbSpecification specification, int pageNum, int pageSize);
	/**
	 * 增加规格
	 */
	public void add(Specification specification);
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public Specification findOne(Long id);
	/**
	 * 修改
	 */
	public void update(Specification specification);

	/**
	 * 规格品牌下拉
	 * @return
	 */
	List<Map> selectOptionList();



}
