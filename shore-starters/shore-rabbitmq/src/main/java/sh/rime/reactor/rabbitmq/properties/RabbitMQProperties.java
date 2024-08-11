package sh.rime.reactor.rabbitmq.properties;

import cn.hutool.core.text.StrPool;
import com.rabbitmq.client.Address;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * RabbitMQ properties.
 *
 * @author youta
 **/
@ConfigurationProperties(prefix = RabbitMQProperties.PREFIX)
@Getter
@Setter
public class RabbitMQProperties {

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     */
    public RabbitMQProperties() {
    }

    /**
     * prefix
     */
    public static final String PREFIX = "shore.rabbitmq";

    /**
     * host
     */
    private String host = "localhost";

    /**
     * port
     */
    private Integer port = 5672;

    /**
     * username
     */
    private String username = "guest";

    /**
     * password
     */
    private String password = "guest";

    /**
     * virtualHost
     */
    private String virtualHost;

    /**
     * retry
     */
    private Long retry = 2L;
    private Long minBackoff = 1L;

    /**
     * addresses list
     */
    private String addresses;

    /**
     * parsed addresses
     */
    private Address[] parsedAddresses;

    /**
     * set addresses
     *
     * @param addresses addresses
     */
    public void setAddresses(String addresses) {
        this.addresses = addresses;
        this.parsedAddresses = parseAddresses(addresses);
    }

    /**
     * parse addresses
     *
     * @param addresses addresses
     * @return parsed addresses
     */
    private Address[] parseAddresses(String addresses) {
        List<Address> parsedAddressList = new ArrayList<>();
        for (String address : StringUtils.commaDelimitedListToStringArray(addresses)) {
            String[] parts = address.split(StrPool.COLON);
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid address: " + address);
            }
            parsedAddressList.add(new Address(parts[0], Integer.parseInt(parts[1])));
        }
        return parsedAddressList.toArray(new Address[0]);
    }

}
