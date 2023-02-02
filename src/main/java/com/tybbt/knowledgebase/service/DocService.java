package com.tybbt.knowledgebase.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tybbt.knowledgebase.domain.Content;
import com.tybbt.knowledgebase.domain.Doc;
import com.tybbt.knowledgebase.domain.DocExample;
import com.tybbt.knowledgebase.exception.BusinessException;
import com.tybbt.knowledgebase.exception.BusinessExceptionCode;
import com.tybbt.knowledgebase.mapper.ContentMapper;
import com.tybbt.knowledgebase.mapper.DocMapper;
import com.tybbt.knowledgebase.mapper.DocMapperCust;
import com.tybbt.knowledgebase.req.DocQueryReq;
import com.tybbt.knowledgebase.req.DocSaveReq;
import com.tybbt.knowledgebase.resp.DocQueryResp;
import com.tybbt.knowledgebase.resp.PageResp;
import com.tybbt.knowledgebase.util.CopyUtil;
import com.tybbt.knowledgebase.util.RedisUtil;
import com.tybbt.knowledgebase.util.RequestContext;
import com.tybbt.knowledgebase.util.SnowFlake;
import com.tybbt.knowledgebase.websocket.WebSocketServer;
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

    @Resource
    private ContentMapper contentMapper;

    @Resource
    private DocMapperCust docMapperCust;

    @Resource
    public RedisUtil redisUtil;

    @Resource
    private WebSocketServer webSocketServer;


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
        Content content = CopyUtil.copy(req, Content.class);
        if (ObjectUtils.isEmpty(doc.getId())) {
            // 自增，uuid，雪花
            long newId = snowFlake.nextId();
            doc.setId(newId);
            doc.setViewCount(0);
            doc.setVoteCount(0);
            docMapper.insert(doc);

            content.setId(newId);
            contentMapper.insert(content);

        } else {
            docMapper.updateByPrimaryKey(doc);
            int count = contentMapper.updateByPrimaryKeyWithBLOBs(content);
            if (count == 0) {
                contentMapper.insert(content);
            }
        }
    }

    public void delete(Long id) {
        docMapper.deleteByPrimaryKey(id);
    }
    public void delete(List<Long> ids) {
        DocExample docExample = new DocExample();
        DocExample.Criteria criteria = docExample.createCriteria();
        criteria.andIdIn(ids);
        docMapper.deleteByExample(docExample);
    }
    public String findContent(Long id) {
        Content content = contentMapper.selectByPrimaryKey(id);
        // 文档阅读数 + 1
        docMapperCust.increaseViewCount(id);
        if (ObjectUtils.isEmpty(content)) {
            return "";
        }
        return content.getContent();
    }

    public List<DocQueryResp> all(long ebookId){
        DocExample docExample = new DocExample();
        docExample.createCriteria().andEbookIdEqualTo(ebookId);
        docExample.setOrderByClause("sort asc");

        // 调用DocMapper的list方法
        List<Doc> docslist = docMapper.selectByExample(docExample);

        // 列表复制
        List<DocQueryResp> list = CopyUtil.copyList(docslist, DocQueryResp.class);

        return list;
    }

    public void vote(Long id) {
        // 使用远程ip + doc.id 作为key存入redis做校验，防止24小时内的重复点赞
        String ip = RequestContext.getRemoteAddr(); // 远程ip
        if (redisUtil.validateRepeat("DOC_VOTE_" + id + "_" + ip, 3600*24)){
            docMapperCust.increaseVoteCount(id);
        } else {
            throw new BusinessException(BusinessExceptionCode.VOTE_REPEAT);
        }

        // 推送消息
        Doc docDb = docMapper.selectByPrimaryKey(id);
        webSocketServer.sendInfo("【" + docDb.getName() + "】收获一个点赞！");
    }



    public void updateEbookInfo() {
        docMapperCust.updateEbookInfo();
    }
}
