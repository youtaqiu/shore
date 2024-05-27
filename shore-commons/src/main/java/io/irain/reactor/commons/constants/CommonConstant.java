package io.irain.reactor.commons.constants;

/**
 * RSAUtil is a utility class that provides methods for RSA encryption and decryption.
 * It includes methods for signing data, verifying signatures, and checking signatures.
 * The class uses the SecureUtil class to generate private and public keys, create signatures, and perform encryption and decryption.
 * The sign methods return a base64-encoded string representing the signature of the provided data.
 * The verify methods return a boolean indicating whether a provided signature is valid for the provided data.
 * The check methods return a boolean indicating whether a provided signature is valid for a set of parameters.
 * The getSignCheckContent and getSignContent methods return a string representing the content to be signed, based on a set of parameters.
 * The getSign method returns a base64-encoded string representing the signature of a set of parameters.
 *
 * @author youta
 */
public class CommonConstant {

    private CommonConstant() {
    }

    /**
     * 通用成功消息
     */
    public static final String SUCCESS_MSG = "success";
    /**
     * 通用成功码
     */
    public static final Integer SUCCESS_CODE = 200;
    /**
     * 通用失败消息
     */
    public static final Integer ERROR_CODE = 500;
    /**
     * 通用失败码
     */
    public static final String ERROR_MSG = "error";

}
