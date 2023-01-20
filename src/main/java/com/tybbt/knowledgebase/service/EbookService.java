package com.tybbt.knowledgebase.service;

import com.tybbt.knowledgebase.domain.Ebook;
import com.tybbt.knowledgebase.mapper.EbookMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EbookService {

    @Resource
    private EbookMapper ebookMapper;
    public List<Ebook> list(){
        // 调用EbookMapper的list方法
        return ebookMapper.selectByExample(null);
    }
}
