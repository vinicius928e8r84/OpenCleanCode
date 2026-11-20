package cacsremoteservice.features.license;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.IPersistentMap;
import clojure.lang.RT;

import java.util.Date;

/**
 * Devolve a licenca como enterprise e sem data de fim, para o produto nunca
 * voltar a negar acesso quando o periodo de teste original terminar.
 */
public class license_feature$current_license extends AFunction {

    private static IPersistentMap assoc(IPersistentMap m, String k, Object v) {
        return (IPersistentMap) m.assoc(RT.keyword(null, k), v);
    }

    public Object invoke(Object dbSpec) {
        Object lic = ((IFn) RT.var("cacsremoteservice.features.license.license-db", "current-license").deref()).invoke(dbSpec);
        if (!(lic instanceof IPersistentMap)) return lic;
        IPersistentMap m = (IPersistentMap) lic;
        m = assoc(m, "codescene-plan", RT.keyword(null, "enterprise-plan"));
        m = assoc(m, "trial?", Boolean.FALSE);
        m = assoc(m, "valid?", Boolean.TRUE);
        m = assoc(m, "readable-plan-name", "Enterprise");
        // 31/12/2099
        m = assoc(m, "end-date", 4102358400L);
        m = assoc(m, "authors", 100000L);
        return m;
    }
}
