package com.tybbt.knowledgebase.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tybbt.knowledgebase.domain.User;
import com.tybbt.knowledgebase.domain.UserExample;
import com.tybbt.knowledgebase.exception.BusinessException;
import com.tybbt.knowledgebase.exception.BusinessExceptionCode;
import com.tybbt.knowledgebase.mapper.UserMapper;
import com.tybbt.knowledgebase.req.UserQueryReq;
import com.tybbt.knowledgebase.req.UserResetPasswordReq;
import com.tybbt.knowledgebase.req.UserSaveReq;
import com.tybbt.knowledgebase.resp.PageResp;
import com.tybbt.knowledgebase.resp.UserQueryResp;
import com.tybbt.knowledgebase.util.CopyUtil;
import com.tybbt.knowledgebase.util.SnowFlake;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
public class UserService {
    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    @Resource
    private UserMapper userMapper;

    @Resource
    private SnowFlake snowFlake;

    public PageResp<UserQueryResp> list(UserQueryReq req){

        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();

        if (!ObjectUtils.isEmpty(req.getLoginName())) {
            criteria.andLoginNameEqualTo(req.getLoginName());
        }

        PageHelper.startPage(req.getPage(), req.getSize());
        // 调用UserMapper的list方法
        List<User> userslist = userMapper.selectByExample(userExample);

        PageInfo<User> pageInfo = new PageInfo<>(userslist);
        LOG.info("总行数： {}", pageInfo.getTotal());
        LOG.info("总页数： {}", pageInfo.getPages());

        // 实体类转换，从返回的实体类转换为封装的返回实体类
//        List<UserResp> respList = new ArrayList<>();
//        for (User user : userslist) {
////            UserResp userResp = new UserResp();
////            BeanUtils.copyProperties(user, userResp);
        //    对象赋值
//            UserResp userResp = CopyUtil.copy(user, UserResp.class);
//            respList.add(userResp);
//        }

        // 列表复制
        List<UserQueryResp> list = CopyUtil.copyList(userslist, UserQueryResp.class);

        PageResp<UserQueryResp> pageResp = new PageResp();

        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);

        return pageResp;
    }

    public void save(UserSaveReq req) {
        User user = CopyUtil.copy(req, User.class);
        if (ObjectUtils.isEmpty(user.getId())) {
            if (ObjectUtils.isEmpty(selectByLoginName(req.getLoginName()))) {
                // 自增，uuid，雪花
                user.setId(snowFlake.nextId());
                userMapper.insert(user);
            } else {
                throw new BusinessException(BusinessExceptionCode.USER_LOGIN_NAME_EXIST);
            }

        } else {
            user.setLoginName(null);
            user.setPassword(null);
            // 当user属性中有字段为空时，则不会更新该字段
            userMapper.updateByPrimaryKeySelective(user);
        }
    }

    public void resetPassword(UserResetPasswordReq req) {
        User user = CopyUtil.copy(req, User.class);
        userMapper.updateByPrimaryKeySelective(user);
    }

    public User selectByLoginName(String loginName) {
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        criteria.andLoginNameEqualTo(loginName);
        List<User> userslist = userMapper.selectByExample(userExample);
        if (CollectionUtils.isEmpty(userslist)) {
            return null;
        } else {
            return userslist.get(0);
        }
    }

    public void delete(Long id) {
        userMapper.deleteByPrimaryKey(id);
    }
}
