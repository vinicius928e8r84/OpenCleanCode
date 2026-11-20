package cacsremoteservice.features.license;

import clojure.lang.AFunction;

/**
 * Substitui o original. O app chama (valid-license? db-spec); este devolve sempre true.
 */
public class license_feature$valid_license_QMARK_ extends AFunction {
    public Object invoke(Object a) {
        return Boolean.TRUE;
    }

    public Object invoke(Object a, Object b) {
        return Boolean.TRUE;
    }

    public Object invoke(Object a, Object b, Object c) {
        return Boolean.TRUE;
    }
}
