package cacsremoteservice.features.license;

import clojure.lang.AFunction;

/**
 * Substitui o original. O app chama (license-limitation-exceeded? licenca contexto autores),
 * com 3 argumentos; este devolve sempre false.
 */
public class license_feature$license_limitation_exceeded_QMARK_ extends AFunction {
    public Object invoke(Object a) {
        return Boolean.FALSE;
    }

    public Object invoke(Object a, Object b) {
        return Boolean.FALSE;
    }

    public Object invoke(Object a, Object b, Object c) {
        return Boolean.FALSE;
    }

    public Object invoke(Object a, Object b, Object c, Object d) {
        return Boolean.FALSE;
    }
}
