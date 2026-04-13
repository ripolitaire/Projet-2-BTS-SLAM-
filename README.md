## Projet

Application JavaFX de gestion de formations.

- Sources : `src/`
- Sortie de compilation (générée) : `bin/` (ignorée par Git)
- Documentation : `DOC_PROJET_GESTION_FORMATIONS.md`

## VS Code (Java)

Le projet est configuré pour l’extension Java de VS Code.

Note : `.vscode/settings.json` et `.vscode/launch.json` contiennent des chemins **absolus** (ex: `C:\javafx-sdk-17.0.18\...`, `C:\jackson\...`) adaptés à ta machine.

## Mettre sur GitHub

1) Initialiser le dépôt Git (dans ce dossier) :

```powershell
git init
git add .
git commit -m "Initial commit"
```

2) Créer un dépôt sur GitHub, puis pousser :

```powershell
git branch -M main
git remote add origin <URL_DU_DEPOT_GITHUB>
git push -u origin main
```

Les fichiers générés/IDE sont filtrés via `.gitignore` (ex: `bin/`, `.sixth/`, configs VS Code machine-spécifiques).
