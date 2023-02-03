package com.tybbt.knowledgebase.mapper;

import com.tybbt.knowledgebase.resp.StatisticResp;

import java.util.List;

public interface EbookSnapshotMapperCust {
    void getSnapshot();

    List<StatisticResp> getStatistic();
}