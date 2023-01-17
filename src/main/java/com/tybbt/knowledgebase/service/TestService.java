package com.tybbt.knowledgebase.service;

import com.tybbt.knowledgebase.domain.Test;
import com.tybbt.knowledgebase.mapper.TestMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestService {

    @Resource
    private TestMapper testMapper;
    public List<Test> list(){
        // 调用testMapper的list方法
        return testMapper.list();
    }
}
