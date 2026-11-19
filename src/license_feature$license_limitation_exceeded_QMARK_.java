package cacsremoteservice.features.license;

import clojure.lang.AFunction;

/**
 * Substitui o original. Teto de autores da licenca; devolve sempre false,
 * entao nenhum projeto fica bloqueado por numero de autores.
 */
public class license_feature$license_limitation_exceeded_QMARK_ extends AFunction {
    public Object invoke(Object a) {
        return Boolean.FALSE;
    }
}
