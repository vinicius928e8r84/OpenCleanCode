#!/bin/bash
# Instala o patch no container opencleancode (precisa estar rodando).
# Idempotente. Rode de novo depois de recriar o container ou de atualizar a imagem.
set -e
dir="$(cd "$(dirname "$0")" && pwd)"
container="${1:-opencleancode}"

docker exec "$container" test -f /opt/opencleancode/app.jar

# 1) classes e templates que substituem os originais
docker exec "$container" mkdir -p /opt/opencleancode/patch
docker cp "$dir/patch/." "$container":/opt/opencleancode/patch/

# 2) a JVM carrega nosso diretorio antes do jar (classes e recursos)
docker exec "$container" sh -c '[ -f /usr/local/bin/start-opencleancode.sh.orig ] || cp /usr/local/bin/start-opencleancode.sh /usr/local/bin/start-opencleancode.sh.orig'
docker exec "$container" sh -c 'sed -i "s|-jar \"/opt/opencleancode/app.jar\"|-cp /opt/opencleancode/patch:/opt/opencleancode/app.jar cacsremoteservice.main|" /usr/local/bin/start-opencleancode.sh'

echo "patch aplicado. reinicie com:  docker restart $container"
