#!/bin/bash
# Cria um segundo login no OpenCleanCode, com permissao de criar projetos e grupos.
# Uso: bash tools/create-admin.sh [usuario] [senha] [container]
# Padrao: admin@opencleancode.local / opencleancode / opencleancode
set -e
user="${1:-admin@opencleancode.local}"
pass="${2:-opencleancode}"
container="${3:-opencleancode}"

repl() {
  docker exec "$container" bash -c '
    exec 3<>/dev/tcp/localhost/5555
    printf "%s\n" "(require (quote cacsremoteservice.database.db) (quote clojure.java.jdbc) (quote cacsremoteservice.features.user.user-feature-impl))" >&3
    sleep 1
    printf "%s\n" "(def ds (cacsremoteservice.database.db/get-db-spec))" >&3
    sleep 1
    printf "%s\n" "$1" >&3
    sleep 4
    timeout 8 cat <&3
  ' _ "$1"
}

# papel usado por este login: cria projetos e grupos, nao administra usuarios
repl "(do (try (clojure.java.jdbc/insert! ds :roles {:id 101 :name \"OpenCleanCode\"}) (catch Throwable t nil)) (clojure.java.jdbc/delete! ds :role_privileges [\"id=101\"]) (doseq [p (map :privilege (clojure.java.jdbc/query ds [\"select privilege from role_privileges where id=9\"])) :when (not (#{\"admin.full-access\" \"admin.activate-license\" \"api.admin\"} p))] (clojure.java.jdbc/insert! ds :role_privileges {:id 101 :privilege p})) :ok)" | tail -1

# cria o login (se ja existir, o erro e ignorado)
repl "(try (cacsremoteservice.features.user.user-feature-impl/add-new-user ds {:username \"$user\" :password \"$pass\" :role-id 101}) (catch Throwable t nil))" | tail -1

# garante a senha e o papel
repl "(let [u (cacsremoteservice.features.user.user-feature-impl/find-by-user-name ds \"$user\")] (when u (cacsremoteservice.features.user.user-feature-impl/update-password-for ds {:id (:id u) :password \"$pass\"}) (clojure.java.jdbc/delete! ds :auth_provider_role_mapping [\"provider_role_name=?\" \"$user\"]) (clojure.java.jdbc/insert! ds :auth_provider_role_mapping {:provider_id 1 :codescene_role_id 101 :provider_role_name \"$user\"})))" | tail -1

echo "login pronto: $user"
