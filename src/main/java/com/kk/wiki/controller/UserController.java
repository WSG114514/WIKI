package com.kk.wiki.controller;

import com.alibaba.fastjson.JSONObject;
import com.kk.wiki.req.UserLoginReq;
import com.kk.wiki.req.UserQueryReq;
import com.kk.wiki.req.UserResetPasswordReq;
import com.kk.wiki.req.UserSaveReq;
import com.kk.wiki.resp.CommonResp;
import com.kk.wiki.resp.UserLoginResp;
import com.kk.wiki.resp.UserQueryResp;
import com.kk.wiki.resp.PageResp;
import com.kk.wiki.service.UserService;
import com.kk.wiki.utils.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger LOG= LoggerFactory.getLogger(UserController.class);
    @Resource
    public UserService userService;
    @Resource
    public RedisTemplate redisTemplate;
    @Resource
    public SnowFlake snowFlake;

    @GetMapping("/list")
    public CommonResp list(@Valid UserQueryReq req) {
        PageResp<UserQueryResp> list = userService.list(req);

        CommonResp<PageResp<UserQueryResp>> commonResp = new CommonResp<>();
        commonResp.setContent(list);
        commonResp.setMessage("test");
        commonResp.setSuccess(true);
        return commonResp;
    }

    @GetMapping("/onserch")
    public CommonResp onserch(UserQueryReq req) {
        PageResp<UserQueryResp> list = userService.list(req);

        CommonResp<PageResp<UserQueryResp>> commonResp = new CommonResp<>();
        commonResp.setContent(list);
        return commonResp;
    }

    @PostMapping("/save")
    public CommonResp save(@RequestBody @Valid UserSaveReq req) {
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        CommonResp commonResp = new CommonResp();
        userService.save(req);
        return commonResp;
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp delete(@PathVariable Long id) {
        CommonResp commonResp = new CommonResp();
        userService.delete(id);
        return commonResp;
    }

    @PostMapping("/reset-password")
    public CommonResp resetPassword(@RequestBody @Valid UserResetPasswordReq req) {
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        CommonResp commonResp = new CommonResp();
        userService.resetPassword(req);
        return commonResp;
    }

    @PostMapping("/login")
    public CommonResp login(@RequestBody @Valid UserLoginReq req) {
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        CommonResp commonResp = new CommonResp();
        UserLoginResp userLoginResp = userService.login(req);

        long taken = snowFlake.nextId();
        LOG.info("单点登录token:{}，并放入redis", taken);
        userLoginResp.setToken(Long.toString(taken));
        redisTemplate.opsForValue().set(taken, JSONObject.toJSON(userLoginResp), 3600 * 4, TimeUnit.SECONDS);

        commonResp.setContent(userLoginResp);
        return commonResp;
    }

    @GetMapping("/logout/{token}")
    public CommonResp logout(@PathVariable String token) {
        CommonResp commonResp = new CommonResp();
        redisTemplate.delete(token);
        return commonResp;
    }
}
