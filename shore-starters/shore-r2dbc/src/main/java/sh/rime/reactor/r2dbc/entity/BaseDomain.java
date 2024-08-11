package sh.rime.reactor.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Column;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Base 实体
 *
 * @param <ID> 主键类型
 * @author youta
 */
@Data
@AllArgsConstructor
@SuperBuilder
public class BaseDomain<ID> implements Serializable {

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     */
    public BaseDomain() {
    }

    /**
     * 主键
     */
    @Id
    @Column("id")
    private ID id;

    /**
     * 创建人
     */
    @Column("create_by")
    @CreatedBy
    private String createBy;

    /**
     * 创建时间
     */
    @Column("create_time")
    @CreatedDate
    private LocalDateTime createTime;

    /**
     * 修改人
     */
    @Column("update_by")
    @LastModifiedBy
    private String updateBy;

    /**
     * 修改时间
     */
    @Column("update_time")
    @LastModifiedDate
    private LocalDateTime updateTime;

    /**
     * 删除
     */
    @Column("deleted")
    private boolean deleted;

}
