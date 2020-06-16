package token_based_auth.dao;

import org.springframework.stereotype.Repository;
import token_based_auth.bean.UserInfo;

public interface UserAccountsDao {
    public void insertUser(UserInfo user);

    public UserInfo queryByUser(String userId);
}
