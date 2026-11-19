#!/bin/bash
# Instala o patch no container "codescene" (precisa estar rodando).
# Idempotente. Rode de novo depois de recriar o container ou de atualizar a imagem.
set -e
dir="$(cd "$(dirname "$0")" && pwd)"

docker exec codescene test -f /opt/codescene/codescene-enterprise-edition.standalone.jar

# 1) classes e templates que substituem os originais
docker exec codescene mkdir -p /opt/codescene/patch
docker cp "$dir/patch/." codescene:/opt/codescene/patch/

# 2) a JVM carrega nosso diretorio antes do jar (classes e recursos)
docker exec codescene sh -c '[ -f /usr/local/bin/start-codescene.sh.orig ] || cp /usr/local/bin/start-codescene.sh /usr/local/bin/start-codescene.sh.orig'
docker exec codescene sh -c 'sed -i "s|-jar \"/opt/codescene/codescene-enterprise-edition.standalone.jar\"|-cp /opt/codescene/patch:/opt/codescene/codescene-enterprise-edition.standalone.jar cacsremoteservice.main|" /usr/local/bin/start-codescene.sh'

echo "patch aplicado. reinicie com:  docker restart codescene"
