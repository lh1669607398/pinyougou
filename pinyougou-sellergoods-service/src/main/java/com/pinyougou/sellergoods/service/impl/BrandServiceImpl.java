package com.pinyougou.sellergoods.service.impl;

import java.util.List;
import java.util.Map;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.entity.PageResult;
import com.pinyougou.pojo.TbBrandExample;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
@Service
public class BrandServiceImpl implements BrandService {

	@Autowired
	private TbBrandMapper brandMapper;

	/**
	 * 查询所有品牌
	 * @return
	 */
	@Override
	public List<TbBrand> findAll() {

		return brandMapper.selectByExample(null);
	}

	/**
	 * 分页
	 * @param pageNum
	 * @param size
	 * @return
	 */
    @Override
    public PageResult findPage(int pageNum, int size) {
		PageHelper.startPage(pageNum,size);
        Page<TbBrand> page = (Page<TbBrand>) brandMapper.selectByExample(null);
        return new PageResult(page.getTotal(),page.getResult());
    }
	/**
	 * 增加品牌
	 * @param brand
	 */
	@Override
	public void add(TbBrand brand) {
		brandMapper.insert(brand);
	}
	/**
	 * 修改
	 */
	@Override
	public void update(TbBrand brand){
		brandMapper.updateByPrimaryKey(brand);
	}

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbBrand findOne(Long id){
		return brandMapper.selectByPrimaryKey(id);
	}
	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			brandMapper.deleteByPrimaryKey(id);
		}
	}
	@Override
	public PageResult findPage(TbBrand brand, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		TbBrandExample example=new TbBrandExample();
		TbBrandExample.Criteria criteria = example.createCriteria();
		if(brand!=null){
			if(brand.getName()!=null && brand.getName().length()>0){
				criteria.andNameLike("%"+brand.getName()+"%");
			}
			if(brand.getFirstChar()!=null && brand.getFirstChar().length()>0){
				criteria.andFirstCharEqualTo(brand.getFirstChar());
			}
		}
		Page<TbBrand> page= (Page<TbBrand>)brandMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 品牌下拉
	 * @return
	 */
    @Override
    public List<Map> selectOptionList() {
        return 	 brandMapper.selectOptionList();
    }


}
