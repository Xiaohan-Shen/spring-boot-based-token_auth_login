package token_based_auth.bean;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;

@Data
@NoArgsConstructor
@Table("user_accounts")
public class UserInfo {
    @Name
    @Column("userName")
    private String userName;
    @Column("password")
    private String password;

    public UserInfo(String userName, String password){
        this.userName = userName;
        this.password = password;
    }
}
