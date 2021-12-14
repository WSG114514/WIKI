package com.kk.wiki.controller;

import com.kk.wiki.domain.Ebook;
import com.kk.wiki.req.EbookReq;
import com.kk.wiki.resp.CommonResp;
import com.kk.wiki.resp.EbookResp;
import com.kk.wiki.service.EbookService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/ebook")
public class EbookController {

    @Resource
    public EbookService ebookService;

    @GetMapping("/list")
    public CommonResp list(EbookReq req) {
        List<EbookResp> list = ebookService.list(req);

        CommonResp<List> commonResp = new CommonResp<>();
        commonResp.setContent(list);
        commonResp.setMessage("test");
        commonResp.setSuccess(true);
        return commonResp;
    }
}
