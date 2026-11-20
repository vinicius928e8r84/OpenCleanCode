package cacsremoteservice.features.project;

import clojure.lang.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Lista so os projetos de quem esta logado (dono), para dois logins no mesmo
 * servidor nao verem os projetos um do outro. Antes o programa mostrava todos.
 */
public class access_management$all_accessible_projects extends AFunction {

    private static Object get(Object m, String k) {
        return m instanceof ILookup ? RT.get(m, RT.keyword(null, k)) : null;
    }

    private static Set<Long> ownedProjectIds(String username) {
        Set<Long> ids = new HashSet<Long>();
        if (username == null) return ids;
        try {
            IFn getDb = (IFn) RT.var("cacsremoteservice.database.db", "get-db-spec").deref();
            Object db = getDb.invoke();
            IFn query = (IFn) RT.var("clojure.java.jdbc", "query").deref();
            Object rows = query.invoke(db,
                    PersistentVector.create("select id from projects where project_owner = ?", username));
            for (Object r : (Iterable<?>) rows) {
                Object id = get(r, "id");
                if (id instanceof Number) ids.add(((Number) id).longValue());
            }
        } catch (Throwable t) {
            // sem banco acessivel: cai no comportamento antigo
            return null;
        }
        return ids;
    }

    private static String asText(Object o) {
        if (o == null) return null;
        try {
            if (o instanceof java.sql.Clob) {
                java.sql.Clob c = (java.sql.Clob) o;
                return c.getSubString(1, (int) c.length());
            }
        } catch (Throwable t) { /* ignora */ }
        return o.toString();
    }

    public Object invoke(Object user, Object allProjects) {
        String username = asText(get(user, "identity"));
        if (username == null) username = asText(get(user, "username"));
        if (username == null) username = asText(get(user, "name"));
        if (username == null) return allProjects;
        Set<Long> owned = ownedProjectIds(username);
        if (owned == null) return allProjects;
        List<Object> out = new ArrayList<Object>();
        for (Object p : (Iterable<?>) allProjects) {
            Object id = get(p, "id");
            if (id instanceof Number && owned.contains(((Number) id).longValue())) out.add(p);
        }
        return RT.seq(out);
    }
}
