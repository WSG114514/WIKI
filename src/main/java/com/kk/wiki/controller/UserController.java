package com.kk.wiki.controller;

import com.kk.wiki.req.UserQueryReq;
import com.kk.wiki.req.UserResetPasswordReq;
import com.kk.wiki.req.UserSaveReq;
import com.kk.wiki.resp.CommonResp;
import com.kk.wiki.resp.UserQueryResp;
import com.kk.wiki.resp.PageResp;
import com.kk.wiki.service.UserService;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    public UserService userService;

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
}
