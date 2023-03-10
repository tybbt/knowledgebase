package com.tybbt.knowledgebase.mapper;

import com.tybbt.knowledgebase.domain.Test;

import java.util.List;

public interface TestMapper {
    // 由 Mapper 影响到对应的Mapper XML文件，执行xml语句， 完成一次数据持久化相关操作
    public List<Test> list();
}
