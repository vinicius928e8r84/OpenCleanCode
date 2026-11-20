#!/usr/bin/env python3
"""Gera patch/cacsremoteservice/views/four-factor-home-view.html a partir do
original dentro do jar: tira "Trial", o teto "/1000" e a data de expiracao do
rodape. Rode com o jar em maos (extraia antes com docker cp)."""
import os
import sys
import zipfile

jar = sys.argv[1] if len(sys.argv) > 1 else "/tmp/app.jar"
out = os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", "patch",
                   "cacsremoteservice", "views", "four-factor-home-view.html")

html = zipfile.ZipFile(jar).read(
    "cacsremoteservice/views/four-factor-home-view.html").decode("utf8")

html = html.replace(
    "Active authors: {{active-authors}}/{{license.authors}}",
    "Active authors: {{active-authors}}")
html = html.replace(
    "<span>Plan: {{license.readable-plan-name}}, license expiration: {{license.end-date}}</span>",
    "<span>Plan: Enterprise</span>")
html = html.replace("License valid until {{license.end-date}}", "License valid")

os.makedirs(os.path.dirname(out), exist_ok=True)
open(out, "w", encoding="utf8").write(html)
print("escrito", out)
