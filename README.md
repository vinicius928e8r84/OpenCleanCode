# OpenCleanCode

Servidor de análise de código que roda na sua máquina, dentro de Docker, com tudo
liberado: sem licença, sem prazo, sem teto de autores e sem pedir chave.

O código roda em cima da versão 7.5.20, congelada — nada se atualiza sozinho.

## O que ele faz

- Analisa repositórios e mostra a saúde do código, pontos de risco e o histórico
  de mudanças por arquivo.
- Acompanha pull requests pelo aplicativo do GitHub: cada PR recebe a análise como
  check, com os limites definidos no próprio projeto.
- Expõe um servidor MCP (`/mcp`) para os agentes de IA consultarem o código com
  autorização própria (OAuth).
- Guarda tudo localmente: banco em volume Docker, sem sair da máquina.

## Como está montado

O app é um jar Java/Clojure em `/opt/opencleancode/app.jar`. O script de subida
roda com `java -jar`. O patch:

1. coloca em `/opt/opencleancode/patch` três classes que substituem os testes de
   licença e o rodapé;
2. sobe o Java com `-cp /opt/opencleancode/patch:/opt/opencleancode/app.jar`, então
   as nossas classes (e o nosso template de rodapé) carregam antes das originais;
3. o rodapé mostra `Plan: Enterprise`, sem teto e sem data de expiração.

| original | patch |
|---|---|
| `valid-license?` consulta o banco | sempre `true` |
| `expired-license?` compara com a data de fim | sempre `false` |
| `license-limitation-exceeded?` bloqueia pelo nº de autores | sempre `false` |

O executor de análise é o único ponto que consulta licença para bloquear trabalho,
e é um dos três que o patch desarma — por isso análise de PR (CI) e o servidor MCP
seguem funcionando normalmente.

## Instalar

```bash
git clone https://github.com/vinicius928e8r84/OpenCleanCode
cd OpenCleanCode
docker compose up -d
bash install.sh
docker restart opencleancode
```

Requisitos: Docker. A porta é a 3003.

## Verificar

```bash
docker exec opencleancode grep -o '\-cp [^ ]*' /usr/local/bin/start-opencleancode.sh
docker logs opencleancode | grep -i licen
```

## Desfazer

```bash
docker exec opencleancode cp /usr/local/bin/start-opencleancode.sh.orig /usr/local/bin/start-opencleancode.sh
docker restart opencleancode
```

## Atualizar (só quando você quiser)

A instância roda de uma imagem local e fixa: `opencleancode:7.5.20`. `docker pull`,
reinício e reboot não mudam a versão; só muda quem recriar o container de propósito.

Atualizar pode quebrar o patch, porque os nomes internos mudam. O caminho seguro é
subir a versão nova num container descartável, em outra porta e com volume novo,
testar, e só então congelar:

```bash
docker cp <container-novo>:/opt/opencleancode/app.jar /tmp/app.jar
docker run --rm -v "$PWD":/w -w /w --entrypoint java <imagem-nova> \
  -cp /opt/opencleancode/app.jar src/Build.java /w/src /w/patch
python3 tools/make-footer.py /tmp/app.jar
bash install.sh && docker restart <container-novo>
docker commit <container-novo> opencleancode:<nova-versao>
```

Se der errado, o container anterior continua parado e intacto: basta voltar para ele.

## Estrutura

```
patch/    classes e template que substituem os originais
src/      código-fonte das classes do patch (recompiláveis)
tools/    gerador do rodapé
install.sh
docker-compose.yml
```
