package com.kk.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kk.wiki.domain.Content;
import com.kk.wiki.domain.Doc;
import com.kk.wiki.domain.DocExample;
import com.kk.wiki.exception.BusinessException;
import com.kk.wiki.exception.BusinessExceptionCode;
import com.kk.wiki.mapper.ContentMapper;
import com.kk.wiki.mapper.DocMapper;
import com.kk.wiki.mapper.DocMapperCust;
import com.kk.wiki.req.DocQueryReq;
import com.kk.wiki.req.DocSaveReq;
import com.kk.wiki.resp.DocQueryResp;
import com.kk.wiki.resp.PageResp;
import com.kk.wiki.utils.CopyUtil;
import com.kk.wiki.utils.RedisUtil;
import com.kk.wiki.utils.RequestContext;
import com.kk.wiki.utils.SnowFlake;
import com.kk.wiki.webSocketServer.WebSocketServer;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

@Service
public class DocService {

    @Resource
    private DocMapper docMapper;
    @Resource
    private DocMapperCust docMapperCust;
    @Resource
    private ContentMapper contentMapper;
    @Resource
    private SnowFlake snowFlake;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private WsService wsService;
    @Resource
    private RocketMQTemplate rocketMQTemplate;

    public PageResp<DocQueryResp> list(DocQueryReq req) {

        //PageHelper在查詢一次后就會失效**** （）
        PageHelper.startPage(req.getPage(), req.getSize());
        DocExample docExample = new DocExample();
        docExample.setOrderByClause("sort asc");
        //createCriteria 相当于创建一个where条件
        DocExample.Criteria criteria = docExample.createCriteria();

        List<Doc> docList = docMapper.selectByExample(docExample);
        PageInfo<Doc> pageInfo = new PageInfo<>(docList);
        //复制列表
        List<DocQueryResp> list = CopyUtil.copyList(docList, DocQueryResp.class);

        PageResp<DocQueryResp> pageResp = new PageResp();
        pageResp.setList(list);
        pageResp.setTotal(pageInfo.getTotal());

        return pageResp;
    }

    /**
     * 查询所有
     * @return
     */
    public List<DocQueryResp> all(Long ebookId) {

        DocExample docExample = new DocExample();
        docExample.setOrderByClause("sort asc");
        docExample.createCriteria().andEbookIdEqualTo(ebookId);
        List<Doc> docList = docMapper.selectByExample(docExample);

        //复制列表
        List<DocQueryResp> list = CopyUtil.copyList(docList, DocQueryResp.class);

        return list;
    }

    /**
     * 保存
     */
    @Transactional
    public void save(DocSaveReq req) {
        Doc doc = CopyUtil.copy(req, Doc.class);
        Content content = CopyUtil.copy(req, Content.class);
        if (ObjectUtils.isEmpty(req.getId())) {
            // 新增
            doc.setViewCount(0);
            doc.setVoteCount(0);
            doc.setId(snowFlake.nextId());
            docMapper.insert(doc);
            content.setId(doc.getId());
            contentMapper.insert(content);
        }else {
            // 更新
            docMapper.updateByPrimaryKey(doc);
            //blobs是大字段
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
        // 文档阅读数+1
        docMapperCust.updataViewCount(id);
        return ObjectUtils.isEmpty(content)?"":content.getContent();
    }

    /**
     * 点赞
     * @param id
     */
    public void updataVote(Long id) {
        // docMapperCust.updataVoteCount(id);
        String key = RequestContext.getRemoteAddr();
        if (redisUtil.validateRepeat("DOC_VOTE" + id + "_" + key, 3600*24)) {
            docMapperCust.updataVoteCount(id);
        }else {
            throw new BusinessException(BusinessExceptionCode.VOTE_REPEAT);
        }
        
        // 推送消息
        Doc docBD = docMapper.selectByPrimaryKey(id);
        String logId = MDC.get("LOG_ID");
        wsService.sendInfo("【" + docBD.getName() + "】被点赞!", logId);
        //MQ发送方
//        rocketMQTemplate.convertAndSend("VOTE_TOPIC", "【" + docBD.getName() + "】被点赞!");
    }

    public void updataEbookInfo() {
        docMapperCust.updataEbookInfo();
    }
}
