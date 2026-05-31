package run.vexa.reactor.rabbitmq.message;

import cn.hutool.core.util.IdUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 队列事件
 *
 * @author youta
 **/
@AllArgsConstructor
@SuperBuilder
@Data
@EqualsAndHashCode
@SuppressWarnings("unused")
public abstract class QueueEvent implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     */
    protected QueueEvent() {
    }

    /**
     * 消息id
     */
    @Builder.Default
    private String eventId = IdUtil.randomUUID();

    /**
     * 日期
     */
    @Builder.Default
    private Date eventTime = new Date();

    /**
     * 交换机
     */
    @Builder.Default
    private String exchange = "";

    /**
     * 路由键
     */
    private String routingKey;

    /**
     * 队列
     */
    private String queue;

    /**
     * getQueue
     *
     * @return queue
     */
    public abstract String getQueue();

    /**
     * 消息体
     *
     * @return 消息体
     */
    @JsonIgnore
    public byte[] getPayload() {
        try {
            return JsonMapper.builder().build().writeValueAsBytes(this);
        } catch (JacksonException e) {
            throw new IllegalStateException(e);
        }
    }

}
