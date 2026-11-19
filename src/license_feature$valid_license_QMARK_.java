package cacsremoteservice.features.license;

import clojure.lang.AFunction;

/**
 * Substitui o original. O app chama (valid-license? db-spec) para decidir se ha
 * licenca valida; este devolve sempre true.
 */
public class license_feature$valid_license_QMARK_ extends AFunction {
    public Object invoke(Object a) {
        return Boolean.TRUE;
    }
}
