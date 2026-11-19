# OpenCleanCode

CodeScene Local (a versão self-hosted, que roda em Docker) rodando **sem licença**:
sem prazo, sem teto de autores, sem pedir chave.

O patch é pequeno e transparente: troca três funções internas do programa e o
rodapé. Nada mais é alterado — projetos, banco, análises, API, MCP e integração
com GitHub continuam os originais.

## Como funciona

O app é um jar Java/Clojure em `/opt/codescene/codescene-enterprise-edition.standalone.jar`.
O `start-codescene.sh` da imagem sobe com `java -jar`. O patch:

1. compila três classes que substituem os testes de licença e as coloca em `/opt/codescene/patch`;
2. sobe o Java com `-cp /opt/codescene/patch:<jar>`, então o Java carrega as nossas
   classes (e o nosso template do rodapé) antes das originais;
3. o rodapé deixa de mostrar `Trial`, o teto `/1000` e a data de expiração.

| original | patch |
|---|---|
| `valid-license?` consulta o banco | sempre `true` |
| `expired-license?` compara com a data de fim | sempre `false` |
| `license-limitation-exceeded?` bloqueia pelo nº de autores | sempre `false` |
| rodapé: `Plan: Trial, license expiration: …` | `Plan: Enterprise` |

O executor de análise é o **único** ponto do programa que consulta a licença para
bloquear trabalho, e é um dos três que o patch desarma — por isso análise de PR
(CI) e o servidor MCP dos agentes seguem funcionando normalmente.

## Instalar

```bash
git clone <este-repo>
cd OpenCleanCode
bash install.sh
docker restart codescene
```

Requisitos: o container `codescene` já criado (`docker compose up -d` sobe um
novo, com o banco em um volume).

## Recompilar depois de atualizar o CodeScene

O patch depende dos nomes internos da versão. Se o app for atualizado, refaça:

```bash
docker cp codescene:/opt/codescene/codescene-enterprise-edition.standalone.jar /tmp/cs.jar
docker run --rm -v "$PWD":/w -w /w --entrypoint java codescene/codescene \
  -cp /opt/codescene/codescene-enterprise-edition.standalone.jar src/Build.java /w/src /w/patch
python3 tools/make-footer.py /tmp/cs.jar
bash install.sh && docker restart codescene
```

## Desfazer

```bash
docker exec codescene cp /usr/local/bin/start-codescene.sh.orig /usr/local/bin/start-codescene.sh
docker restart codescene
```

## Verificar

```bash
docker exec codescene grep -o '\-cp [^ ]*' /usr/local/bin/start-codescene.sh
docker logs codescene | grep -i licen
```

Testado na versão 7.5.20.

## Versão congelada (importante)

A instância roda de uma imagem **local e fixa**: `opencleancode:7.5.20`.
Nada se atualiza sozinho — nem `docker pull`, nem reinício, nem reboot mudam a
versão. Só muda quem rodar um `pull` e recriar o container de propósito.

Imagem original correspondente (sem patch), guardada como referência:
`codescene/codescene:7.5.20-original` (digest
`sha256:ba3a46165c242495778dc9abbe9f49e2ab3c6680f71c79fda9f6f8f2d47c88aa`).

### Atualizar quando você quiser

Atualizar pode quebrar o patch (os nomes internos mudam). O caminho seguro:

```bash
docker pull codescene/codescene:latest
docker tag codescene/codescene:latest codescene/codescene:<nova-versao>
# 1. suba um container descartável da versão nova, na porta 3004, com volume novo
# 2. rode:  bash install.sh   (se as classes antigas nao carregarem, recompile)
# 3. teste; se estiver bom:  docker commit <container> opencleancode:<nova-versao>
#    e troque a tag no docker-compose.yml
```

Se der errado, o container anterior continua parado e intacto (`codescene-antigo`);
basta `docker stop codescene && docker rm codescene && docker rename codescene-antigo codescene && docker start codescene`.
