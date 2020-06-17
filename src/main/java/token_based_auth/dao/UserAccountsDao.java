package token_based_auth.dao;

import org.springframework.stereotype.Repository;
import token_based_auth.bean.UserInfo;

public interface UserAccountsDao {

    // interface 不用写public
    void insertUser(UserInfo user);

    // interface 不用写public
    UserInfo queryByUser(String userId);
}
