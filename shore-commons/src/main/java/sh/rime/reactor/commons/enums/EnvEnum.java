package sh.rime.reactor.commons.enums;

import lombok.Getter;

import java.util.Arrays;

/**
 * EnvEnum is an enum that represents different types of environments, such as local, development, testing, etc.
 * Each enum value represents a specific environment type.
 * The class also includes a method for retrieving the enum value for a specific environment string.
 *
 * @author youta
 */
@Getter
public enum EnvEnum {

    /**
     * local
     */
    LOCAL("local"),
    /**
     * dev
     */
    DEV("dev"),
    /**
     * test
     */
    TEST("test"),
    /**
     * fat
     */
    FAT("fat"),
    /**
     * uat
     */
    UAT("uat"),
    /**
     * prod
     */
    PROD("prod");

    /**
     * the environment
     * -- GETTER --
     * get the environment
     */
    private final String env;

    /**
     * constructor
     *
     * @param env the environment
     */
    EnvEnum(String env) {
        this.env = env;
    }

    /**
     * get the environment
     *
     * @param envString the environment string
     * @return the environment
     */
    public static EnvEnum environment(String envString) {
        return Arrays.stream(EnvEnum.values())
                .filter(x -> x.env.equals(envString))
                .findFirst()
                .orElse(DEV);
    }
}
