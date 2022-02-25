package com.kk.wiki.service;

import com.kk.wiki.domain.EbookSnapshot;
import com.kk.wiki.domain.EbookSnapshotExample;
import com.kk.wiki.mapper.EbookSnapshotMapper;
import com.kk.wiki.mapper.EbookSnapshotMapperCust;
import com.kk.wiki.resp.StatisticResp;
import com.kk.wiki.utils.CopyUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class EbookSnapshotService {
    @Resource
    private EbookSnapshotMapperCust ebookSnapshotMapperCust;

    public void genSnapshot() {
        ebookSnapshotMapperCust.genSnapshot();
    }

    public List<StatisticResp> getStatistic() {
        return ebookSnapshotMapperCust.getStatistic();
    }

    public List<StatisticResp> get30Statistic() {
        return ebookSnapshotMapperCust.get30Statistic();
    }
}
