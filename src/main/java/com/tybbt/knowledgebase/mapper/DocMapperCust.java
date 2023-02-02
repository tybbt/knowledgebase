package com.tybbt.knowledgebase.mapper;

import org.apache.ibatis.annotations.Param;

public interface DocMapperCust {
    // 由 Mapper 影响到对应的Mapper XML文件，执行xml语句， 完成一次数据持久化相关操作
    public void increaseViewCount(@Param("id") Long id);
}
