package com.tybbt.knowledgebase.controller;

import com.tybbt.knowledgebase.resp.CommonResp;
import com.tybbt.knowledgebase.resp.StatisticResp;
import com.tybbt.knowledgebase.service.EbookSnapshotService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// @RestController 用于返回一个字符串，一般是Json对象 | @Controller用于返回一个页面
// 在外层类增加@RequestMapping注解，可以直接作为公共的上层链接，后续内部无论使用GET POST都可以作为请求的前缀
@RestController
@RequestMapping("/ebook-snapshot")
public class EbookSnapshotController {
    private static final Logger LOG = LoggerFactory.getLogger(EbookSnapshotController.class);
    // 用于加载application自定义配置项，使用 custom.args:Default 作为默认配置防止转移环境时忘记配置
    @Resource
    private EbookSnapshotService ebookSnapshotService;

    // 程序接口入口 -> 调用demoService 的list方法
    @RequestMapping("/get-statistic")
    public CommonResp getStatistic() {
        List<StatisticResp> statisticResp = ebookSnapshotService.getStatistic();
        LOG.info("查询首页统计数据：{}", statisticResp);
        CommonResp<List<StatisticResp>> commonResp = new CommonResp<>();
        commonResp.setContent(statisticResp);
        return commonResp;
    }

    @RequestMapping("/get-30-statistic")
    public CommonResp get30Statistic() {
        List<StatisticResp> statisticResp = ebookSnapshotService.get30Statistic();
        CommonResp<List<StatisticResp>> commonResp = new CommonResp<>();
        commonResp.setContent(statisticResp);
        return commonResp;
    }
}
