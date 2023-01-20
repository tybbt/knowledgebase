package com.tybbt.knowledgebase.controller;

import com.tybbt.knowledgebase.req.EbookReq;
import com.tybbt.knowledgebase.resp.CommonResp;
import com.tybbt.knowledgebase.resp.EbookResp;
import com.tybbt.knowledgebase.service.EbookService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// @RestController 用于返回一个字符串，一般是Json对象 | @Controller用于返回一个页面
// 在外层类增加@RequestMapping注解，可以直接作为公共的上层链接，后续内部无论使用GET POST都可以作为请求的前缀
@RestController
@RequestMapping("/ebook")
public class EbookController {
    // 用于加载application自定义配置项，使用 custom.args:Default 作为默认配置防止转移环境时忘记配置
    @Resource
    private EbookService ebookService;

    // 程序接口入口 -> 调用ebookService 的list方法
    @RequestMapping("/list")
    public CommonResp list(EbookReq req){
        CommonResp<List<EbookResp>> response = new CommonResp<>();
        List<EbookResp> list = ebookService.list(req);
        response.setContent(list);
        return response;
    }
}
