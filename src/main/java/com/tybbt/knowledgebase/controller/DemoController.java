package com.tybbt.knowledgebase.controller;

import com.tybbt.knowledgebase.domain.Demo;
import com.tybbt.knowledgebase.service.DemoService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// @RestController 用于返回一个字符串，一般是Json对象 | @Controller用于返回一个页面
// 在外层类增加@RequestMapping注解，可以直接作为公共的上层链接，后续内部无论使用GET POST都可以作为请求的前缀
@RestController
@RequestMapping("/demo")
public class DemoController {
    // 用于加载application自定义配置项，使用 custom.args:Default 作为默认配置防止转移环境时忘记配置
    @Resource
    private DemoService demoService;

    // 程序接口入口 -> 调用demoService 的list方法
    @RequestMapping("/list")
    public List<Demo> list(){
        return demoService.list();
    }
}
