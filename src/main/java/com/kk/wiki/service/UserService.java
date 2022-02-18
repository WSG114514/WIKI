package com.kk.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kk.wiki.domain.User;
import com.kk.wiki.domain.UserExample;
import com.kk.wiki.mapper.UserMapper;
import com.kk.wiki.req.UserQueryReq;
import com.kk.wiki.req.UserSaveReq;
import com.kk.wiki.resp.UserQueryResp;
import com.kk.wiki.resp.PageResp;
import com.kk.wiki.utils.CopyUtil;
import com.kk.wiki.utils.SnowFlake;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private SnowFlake snowFlake;

    public PageResp<UserQueryResp> list(UserQueryReq req) {

        //PageHelper在查詢一次后就會失效**** （）
        PageHelper.startPage(req.getPage(), req.getSize());
        UserExample userExample = new UserExample();
        //createCriteria 相当于创建一个where条件
        UserExample.Criteria criteria = userExample.createCriteria();

        if(!ObjectUtils.isEmpty(req.getLoginName())) {
            criteria.andLoginNameEqualTo(req.getLoginName());
        }

        List<User> userList = userMapper.selectByExample(userExample);
        PageInfo<User> pageInfo = new PageInfo<>(userList);
        //复制列表
        List<UserQueryResp> list = CopyUtil.copyList(userList, UserQueryResp.class);

        PageResp<UserQueryResp> pageResp = new PageResp();
        pageResp.setList(list);
        pageResp.setTotal(pageInfo.getTotal());

        return pageResp;
    }

    /**
     * 保存
     */
    public void save(UserSaveReq req) {
        User user = CopyUtil.copy(req, User.class);
        if (ObjectUtils.isEmpty(req.getId())) {
            // 新增
            user.setId(snowFlake.nextId());
            userMapper.insert(user);
        }else {
            // 更新
            userMapper.updateByPrimaryKey(user);
        }
    }

    public void delete(Long id) {
        userMapper.deleteByPrimaryKey(id);
    }
}
