package com.kk.wiki.controller;

import com.kk.wiki.domain.Content;
import com.kk.wiki.req.DocQueryReq;
import com.kk.wiki.req.DocSaveReq;
import com.kk.wiki.resp.DocQueryResp;
import com.kk.wiki.resp.CommonResp;
import com.kk.wiki.resp.PageResp;
import com.kk.wiki.service.DocService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/doc")
public class DocController {

    @Resource
    public DocService docService;

    @GetMapping("/list")
    public CommonResp list(@Valid DocQueryReq req) {
        PageResp<DocQueryResp> list = docService.list(req);

        CommonResp<PageResp<DocQueryResp>> commonResp = new CommonResp<>();
        commonResp.setContent(list);
        commonResp.setMessage("test");
        commonResp.setSuccess(true);
        return commonResp;
    }

    @GetMapping("/all/{ebookId}")
    public CommonResp all(@PathVariable Long ebookId) {
        List<DocQueryResp> list = docService.all(ebookId);

        CommonResp<List<DocQueryResp>> commonResp = new CommonResp<>();
        commonResp.setContent(list);
        commonResp.setMessage("test");
        commonResp.setSuccess(true);
        return commonResp;
    }

    @GetMapping("/onserch")
    public CommonResp onserch(DocQueryReq req) {
        PageResp<DocQueryResp> list = docService.list(req);

        CommonResp<PageResp<DocQueryResp>> commonResp = new CommonResp<>();
        commonResp.setContent(list);
        return commonResp;
    }

    @PostMapping("/save")
    public CommonResp save(@RequestBody @Valid DocSaveReq req) {
        CommonResp commonResp = new CommonResp();
        docService.save(req);
        return commonResp;
    }

    @DeleteMapping("/delete/{idsStr}")
    public CommonResp delete(@PathVariable String idsStr) {
        CommonResp commonResp = new CommonResp();
        String[] ids = idsStr.split(",");
        List<String> listStr = Arrays.asList(ids);
        List<Long> listLg = new ArrayList<>();
        listStr.forEach(item-> {
            listLg.add(Long.parseLong(item));
        });

        docService.delete(listLg);
        return commonResp;
    }

    @GetMapping("/find-content/{id}")
    public CommonResp findContent(@PathVariable Long id) {
        CommonResp<String> resp = new CommonResp<>();
        String content = docService.findContent(id);
        resp.setContent(content);
        return resp;
    }
}
