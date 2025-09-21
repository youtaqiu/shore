package run.vexa.reactor.http.core;

import java.util.Map;

/**
 * DefaultUriVariablesSupplier.
 *
 * @author rained
 **/
public interface DefaultUriVariablesSupplier {

    /**
     * supply defaultUriVariables Map
     *
     * @return Map
     */
    Map<String, ?> supply();

}
