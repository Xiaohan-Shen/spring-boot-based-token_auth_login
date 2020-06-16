package token_based_auth.dao;

import org.springframework.stereotype.Repository;
import token_based_auth.bean.UserInfo;

public interface UserAccountsDao {

    //TODO: interface 不用写public
    public void insertUser(UserInfo user);

    //TODO: interface 不用写public
    public UserInfo queryByUser(String userId);
}
