package io.irain.reactor.commons.domain;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * Search is a class that represents the search criteria for a query.
 * It includes information such as the current page, page size, keyword, start date, end date, sorting property, and sorting order.
 * This class is also serializable, which means it can be converted to a byte stream and restored later.
 *
 * @author youta
 */
@Data
public class Search implements Serializable {

    /**
     * The current page number.
     */
    private Integer current = 1;

    /**
     * The size of the page.
     */
    private Integer size = 10;

    /**
     * The keyword for the search.
     */
    @Size(max = 30)
    private String keyword;

    /**
     * The start date for the search.
     */
    private String startDate;

    /**
     * The end date for the search.
     */
    private String endDate;

    /**
     * The property to sort the results by.
     */
    private String prop;

    /**
     * The order to sort the results in. Can be 'asc' or 'desc'.
     */
    private String order;

}
