package dao;

import bean.UserInfo;
import org.nutz.dao.Dao;
import org.springframework.beans.factory.annotation.Autowired;

public interface UserAccountsDao {
    public void insertUser(UserInfo user);

    public UserInfo queryByUser(String userId);
}
