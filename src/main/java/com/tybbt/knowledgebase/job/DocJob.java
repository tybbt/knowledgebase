package com.tybbt.knowledgebase.job;

import com.tybbt.knowledgebase.service.DocService;
import com.tybbt.knowledgebase.util.SnowFlake;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DocJob {

    private static final Logger LOG = LoggerFactory.getLogger(DocJob.class);
    @Resource
    private SnowFlake snowFlake;
    @Resource
    private DocService docService;
    /**
     * 自定义cron表达式跑批
     * 每30秒更新电子书信息
     */
    @Scheduled(cron = "5/30 * * * * ?")
    public void cron() {
        // 增加日志流水号
        MDC.put("LOG_ID", String.valueOf(snowFlake.nextId()));
        LOG.info("更新电子书下的文档数据开始");
        long startTime = System.currentTimeMillis();
        docService.updateEbookInfo();
        LOG.info("更新电子书下的文档数据结束，耗时 {} 毫秒", System.currentTimeMillis() - startTime);
    }

}
