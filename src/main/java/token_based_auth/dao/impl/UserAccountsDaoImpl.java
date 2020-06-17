package token_based_auth.dao.impl;

import org.springframework.stereotype.Repository;
import token_based_auth.bean.UserInfo;
import token_based_auth.dao.UserAccountsDao;
import org.nutz.dao.Dao;
import org.springframework.beans.factory.annotation.Autowired;

@Repository
public class UserAccountsDaoImpl implements UserAccountsDao {
    // Autowired will invoke spring to scan the related packages for dependency injection
    @Autowired
    Dao dao;

    @Override
    public void insertUser(UserInfo user) {
        dao.insert(user);
    }

    @Override
    public UserInfo queryByUser(String userName) {
        return dao.fetch(UserInfo.class, userName);
    }
}
