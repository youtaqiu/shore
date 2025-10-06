package run.vexa.reactor.rabbitmq.autoconfigure;

import com.rabbitmq.client.Address;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.ReceiverOptions;
import reactor.rabbitmq.Sender;
import reactor.rabbitmq.SenderOptions;
import run.vexa.reactor.rabbitmq.producer.RabbitMQSender;
import run.vexa.reactor.rabbitmq.properties.RabbitMQProperties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RabbitMqAutoConfigurationTest {

    private final RabbitMqAutoConfiguration configuration = new RabbitMqAutoConfiguration();

    @Test
    void connectionMonoShouldCreateConnectionWithConfiguredFactory() {
        RabbitMQProperties properties = new RabbitMQProperties();
        properties.setHost("example.com");
        properties.setPort(1234);
        String username = "user";
        String password = "pwdValue";
        properties.setUsername(username);
        properties.setPassword(password);
        properties.setVirtualHost("/vh");

        Connection connection = mock(Connection.class);

        try (MockedConstruction<ConnectionFactory> mocked = mockConstruction(ConnectionFactory.class, (mock, context) -> {
            when(mock.newConnection("shore-rabbit")).thenReturn(connection);
        })) {
            Mono<Connection> mono = configuration.connectionMono(properties);
            Connection result = mono.block();

            assertThat(result).isSameAs(connection);

            ConnectionFactory factory = mocked.constructed().get(0);
            verify(factory).setHost("example.com");
            verify(factory).setPort(1234);
            verify(factory).setUsername(username);
            verify(factory).setPassword(password);
            verify(factory).setVirtualHost("/vh");
            verify(factory).useNio();
        }
    }

    @Test
    void senderOptionsShouldUseConnectionMonoWhenNoAddresses() {
        RabbitMQProperties properties = new RabbitMQProperties();
        Mono<Connection> connectionMono = Mono.just(mock(Connection.class));

        SenderOptions options = configuration.senderOptions(connectionMono, properties);

        assertThat(options.getConnectionMono()).isSameAs(connectionMono);
        assertThat(options.getConnectionSupplier()).isNull();
        assertThat(options.getResourceManagementScheduler()).isNotNull();
    }

    @Test
    void senderOptionsShouldConfigureConnectionSupplierWhenAddressesProvided() throws Exception {
        RabbitMQProperties properties = new RabbitMQProperties();
        String username = "user";
        String password = "pwdValue";
        properties.setUsername(username);
        properties.setPassword(password);
        properties.setVirtualHost("/vh");
        properties.setAddresses("host1:1111,host2:2222");

        Address[] addresses = properties.getParsedAddresses();
        Connection connection = mock(Connection.class);

        try (MockedConstruction<ConnectionFactory> mocked = mockConstruction(ConnectionFactory.class, (mock, context) -> {
            when(mock.newConnection(addresses, "shore-sender")).thenReturn(connection);
        })) {
            SenderOptions options = configuration.senderOptions(Mono.empty(), properties);

            assertThat(options.getConnectionMono()).isNull();
            assertThat(options.getConnectionSupplier()).isNotNull();

            ConnectionFactory factory = mocked.constructed().get(0);
            verify(factory).useNio();
            verify(factory).setUsername(username);
            verify(factory).setPassword(password);
            verify(factory).setVirtualHost("/vh");

            Connection created = options.getConnectionSupplier().apply(factory);
            assertThat(created).isSameAs(connection);
        }
    }

    @Test
    void receiverOptionsShouldUseConnectionMonoWhenNoAddresses() {
        RabbitMQProperties properties = new RabbitMQProperties();
        Mono<Connection> connectionMono = Mono.just(mock(Connection.class));

        ReceiverOptions options = configuration.receiverOptions(connectionMono, properties);

        assertThat(options.getConnectionMono()).isSameAs(connectionMono);
        assertThat(options.getConnectionSupplier()).isNull();
        assertThat(options.getConnectionSubscriptionScheduler()).isNotNull();
    }

    @Test
    void receiverOptionsShouldConfigureConnectionSupplierWhenAddressesProvided() throws Exception {
        RabbitMQProperties properties = new RabbitMQProperties();
        String username = "user";
        String password = "pwdValue";
        properties.setUsername(username);
        properties.setPassword(password);
        properties.setVirtualHost("/vh");
        properties.setAddresses("host1:1111,host2:2222");

        Address[] addresses = properties.getParsedAddresses();
        Connection connection = mock(Connection.class);

        try (MockedConstruction<ConnectionFactory> mocked = mockConstruction(ConnectionFactory.class, (mock, context) -> {
            when(mock.newConnection(addresses, "shore-receiver")).thenReturn(connection);
        })) {
            ReceiverOptions options = configuration.receiverOptions(Mono.empty(), properties);

            assertThat(options.getConnectionMono()).isNull();
            assertThat(options.getConnectionSupplier()).isNotNull();

            ConnectionFactory factory = mocked.constructed().get(0);
            verify(factory).useNio();
            verify(factory).setUsername(username);
            verify(factory).setPassword(password);
            verify(factory).setVirtualHost("/vh");

            Connection created = options.getConnectionSupplier().apply(factory);
            assertThat(created).isSameAs(connection);
        }
    }

    @Test
    void rabbitSenderBeanShouldWrapProvidedSender() {
        Sender sender = mock(Sender.class);

        RabbitMQSender rabbitSender = configuration.rabbitSender(sender);

        assertThat(rabbitSender).isNotNull();
    }
}
