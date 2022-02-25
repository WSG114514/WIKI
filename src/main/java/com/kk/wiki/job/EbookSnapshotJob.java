package com.kk.wiki.job;

import com.kk.wiki.mapper.EbookSnapshotMapperCust;
import com.kk.wiki.service.EbookSnapshotService;
import com.kk.wiki.utils.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class EbookSnapshotJob {

    private static final Logger LOG = LoggerFactory.getLogger(DocJob.class);

    @Resource
    private EbookSnapshotService ebookSnapshotService;
    @Resource
    private SnowFlake snowFlake;

    @Scheduled(cron = "* 0/3 * * * ? ")
    private void genSnapshot() {
        MDC.put("LOG_ID", String.valueOf(snowFlake.nextId()));
        LOG.info("生成电子书快照开始");
        Long start = System.currentTimeMillis();
        ebookSnapshotService.genSnapshot();
        LOG.info("生成今日电子书快照结束，耗时:{}毫秒", System.currentTimeMillis() - start);
    }
}
