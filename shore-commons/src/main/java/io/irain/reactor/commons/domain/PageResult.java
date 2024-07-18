package io.irain.reactor.commons.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * PageResult is a class that represents the result of a pagination query.
 * It includes information such as the current page, page size, data list, total number of items, and total number of pages.
 * This class is also serializable, which means it can be converted to a byte stream and restored later.
 *
 * @param <T> the type of the data in the list
 * @author youta
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder
public class PageResult<T> implements Serializable {


    /**
     * The current page number.
     */
    private int current;

    /**
     * The size of the page.
     */
    private int size;

    /**
     * The list of data items on the current page.
     */
    private List<T> list;

    /**
     * The total number of data items across all pages.
     */
    private int total;

    /**
     * The total number of pages.
     */
    private int pages;

    /**
     * Constructor that initializes the list of data items.
     *
     * @param list the list of data items
     */
    public PageResult(List<T> list) {
        this.list = Collections.unmodifiableList(list);
    }

    /**
     * Constructor that initializes the total number of items, the list of data items, and the total number of pages.
     *
     * @param total the total number of data items
     * @param list  the list of data items
     * @param pages the total number of pages
     */
    public PageResult(int total, List<T> list, int pages) {
        this.total = total;
        this.list = list;
        this.pages = pages;
    }

    /**
     * Returns an empty PageResult.
     *
     * @return an empty PageResult
     */
    public PageResult<T> empty() {
        return new PageResult<>(1, 10, Collections.emptyList(), 0, 0);
    }
}
