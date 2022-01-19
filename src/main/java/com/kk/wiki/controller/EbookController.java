package com.kk.wiki.controller;

import com.kk.wiki.domain.Ebook;
import com.kk.wiki.req.EbookQueryReq;
import com.kk.wiki.req.EbookSaveReq;
import com.kk.wiki.resp.CommonResp;
import com.kk.wiki.resp.EbookQueryResp;
import com.kk.wiki.resp.PageResp;
import com.kk.wiki.service.EbookService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/ebook")
public class EbookController {

    @Resource
    public EbookService ebookService;

    @GetMapping("/list")
    public CommonResp list(@Valid EbookQueryReq req) {
        PageResp<EbookQueryResp> list = ebookService.list(req);

        CommonResp<PageResp<EbookQueryResp>> commonResp = new CommonResp<>();
        commonResp.setContent(list);
        commonResp.setMessage("test");
        commonResp.setSuccess(true);
        return commonResp;
    }

    @PostMapping("/save")
    public CommonResp save(@RequestBody @Valid EbookSaveReq req) {
        CommonResp commonResp = new CommonResp();
        ebookService.save(req);
        return commonResp;
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp delete(@PathVariable Long id) {
        CommonResp commonResp = new CommonResp();
        ebookService.delete(id);
        return commonResp;
    }
}
