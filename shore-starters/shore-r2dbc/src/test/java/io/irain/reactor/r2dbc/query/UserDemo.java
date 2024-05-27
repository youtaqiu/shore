package io.irain.reactor.r2dbc.query;


import io.irain.reactor.r2dbc.entity.BaseDomain;
import lombok.Getter;
import lombok.Setter;

/**
 * @author youta
 **/
@Setter
@Getter
@SuppressWarnings("unused")
class UserDemo extends BaseDomain<Long> {
    private Long id;
    private String name;

    public UserDemo(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public UserDemo() {
    }

}
