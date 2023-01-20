package com.tybbt.knowledgebase.service;

import com.tybbt.knowledgebase.domain.Ebook;
import com.tybbt.knowledgebase.domain.EbookExample;
import com.tybbt.knowledgebase.mapper.EbookMapper;
import com.tybbt.knowledgebase.req.EbookReq;
import com.tybbt.knowledgebase.resp.EbookResp;
import com.tybbt.knowledgebase.util.CopyUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

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

        // 实体类转换，从返回的实体类转换为封装的返回实体类
//        List<EbookResp> respList = new ArrayList<>();
//        for (Ebook ebook : ebookslist) {
////            EbookResp ebookResp = new EbookResp();
////            BeanUtils.copyProperties(ebook, ebookResp);
        //    对象赋值
//            EbookResp ebookResp = CopyUtil.copy(ebook, EbookResp.class);
//            respList.add(ebookResp);
//        }
        // 列表复制
        List<EbookResp> list = CopyUtil.copyList(ebookslist, EbookResp.class);
        return list;
    }
}
