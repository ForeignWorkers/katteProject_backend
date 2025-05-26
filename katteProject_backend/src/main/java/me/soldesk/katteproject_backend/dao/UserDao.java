package me.soldesk.katteproject_backend.dao;

import common.bean.UserBean;
import me.soldesk.katteproject_backend.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao {

    @Autowired
    private UserMapper userMapper;

    public void addUserInfo(UserBean joinUserBean) {
        userMapper.addUserInfo(joinUserBean);
    }
}
