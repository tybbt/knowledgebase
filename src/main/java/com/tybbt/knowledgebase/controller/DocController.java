package com.tybbt.knowledgebase.controller;

import com.tybbt.knowledgebase.req.DocQueryReq;
import com.tybbt.knowledgebase.req.DocSaveReq;
import com.tybbt.knowledgebase.resp.CommonResp;
import com.tybbt.knowledgebase.resp.DocQueryResp;
import com.tybbt.knowledgebase.resp.PageResp;
import com.tybbt.knowledgebase.service.DocService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

// @RestController 用于返回一个字符串，一般是Json对象 | @Controller用于返回一个页面
// 在外层类增加@RequestMapping注解，可以直接作为公共的上层链接，后续内部无论使用GET POST都可以作为请求的前缀
@RestController
@RequestMapping("/doc")
public class DocController {
    // 用于加载application自定义配置项，使用 custom.args:Default 作为默认配置防止转移环境时忘记配置
    @Resource
    private DocService docService;

    // 程序接口入口 -> 调用docService 的list方法
    @GetMapping ("/list")
    public CommonResp list(@Valid DocQueryReq req){
        // 前后端参数名称需一致，才能自动映射。
        CommonResp<PageResp<DocQueryResp>> response = new CommonResp<>();
        PageResp<DocQueryResp> list = docService.list(req);
        response.setContent(list);
        return response;
    }

    @GetMapping ("/all")
    public CommonResp all(){
        // 前后端参数名称需一致，才能自动映射。
        CommonResp<List<DocQueryResp>> response = new CommonResp<>();
        List<DocQueryResp> list = docService.all();
        response.setContent(list);
        return response;
    }

    @PostMapping("/save")
    public CommonResp save(@Valid @RequestBody DocSaveReq req){
        // 前端如果使用POST提交中Content-Type为application/json方式，则需要RequestBody注解包裹request才能处理，axios使用json
        // 如果利用application/x-www-form的方式提交则不需要加注解
        CommonResp resp = new CommonResp<>();
        docService.save(req);
        return resp;
    }

    @DeleteMapping("/delete/{ids}")
    public CommonResp delete(@PathVariable String ids) {
        // delete 一般根据id删除，所以需要在接口中明确需要删除的id，这个id通过PathVariable映射给id参数
        CommonResp resp = new CommonResp<>();
        List<Long> list = Arrays.asList(ids.split(",")).stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
        docService.delete(list);
        return resp;
    }
}
