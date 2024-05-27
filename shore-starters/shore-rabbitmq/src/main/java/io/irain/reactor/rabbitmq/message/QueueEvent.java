package io.irain.reactor.rabbitmq.message;

import cn.hutool.core.util.IdUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @author youta
 **/
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Data
@EqualsAndHashCode
@SuppressWarnings("unused")
public abstract class QueueEvent implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

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
            return new ObjectMapper().writeValueAsBytes(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
