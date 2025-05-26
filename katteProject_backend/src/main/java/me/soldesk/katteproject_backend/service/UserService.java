package me.soldesk.katteproject_backend.service;

import common.bean.UserBean;
import me.soldesk.katteproject_backend.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserDao userDao;

    public void addUserInfo(UserBean joinUserBean) {
        userDao.addUserInfo(joinUserBean);
    }
}
