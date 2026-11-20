package cacsremoteservice.features.license;

import clojure.lang.AFunction;

/**
 * Substitui o original. O app chama (expired-license? licenca); este devolve sempre false.
 */
public class license_feature$expired_license_QMARK_ extends AFunction {
    public Object invoke(Object a) {
        return Boolean.FALSE;
    }

    public Object invoke(Object a, Object b) {
        return Boolean.FALSE;
    }

    public Object invoke(Object a, Object b, Object c) {
        return Boolean.FALSE;
    }
}
