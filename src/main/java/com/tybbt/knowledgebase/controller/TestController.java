package com.tybbt.knowledgebase.controller;

import com.tybbt.knowledgebase.domain.Test;
import com.tybbt.knowledgebase.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// @RestController 用于返回一个字符串，一般是Json对象 | @Controller用于返回一个页面
@RestController
public class TestController {
    // 用于加载application自定义配置项，使用 custom.args:Default 作为默认配置防止转移环境时忘记配置
    @Value("${test.hello:TEST}")
    private String testHello;

    @Autowired
    private TestService testService;
    // http://127.0.0.1:8080/hello
    /**
     *  http的八种请求方式：GET POST PUT DELETE OPTIONS PATCH TRACE HEAD
     *  例如对于传统get请求：
     *      一般格式：/user?id=1  RESFUL格式：/user/1
     *  如果用requestmapping注解，表示该方法接受所有四种请求
     *  而如果想只允许GET， 则转用GetMapping注解，括号内添加args或添加参数为（value="/hello", method=RequestMethod.GET）
     *  同时使用浏览器测试时，只能测试get请求
     */
    @RequestMapping("/hello")
    public String hello() {
        return "Hello World!" + testHello;
    }

    @PostMapping("/hello/post")
    public String posthello(String name) {
        return "Posted Hello World!" + name;
    }

    // 程序接口入口 -> 调用testService 的list方法
    @RequestMapping("/test/list")
    public List<Test> list(){
        return testService.list();
    }
}
