package com.kk.wiki.controller;

import com.kk.wiki.resp.CommonResp;
import com.kk.wiki.resp.StatisticResp;
import com.kk.wiki.service.EbookSnapshotService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/ebook-snapshot")
public class EbookSnapshotController {

    @Resource
    private EbookSnapshotService ebookSnapshotService;

    @GetMapping("/get-snapshot")
    public CommonResp getStatistic() {
        CommonResp<List<StatisticResp>> commonResp = new CommonResp<>();
        List<StatisticResp> statisticResps = ebookSnapshotService.getStatistic();
        commonResp.setContent(statisticResps);
        return commonResp;
    }

    @GetMapping("/get-30-statistic")
    public CommonResp get30Statistic() {
        CommonResp<List<StatisticResp>> commonResp = new CommonResp<>();
        List<StatisticResp> statisticResps = ebookSnapshotService.get30Statistic();
        commonResp.setContent(statisticResps);
        return commonResp;
    }
}
