# Levantamento das regras (CI/CD, qualidade e MCP)

Objetivo: registrar, com a fonte de cada item, as regras que o produto usa, para que
uma versão nossa possa dar o mesmo veredito sem depender do programa deles.

Regra deste documento: todo item traz a fonte exata. Item sem fonte marcada é
"observado", não lido.

## Como ler o pacote

O programa vem num único arquivo (`/opt/opencleancode/app.jar`, 244 MB). Dentro dele:

- `codescene/**/*.clj` — **código-fonte Clojure de verdade** (as regras de análise e de
  gate moram aqui);
- `cacsremoteservice/**/*.class` — só compilado (a camada web, sem fonte);
- `remote-mcp/**` — biblioteca de skills do MCP, com texto e esquemas em JSON;
- `public/**` — telas, imagens, documentação e o bundle JS;
- `liquibase/migrations/**` — mudanças de banco, incluindo presets.

Leitura usada: `java Ls2.jar <jar> <filtro>` para listar entradas e
`java Ex.java <jar> <filtro> <destino>` para extrair. Alguns itens só existem em
execução e foram lidos no REPL do próprio servidor.

## 1. MCP

- Catálogo versionado das skills: `remote-mcp/library/catalog.json` (versão 0.5.0).
- Manifesto com hash por arquivo: `remote-mcp/library/MANIFEST.json`.
- Regras gerais de toda skill: `remote-mcp/library/shared/ground-rules.md`
  (usar só dado do MCP, nunca estimar, nunca imprimir credencial).
- Uma pasta por skill com `SKILL.md`, `skill.json` e `report-data.schema.json`
  (ex.: `remote-mcp/library/skills/pull-request-safeguards/`).
- Pin da biblioteca: `remote-mcp/library-pin.edn`.
- Implementação do gate dentro do MCP: `codescene/features/remote_mcp/gate.clj`.
- Faixa de neutralidade de Code Health por PR: `stable_band`, padrão `0.05`
  (`.../skills/pull-request-safeguards/skill.json`).

Ferramentas MCP declaradas nas skills (campo `mcp_tools`):

`assess_project_code_health`, `estimate_engineering_outcomes`,
`explore_project_components`, `explore_project_files`, `get_commit_activity`,
`get_mcp_safeguard_insights`, `get_pull_request_safeguard_outcomes`,
`list_code_health_alerts`, `list_prioritized_refactorings`, `list_projects`.

## 2. Gates de qualidade e presets

- Nomes dos gates e lógica de falha: `codescene/features/delta/code_health/ch_gates.clj`
  (funções `description`, `gather-gate`, `qg-summary`, `failed-gates`).
- Presets e o que cada um liga: função `profile` e mapa `gate-profile-names` do
  namespace `codescene.delta.detectors.quality-gates` (lido no REPL). Valores:
  - `minimal`: hotspot-decline, new-code-health, critical-health-rules;
  - `medium`: os três acima + advisory-health-rules + tagear code owners;
  - `all`: todos, incluindo refactoring-goals e supervise-goals;
  - `custom` / `custom-file`: nada ligado por padrão.
- Chaves de configuração e defaults (todas `false`): `codescene/features/delta/code_health/ch_config.clj`
  (`qg-properties`), e a lista fechada de presets `::qg-preset`
  (`global`, `minimal`, `medium`, `all`, `custom`, `custom-file`).
- Preset usado por projeto: tabela `settings_kv`, chave `qg-preset`
  (ex.: projeto 1 com `minimal`); a categoria é `pr-quality-gates`.
- Criação do preset `medium` como padrão de instalação:
  `liquibase/migrations/cs_2373_reset_qg_preset.xml`.
- Tela que consome isso: `cacsremoteservice/views/settings/pr-quality-gates.html`.
- Nomes visíveis dos gates e chaves `qg-*` no bundle:
  `public/bundle.4b0311757d300cae36f6.js` (ex.: `Enforce Critical Code Health Rules`,
  `qg-new-code-health?`).

Pendente de leitura literal: o **valor numérico** do limiar de Code Health de código
novo é constante no trecho compilado (aparece formatado em
`ch_gates.clj`, função `description`, como "code health below %.2f"). Para fechar,
falta ler a constante no bytecode ou medir pela tela.

## 3. Cobertura de código

- `codescene/features/delta/coverage/cov_gates.clj` e
  `codescene/features/delta/coverage/cov-config.clj`.
- Gate na UI do projeto: `cacsremoteservice/views/projects/configuration/coverage-gates.html`.
- Guia: `public/docs/_sources/guides/code-coverage-gates/check-code-coverage-in-pull-and-merge-requests.rst.txt`.

## 4. Resultado de análise e relatórios

- Resultado de delta e "descope" de arquivos: `codescene/delta/delta-result.clj`
  (referenciado em `ch_gates.clj`).
- Gate de PR por regras de saúde: `codescene/features/delta/code_health/pr_check.clj`
  (`qg-preset-desc`, opções de provedor).
- Opções por provedor (comentário em PR, anotação, build, discussão):
  `codescene/features/delta/code_health/ch_config.clj`, mapa `provider-properties`.

## 5. O que ainda não tem fonte legível

- O motor de análise (interpretar a linguagem e calcular as métricas): compilado.
  As métricas em si são documentadas em público pela CodeScene; a paridade se faz
  pelas fórmulas publicadas, não pelo código.
- Limiares numéricos das regras críticas/advisórias: compilados.
- A camada web (`cacsremoteservice/**`): só compilado, incluindo rotas e permissões.

## 6. Como usar isto numa versão nossa

1. Reproduzir o MCP a partir de `catalog.json` + `skill.json` + `schema.json`
   (nomes, entradas, formato de saída e as ground rules).
2. Reproduzir os gates com as mesmas chaves, os mesmos presets e o mesmo veredito
   de `failed-gates` (pass/disabled/fail por gate).
3. Reproduzir a cobertura com as mesmas metas e o mesmo critério de PR.
4. Só então encarar o motor de análise, com as métricas públicas como especificação
   e testes de paridade contra resultados reais do servidor original.
