package me.youm.reactor.r2dbc.page;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author youta
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class IPage<T> implements Serializable {


    /**
     * 当前页
     */
    private int current;
    /**
     * 页码
     */
    private int size;
    /**
     * 数据
     */
    private List<T> list;
    /**
     * 总数
     */
    private int total;
    /**
     * 页数
     */
    private int pages;

    public IPage(List<T> list) {
        this.list = list;
    }

}
