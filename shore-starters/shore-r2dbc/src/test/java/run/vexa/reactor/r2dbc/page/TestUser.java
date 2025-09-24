package run.vexa.reactor.r2dbc.page;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * 测试用的用户实体
 *
 * @author youta
 */
@Data
@Table("t_user")
public class TestUser {
    @Id
    private Long id;
    
    @Column("user_id")
    private Long userId;
    
    @Column("user_name")
    private String userName;
}