package com.tybbt.knowledgebase.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tybbt.knowledgebase.domain.Category;
import com.tybbt.knowledgebase.domain.CategoryExample;
import com.tybbt.knowledgebase.mapper.CategoryMapper;
import com.tybbt.knowledgebase.req.CategoryQueryReq;
import com.tybbt.knowledgebase.req.CategorySaveReq;
import com.tybbt.knowledgebase.resp.CategoryQueryResp;
import com.tybbt.knowledgebase.resp.PageResp;
import com.tybbt.knowledgebase.util.CopyUtil;
import com.tybbt.knowledgebase.util.SnowFlake;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
public class CategoryService {
    private static final Logger LOG = LoggerFactory.getLogger(CategoryService.class);

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private SnowFlake snowFlake;

    public PageResp<CategoryQueryResp> list(CategoryQueryReq req){

        CategoryExample categoryExample = new CategoryExample();
        CategoryExample.Criteria criteria = categoryExample.createCriteria();
        PageHelper.startPage(req.getPage(), req.getSize());
        // 调用CategoryMapper的list方法
        List<Category> categoryslist = categoryMapper.selectByExample(categoryExample);

        PageInfo<Category> pageInfo = new PageInfo<>(categoryslist);
        LOG.info("总行数： {}", pageInfo.getTotal());
        LOG.info("总页数： {}", pageInfo.getPages());

        // 实体类转换，从返回的实体类转换为封装的返回实体类
//        List<CategoryResp> respList = new ArrayList<>();
//        for (Category category : categoryslist) {
////            CategoryResp categoryResp = new CategoryResp();
////            BeanUtils.copyProperties(category, categoryResp);
        //    对象赋值
//            CategoryResp categoryResp = CopyUtil.copy(category, CategoryResp.class);
//            respList.add(categoryResp);
//        }

        // 列表复制
        List<CategoryQueryResp> list = CopyUtil.copyList(categoryslist, CategoryQueryResp.class);

        PageResp<CategoryQueryResp> pageResp = new PageResp();

        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);

        return pageResp;
    }

    public void save(CategorySaveReq req) {
        Category category = CopyUtil.copy(req, Category.class);
        if (ObjectUtils.isEmpty(category.getId())) {
            // 自增，uuid，雪花
            category.setId(snowFlake.nextId());
            categoryMapper.insert(category);
        } else {
            categoryMapper.updateByPrimaryKey(category);
        }
    }

    public void delete(Long id) {
        categoryMapper.deleteByPrimaryKey(id);
    }

    public List<CategoryQueryResp> all(){
        CategoryExample categoryExample = new CategoryExample();
        categoryExample.setOrderByClause("sort asc");
        // 调用CategoryMapper的list方法
        List<Category> categoryslist = categoryMapper.selectByExample(categoryExample);

        // 列表复制
        List<CategoryQueryResp> list = CopyUtil.copyList(categoryslist, CategoryQueryResp.class);

        return list;
    }
}
