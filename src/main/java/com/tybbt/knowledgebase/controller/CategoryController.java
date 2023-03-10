package com.tybbt.knowledgebase.controller;

import com.tybbt.knowledgebase.req.CategoryQueryReq;
import com.tybbt.knowledgebase.req.CategorySaveReq;
import com.tybbt.knowledgebase.resp.CategoryQueryResp;
import com.tybbt.knowledgebase.resp.CommonResp;
import com.tybbt.knowledgebase.resp.PageResp;
import com.tybbt.knowledgebase.service.CategoryService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// @RestController 用于返回一个字符串，一般是Json对象 | @Controller用于返回一个页面
// 在外层类增加@RequestMapping注解，可以直接作为公共的上层链接，后续内部无论使用GET POST都可以作为请求的前缀
@RestController
@RequestMapping("/category")
public class CategoryController {
    // 用于加载application自定义配置项，使用 custom.args:Default 作为默认配置防止转移环境时忘记配置
    @Resource
    private CategoryService categoryService;

    // 程序接口入口 -> 调用categoryService 的list方法
    @GetMapping ("/list")
    public CommonResp list(@Valid CategoryQueryReq req){
        // 前后端参数名称需一致，才能自动映射。
        CommonResp<PageResp<CategoryQueryResp>> response = new CommonResp<>();
        PageResp<CategoryQueryResp> list = categoryService.list(req);
        response.setContent(list);
        return response;
    }

    @GetMapping ("/all")
    public CommonResp all(){
        // 前后端参数名称需一致，才能自动映射。
        CommonResp<List<CategoryQueryResp>> response = new CommonResp<>();
        List<CategoryQueryResp> list = categoryService.all();
        response.setContent(list);
        return response;
    }

    @PostMapping("/save")
    public CommonResp save(@Valid @RequestBody CategorySaveReq req){
        // 前端如果使用POST提交中Content-Type为application/json方式，则需要RequestBody注解包裹request才能处理，axios使用json
        // 如果利用application/x-www-form的方式提交则不需要加注解
        CommonResp resp = new CommonResp<>();
        categoryService.save(req);
        return resp;
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp delete(@PathVariable Long id) {
        // delete 一般根据id删除，所以需要在接口中明确需要删除的id，这个id通过PathVariable映射给id参数
        CommonResp resp = new CommonResp<>();
        categoryService.delete(id);
        return resp;
    }
}
