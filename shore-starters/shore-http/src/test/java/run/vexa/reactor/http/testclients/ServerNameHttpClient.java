package run.vexa.reactor.http.testclients;

import run.vexa.reactor.http.annotation.HttpClient;

@HttpClient(serverName = "my-service")
public interface ServerNameHttpClient {
}
