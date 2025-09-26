package run.vexa.reactor.http.testclients.scanner;

import org.springframework.context.annotation.Scope;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import run.vexa.reactor.http.annotation.HttpClient;

@HttpClient
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public interface PrototypeScanHttpClient {
}
