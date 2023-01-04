package com.tybbt.knowledgebase.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// @RestController 用于返回一个字符串，一般是Json对象 | @Controller用于返回一个页面
@RestController
public class TestController {
    // http://127.0.0.1:8080/hello
    /**
     *  http的八种请求方式：GET POST PUT DELETE OPTIONS PATCH TRACE HEAD
     *  例如对于传统get请求：
     *      一般格式：/user?id=1  RESFUL格式：/user/1
     *  如果用requestmapping注解，表示该方法接受所有四种请求
     *  而如果想只允许GET， 则转用GetMapping注解，括号内添加args或添加参数为（value="/hello", method=RequestMethod.GET）
     */
    @RequestMapping("/hello")
    public String hello() {
        return "Hello World！";
    }
}
