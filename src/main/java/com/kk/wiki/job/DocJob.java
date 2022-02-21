package com.kk.wiki.job;

import com.kk.wiki.service.DocService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class DocJob {
    @Resource
    DocService docService;

    private static final Logger LOG = LoggerFactory.getLogger(DocJob.class);

    // 每五分钟更新Ebook
    @Scheduled(cron = "0 0/3 * * * ? ")
    public void cron() {
        LOG.info("更新电子书下的数据开始");
        long start = System.currentTimeMillis();
        docService.updataEbookInfo();
        LOG.info("更新电子书下的文档数据结束:耗时:{}毫秒", System.currentTimeMillis()-start);
    }
}
