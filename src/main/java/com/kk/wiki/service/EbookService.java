package com.kk.wiki.service;

import com.github.pagehelper.PageHelper;
import com.kk.wiki.domain.Ebook;
import com.kk.wiki.domain.EbookExample;
import com.kk.wiki.mapper.EbookMapper;
import com.kk.wiki.req.EbookReq;
import com.kk.wiki.resp.EbookResp;
import com.kk.wiki.utils.CopyUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class EbookService {

    @Resource
    private EbookMapper ebookMapper;

    public List<EbookResp> list(EbookReq req) {

        //PageHelper在查詢一次后就會失效**** （）
        PageHelper.startPage(2, 3);
        EbookExample ebookExample = new EbookExample();
        //createCriteria 相当于创建一个where条件
        EbookExample.Criteria criteria = ebookExample.createCriteria();

        if(!ObjectUtils.isEmpty(req.getName())) {
            criteria.andNameLike("%" + req.getName() + "%");
        }

        List<Ebook> ebookList = ebookMapper.selectByExample(ebookExample);

        return CopyUtil.copyList(ebookList, EbookResp.class);
    }
}
