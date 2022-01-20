package com.kk.wiki.controller;

import com.kk.wiki.req.CategoryQueryReq;
import com.kk.wiki.req.CategorySaveReq;
import com.kk.wiki.resp.CommonResp;
import com.kk.wiki.resp.CategoryQueryResp;
import com.kk.wiki.resp.PageResp;
import com.kk.wiki.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Resource
    public CategoryService categoryService;

    @GetMapping("/list")
    public CommonResp list(@Valid CategoryQueryReq req) {
        PageResp<CategoryQueryResp> list = categoryService.list(req);

        CommonResp<PageResp<CategoryQueryResp>> commonResp = new CommonResp<>();
        commonResp.setContent(list);
        commonResp.setMessage("test");
        commonResp.setSuccess(true);
        return commonResp;
    }

    @GetMapping("/onserch")
    public CommonResp onserch(CategoryQueryReq req) {
        PageResp<CategoryQueryResp> list = categoryService.list(req);

        CommonResp<PageResp<CategoryQueryResp>> commonResp = new CommonResp<>();
        commonResp.setContent(list);
        return commonResp;
    }

    @PostMapping("/save")
    public CommonResp save(@RequestBody @Valid CategorySaveReq req) {
        CommonResp commonResp = new CommonResp();
        categoryService.save(req);
        return commonResp;
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp delete(@PathVariable Long id) {
        CommonResp commonResp = new CommonResp();
        categoryService.delete(id);
        return commonResp;
    }
}
