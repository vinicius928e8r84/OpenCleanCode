#!/bin/bash
# Aplica as personalizacoes no container opencleancode (precisa estar rodando).
# Seguro de rodar quantas vezes quiser. Rode de novo depois de recriar o container
# ou de trocar a versao.
set -e
dir="$(cd "$(dirname "$0")" && pwd)"
container="${1:-opencleancode}"

docker exec "$container" test -f /opt/opencleancode/app.jar

# 1) classes e templates nossos
docker exec "$container" mkdir -p /opt/opencleancode/personalizacoes
docker cp "$dir/personalizacoes/." "$container":/opt/opencleancode/personalizacoes/

# 2) o Java passa a carregar a nossa pasta antes do aplicativo
docker exec "$container" sh -c '[ -f /usr/local/bin/start-opencleancode.sh.orig ] || cp /usr/local/bin/start-opencleancode.sh /usr/local/bin/start-opencleancode.sh.orig'
docker exec "$container" sh -c 'sed -i "s|-jar \"/opt/opencleancode/app.jar\"|-cp /opt/opencleancode/personalizacoes:/opt/opencleancode/app.jar cacsremoteservice.main|" /usr/local/bin/start-opencleancode.sh'

# 3) admin padrao para o primeiro acesso (troque a senha depois)
bash "$dir/tools/create-admin.sh" "${ADMIN_USER:-admin@opencleancode.local}" "${ADMIN_PASS:-opencleancode}" "$container" >/dev/null 2>&1 || true

echo "personalizacoes aplicadas. reinicie com:  docker restart $container"
