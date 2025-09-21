package run.vexa.reactor.r2dbc.query;


import run.vexa.reactor.r2dbc.entity.BaseDomain;
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

    UserDemo(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    UserDemo() {
    }

}
