package com.tybbt.knowledgebase.service;

import com.tybbt.knowledgebase.domain.Ebook;
import com.tybbt.knowledgebase.domain.EbookExample;
import com.tybbt.knowledgebase.mapper.EbookMapper;
import com.tybbt.knowledgebase.req.EbookReq;
import com.tybbt.knowledgebase.resp.EbookResp;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EbookService {

    @Resource
    private EbookMapper ebookMapper;
    public List<EbookResp> list(EbookReq req){
        EbookExample ebookExample = new EbookExample();
        EbookExample.Criteria criteria = ebookExample.createCriteria();
        criteria.andNameLike("%" + req.getName() + "%");
        // 调用EbookMapper的list方法
        List<Ebook> ebookslist = ebookMapper.selectByExample(ebookExample);

        List<EbookResp> respList = new ArrayList<>();
        for (Ebook ebook : ebookslist) {
            EbookResp ebookResp = new EbookResp();
            BeanUtils.copyProperties(ebook, ebookResp);
            respList.add(ebookResp);
        }

        return respList;
    }
}
