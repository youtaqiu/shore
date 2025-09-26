package run.vexa.reactor.http.testclients.scanner;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import run.vexa.reactor.http.annotation.HttpClient;

@HttpClient
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public interface SingletonScanHttpClient {
}
