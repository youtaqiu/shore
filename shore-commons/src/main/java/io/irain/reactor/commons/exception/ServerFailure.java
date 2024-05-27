package io.irain.reactor.commons.exception;

/**
 * ServerFailure is an interface that represents a server failure.
 * It includes methods for retrieving the error code and error message associated with the failure.
 *
 * @author youta
 */
public interface ServerFailure {

    /**
     * Retrieves the error code associated with the server failure.
     *
     * @return the error code
     */
    int code();

    /**
     * Retrieves the error message associated with the server failure.
     *
     * @return the error message
     */
    String message();

}
