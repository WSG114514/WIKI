package com.kk.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kk.wiki.domain.Category;
import com.kk.wiki.domain.CategoryExample;
import com.kk.wiki.mapper.CategoryMapper;
import com.kk.wiki.req.CategoryQueryReq;
import com.kk.wiki.req.CategorySaveReq;
import com.kk.wiki.resp.CategoryQueryResp;
import com.kk.wiki.resp.PageResp;
import com.kk.wiki.utils.CopyUtil;
import com.kk.wiki.utils.SnowFlake;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class CategoryService {

    @Resource
    private CategoryMapper categoryMapper;
    @Resource
    private SnowFlake snowFlake;

    public PageResp<CategoryQueryResp> list(CategoryQueryReq req) {

        //PageHelper在查詢一次后就會失效**** （）
        PageHelper.startPage(req.getPage(), req.getSize());
        CategoryExample categoryExample = new CategoryExample();
        categoryExample.setOrderByClause("sort asc");
        //createCriteria 相当于创建一个where条件
        CategoryExample.Criteria criteria = categoryExample.createCriteria();

        List<Category> categoryList = categoryMapper.selectByExample(categoryExample);
        PageInfo<Category> pageInfo = new PageInfo<>(categoryList);
        //复制列表
        List<CategoryQueryResp> list = CopyUtil.copyList(categoryList, CategoryQueryResp.class);

        PageResp<CategoryQueryResp> pageResp = new PageResp();
        pageResp.setList(list);
        pageResp.setTotal(pageInfo.getTotal());

        return pageResp;
    }

    /**
     * 查询所有
     * @return
     */
    public List<CategoryQueryResp> all() {

        CategoryExample categoryExample = new CategoryExample();
        categoryExample.setOrderByClause("sort asc");
        List<Category> categoryList = categoryMapper.selectByExample(categoryExample);

        //复制列表
        List<CategoryQueryResp> list = CopyUtil.copyList(categoryList, CategoryQueryResp.class);

        return list;
    }

    /**
     * 保存
     */
    public void save(CategorySaveReq req) {
        Category category = CopyUtil.copy(req, Category.class);
        if (ObjectUtils.isEmpty(req.getId())) {
            // 新增
            category.setId(snowFlake.nextId());
            categoryMapper.insert(category);
        }else {
            // 更新
            categoryMapper.updateByPrimaryKey(category);
        }
    }

    public void delete(Long id) {
        categoryMapper.deleteByPrimaryKey(id);
    }
}
