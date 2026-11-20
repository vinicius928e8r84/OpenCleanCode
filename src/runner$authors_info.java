package cacsremoteservice.features.analysis_runner;

import clojure.lang.AFunction;
import clojure.lang.ILookup;
import clojure.lang.RT;

/**
 * Substitui o original, que estoura com NullPointerException quando o estado da
 * licenca vem sem limite de autores. Devolve sempre um mapa com numeros.
 */
public class runner$authors_info extends AFunction {

    private static Object get(Object m, String k) {
        return m instanceof ILookup ? RT.get(m, RT.keyword(null, k)) : null;
    }

    private static long asLong(Object o, long def) {
        return o instanceof Number ? ((Number) o).longValue() : def;
    }

    private static long count(Object o) {
        if (o == null) return 0;
        try { return RT.count(o); } catch (Throwable t) { return 0; }
    }

    public Object invoke(Object state) {
        long limit = asLong(get(state, "author-limit"), 1000);
        long used = count(get(state, "filtered-authors"));
        if (used == 0) used = count(get(state, "active-authors"));
        long exceeded = used > limit ? used - limit : 0;
        return RT.map(RT.keyword(null, "authors_count"), RT.box(used),
                      RT.keyword(null, "exceeded_by"), RT.box(exceeded));
    }
}
