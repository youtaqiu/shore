package io.irain.reactor.r2dbc.query;


import io.irain.reactor.r2dbc.entity.BaseDomain;

/**
 * @author youta
 **/
class UserDemo extends BaseDomain<Long> {
    private Long id;
    private String name;

    public UserDemo(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public UserDemo() {
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

}
