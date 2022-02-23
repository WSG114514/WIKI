package com.kk.wiki.mapper;

import com.kk.wiki.resp.StatisticResp;

import java.util.List;

public interface EbookSnapshotMapperCust {

    public void genSnapshot();

    public List<StatisticResp> getStatistic();
}
