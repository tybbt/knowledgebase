package com.tybbt.knowledgebase.controller;

import com.alibaba.fastjson.JSONObject;
import com.tybbt.knowledgebase.req.UserLoginReq;
import com.tybbt.knowledgebase.req.UserQueryReq;
import com.tybbt.knowledgebase.req.UserResetPasswordReq;
import com.tybbt.knowledgebase.req.UserSaveReq;
import com.tybbt.knowledgebase.resp.CommonResp;
import com.tybbt.knowledgebase.resp.PageResp;
import com.tybbt.knowledgebase.resp.UserLoginResp;
import com.tybbt.knowledgebase.resp.UserQueryResp;
import com.tybbt.knowledgebase.service.UserService;
import com.tybbt.knowledgebase.util.SnowFlake;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

// @RestController 用于返回一个字符串，一般是Json对象 | @Controller用于返回一个页面
// 在外层类增加@RequestMapping注解，可以直接作为公共的上层链接，后续内部无论使用GET POST都可以作为请求的前缀
@RestController
@RequestMapping("/user")
public class UserController {
    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);
    
    // 用于加载application自定义配置项，使用 custom.args:Default 作为默认配置防止转移环境时忘记配置
    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate<Object, Object> redisTemplate;

    @Resource
    private SnowFlake snowFlake;

    // 程序接口入口 -> 调用userService 的list方法
    @GetMapping ("/list")
    public CommonResp<PageResp<UserQueryResp>> list(@Valid UserQueryReq req){
        // 前后端参数名称需一致，才能自动映射。
        CommonResp<PageResp<UserQueryResp>> response = new CommonResp<>();
        PageResp<UserQueryResp> list = userService.list(req);
        response.setContent(list);
        return response;
    }

    @PostMapping("/save")
    public CommonResp save(@Valid @RequestBody UserSaveReq req){
        // 前端如果使用POST提交中Content-Type为application/json方式，则需要RequestBody注解包裹request才能处理，axios使用json
        // 如果利用application/x-www-form的方式提交则不需要加注解
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        CommonResp resp = new CommonResp<>();
        userService.save(req);
        return resp;
    }

    @PostMapping("/reset-password")
    public CommonResp resetPassword(@Valid @RequestBody UserResetPasswordReq req){
        // 前端如果使用POST提交中Content-Type为application/json方式，则需要RequestBody注解包裹request才能处理，axios使用json
        // 如果利用application/x-www-form的方式提交则不需要加注解
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        CommonResp resp = new CommonResp<>();
        userService.resetPassword(req);
        return resp;
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp delete(@PathVariable Long id) {
        // delete 一般根据id删除，所以需要在接口中明确需要删除的id，这个id通过PathVariable映射给id参数
        CommonResp resp = new CommonResp<>();
        userService.delete(id);
        return resp;
    }

    @PostMapping("/login")
    public CommonResp<UserLoginResp> login(@Valid @RequestBody UserLoginReq req){
        // 前端如果使用POST提交中Content-Type为application/json方式，则需要RequestBody注解包裹request才能处理，axios使用json
        // 如果利用application/x-www-form的方式提交则不需要加注解
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        CommonResp<UserLoginResp> resp = new CommonResp<>();
        UserLoginResp userLoginResp = userService.login(req);

        // 生成单点登录token，放入redis
        Long token = snowFlake.nextId();
        LOG.info("生成单点登录token: {}，放入redis, 值为 {}", token.toString(), JSONObject.toJSONString(userLoginResp));
        userLoginResp.setToken(token.toString());
        redisTemplate.opsForValue().set(token.toString(), JSONObject.toJSONString(userLoginResp), 3600*24, TimeUnit.SECONDS);
        Object object = redisTemplate.keys("*");
        LOG.info("GET Redis All keys: {}", object);
        resp.setContent(userLoginResp);
        return resp;
    }

    @GetMapping("/login/all")
    public Object all() {
        Object object = redisTemplate.keys("*");
        LOG.info("GET Redis All keys: {}", object);
        return object;
    }

    @GetMapping("/logout/{token}")
    private CommonResp logout(@PathVariable String token) {
        CommonResp resp = new CommonResp<>();
        LOG.info("从redis中删除token {}", token);
        redisTemplate.delete(token);
        return resp;
    }
}
