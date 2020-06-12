package dao.impl;

import bean.UserInfo;
import dao.UserAccountsDao;
import org.nutz.dao.Dao;
import org.springframework.beans.factory.annotation.Autowired;

public class UserAccountsDaoImpl implements UserAccountsDao {
    @Autowired
    private Dao dao;

    @Override
    public void insertUser(UserInfo user) {
        dao.insert(user);
    }

    @Override
    public UserInfo queryByUser(String userName) {
        return dao.fetch(UserInfo.class, userName);
    }
}
