package com.tybbt.knowledgebase.service;

import com.tybbt.knowledgebase.domain.Ebook;
import com.tybbt.knowledgebase.domain.EbookExample;
import com.tybbt.knowledgebase.mapper.EbookMapper;
import com.tybbt.knowledgebase.req.EbookQueryReq;
import com.tybbt.knowledgebase.req.EbookSaveReq;
import com.tybbt.knowledgebase.resp.EbookQueryResp;
import com.tybbt.knowledgebase.util.CopyUtil;
import com.tybbt.knowledgebase.util.SnowFlake;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
public class EbookService {

    @Resource
    private EbookMapper ebookMapper;

    @Resource
    private SnowFlake snowFlake;
    public List<EbookQueryResp> list(EbookQueryReq req){
        EbookExample ebookExample = new EbookExample();
        EbookExample.Criteria criteria = ebookExample.createCriteria();
        if (!ObjectUtils.isEmpty(req.getName())) {
            criteria.andNameLike("%" + req.getName() + "%");
        }

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
        List<EbookQueryResp> list = CopyUtil.copyList(ebookslist, EbookQueryResp.class);
        return list;
    }

    public void save(EbookSaveReq req) {
        Ebook ebook = CopyUtil.copy(req, Ebook.class);
        if (ObjectUtils.isEmpty(ebook.getId())) {
            // 自增，uuid，雪花
            ebook.setId(snowFlake.nextId());
            ebookMapper.insert(ebook);
        } else {
            ebookMapper.updateByPrimaryKey(ebook);
        }

    }
}
