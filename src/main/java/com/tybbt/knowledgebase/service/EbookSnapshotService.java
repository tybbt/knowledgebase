package com.tybbt.knowledgebase.service;

import com.tybbt.knowledgebase.mapper.EbookSnapshotMapperCust;
import com.tybbt.knowledgebase.resp.StatisticResp;
import com.tybbt.knowledgebase.util.SnowFlake;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EbookSnapshotService {
    private static final Logger LOG = LoggerFactory.getLogger(EbookSnapshotService.class);

    @Resource
    private EbookSnapshotMapperCust ebookSnapshotMapperCust;

    @Resource
    private SnowFlake snowFlake;

    public void getSnapshot() {
        ebookSnapshotMapperCust.getSnapshot();
    }

    public List<StatisticResp> getStatistic() {
        List<StatisticResp> result = ebookSnapshotMapperCust.getStatistic();
        LOG.info("Got Statistic Result: {}", result);
        return result;
    }

}
