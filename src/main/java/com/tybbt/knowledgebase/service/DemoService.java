package com.tybbt.knowledgebase.service;

import com.tybbt.knowledgebase.domain.Demo;
import com.tybbt.knowledgebase.mapper.DemoMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DemoService {

    @Resource
    private DemoMapper demoMapper;
    public List<Demo> list(){
        // 调用DemoMapper的list方法
        return demoMapper.selectByExample(null);
    }
}
