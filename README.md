# OpenCleanCode

Servidor de análise de código que roda na sua máquina, dentro de Docker, com tudo
liberado: sem licença, sem prazo, sem teto de autores.

## O que ele faz

- Analisa repositórios e mostra a saúde do código, pontos de risco e o histórico
  de mudanças por arquivo.
- Acompanha pull requests pelo aplicativo do GitHub: cada PR recebe a análise como
  check, com os limites definidos no próprio projeto.
- Expõe um servidor MCP (`/mcp`) para agentes de IA consultarem o código com
  autorização própria (OAuth).
- Guarda tudo localmente: banco em volume Docker, sem sair da máquina.

## Instalar

```bash
git clone https://github.com/vinicius928e8r84/OpenCleanCode
cd OpenCleanCode
docker compose up -d
bash install.sh
docker restart opencleancode
```

Requisitos: Docker. A porta é a 3003.

## Conferir

```bash
docker exec opencleancode grep -o '\-cp [^ ]*' /usr/local/bin/start-opencleancode.sh
docker logs opencleancode | grep -i licen
```

## Voltar ao script de subida simples

```bash
docker exec opencleancode cp /usr/local/bin/start-opencleancode.sh.orig /usr/local/bin/start-opencleancode.sh
docker restart opencleancode
```

## Estrutura

```
personalizacoes/  nossas classes e o nosso rodapé
src/              código-fonte das classes, para recompilar
tools/            gerador do rodapé
install.sh
docker-compose.yml
```
