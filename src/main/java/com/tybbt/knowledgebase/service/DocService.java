package com.tybbt.knowledgebase.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tybbt.knowledgebase.domain.Doc;
import com.tybbt.knowledgebase.domain.DocExample;
import com.tybbt.knowledgebase.mapper.DocMapper;
import com.tybbt.knowledgebase.req.DocQueryReq;
import com.tybbt.knowledgebase.req.DocSaveReq;
import com.tybbt.knowledgebase.resp.DocQueryResp;
import com.tybbt.knowledgebase.resp.PageResp;
import com.tybbt.knowledgebase.util.CopyUtil;
import com.tybbt.knowledgebase.util.SnowFlake;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
public class DocService {
    private static final Logger LOG = LoggerFactory.getLogger(DocService.class);

    @Resource
    private DocMapper docMapper;

    @Resource
    private SnowFlake snowFlake;

    public PageResp<DocQueryResp> list(DocQueryReq req){

        DocExample docExample = new DocExample();
        DocExample.Criteria criteria = docExample.createCriteria();
        PageHelper.startPage(req.getPage(), req.getSize());
        // 调用DocMapper的list方法
        List<Doc> docslist = docMapper.selectByExample(docExample);

        PageInfo<Doc> pageInfo = new PageInfo<>(docslist);
        LOG.info("总行数： {}", pageInfo.getTotal());
        LOG.info("总页数： {}", pageInfo.getPages());

        // 实体类转换，从返回的实体类转换为封装的返回实体类
//        List<DocResp> respList = new ArrayList<>();
//        for (Doc doc : docslist) {
////            DocResp docResp = new DocResp();
////            BeanUtils.copyProperties(doc, docResp);
        //    对象赋值
//            DocResp docResp = CopyUtil.copy(doc, DocResp.class);
//            respList.add(docResp);
//        }

        // 列表复制
        List<DocQueryResp> list = CopyUtil.copyList(docslist, DocQueryResp.class);

        PageResp<DocQueryResp> pageResp = new PageResp();

        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);

        return pageResp;
    }

    public void save(DocSaveReq req) {
        Doc doc = CopyUtil.copy(req, Doc.class);
        if (ObjectUtils.isEmpty(doc.getId())) {
            // 自增，uuid，雪花
            doc.setId(snowFlake.nextId());
            docMapper.insert(doc);
        } else {
            docMapper.updateByPrimaryKey(doc);
        }
    }

    public void delete(Long id) {
        docMapper.deleteByPrimaryKey(id);
    }

    public List<DocQueryResp> all(){
        DocExample docExample = new DocExample();
        docExample.setOrderByClause("sort asc");
        // 调用DocMapper的list方法
        List<Doc> docslist = docMapper.selectByExample(docExample);

        // 列表复制
        List<DocQueryResp> list = CopyUtil.copyList(docslist, DocQueryResp.class);

        return list;
    }
}
