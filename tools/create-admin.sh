#!/bin/bash
# Cria (ou conserta a senha d)o usuario admin do OpenCleanCode.
# Uso: bash tools/create-admin.sh [usuario] [senha] [container]
# Padrao: admin@opencleancode.local / opencleancode / opencleancode
set -e
user="${1:-admin@opencleancode.local}"
pass="${2:-opencleancode}"
container="${3:-opencleancode}"

repl() {
  docker exec "$container" bash -c '
    exec 3<>/dev/tcp/localhost/5555
    printf "%s\n" "(require (quote cacsremoteservice.database.db) (quote cacsremoteservice.features.user.user-feature-impl) (quote cacsremoteservice.features.license.third-party-license))" >&3
    sleep 1
    printf "%s\n" "(def ds (cacsremoteservice.database.db/get-db-spec))" >&3
    sleep 1
    printf "%s\n" "$1" >&3
    sleep 3
    timeout 8 cat <&3
  ' _ "$1"
}

repl "(try (cacsremoteservice.features.license.third-party-license/create-default-admin-account ds {:username \"$user\" :password \"$pass\"}) (catch Throwable t nil))" | tail -1

# garante a senha, caso o usuario ja existisse com outra
repl "(let [u (cacsremoteservice.features.user.user-feature-impl/find-by-user-name ds \"$user\")] (when u (cacsremoteservice.features.user.user-feature-impl/update-password-for ds {:id (:id u) :password \"$pass\"})))" | tail -1

echo "admin pronto: $user"
