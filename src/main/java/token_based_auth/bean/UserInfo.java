package token_based_auth.bean;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;

// 在建表的时候，大多数时候（99%）第一个需要的是一个long型的自增正整数：id
//  这样在查询时会比使用字符串的主键更加快速，而且也可以辅助多个表的连接

@Data
@NoArgsConstructor
@Table("user_accounts")
public class UserInfo {
    @Id
    private long id;
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
