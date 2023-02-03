package com.tybbt.knowledgebase.job;

import com.tybbt.knowledgebase.service.DocService;
import com.tybbt.knowledgebase.service.EbookSnapshotService;
import com.tybbt.knowledgebase.util.SnowFlake;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class EbookSnapshotJob {

    private static final Logger LOG = LoggerFactory.getLogger(EbookSnapshotJob.class);
    @Resource
    private SnowFlake snowFlake;
    @Resource
    private EbookSnapshotService ebookSnapshotService;
    /**
     * 自定义cron表达式跑批
     * 每30秒更新电子书信息
     */
    @Scheduled(cron = "0/5 * * * * ?")
    public void cron() {
        // 增加日志流水号
        MDC.put("LOG_ID", String.valueOf(snowFlake.nextId()));
        LOG.info("生成电子书快照开始");
        long startTime = System.currentTimeMillis();
        ebookSnapshotService.getSnapshot();
        LOG.info("生成电子书快照结束，耗时 {} 毫秒", System.currentTimeMillis() - startTime);
    }

}
