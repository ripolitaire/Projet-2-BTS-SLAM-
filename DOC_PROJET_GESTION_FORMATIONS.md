# Documentation complète — JavaFxProjectGestionFormations

Date de génération : 08/04/2026 (Europe/Paris)

## 1) Présentation

**JavaFxProjectGestionFormations** est une application **desktop JavaFX** (FXML + CSS) qui sert d’interface de gestion pour un centre de formations :

- consultation des **formations**
- consultation des **sessions**
- gestion/consultation des **apprenants**
- suivi des **présences**
- collecte/consultation des **avis**
- authentification (récupération d’un token) et affichage d’un **tableau de bord** (stats)

L’application consomme une **API HTTP** (par défaut sur `http://localhost:3000`) via `java.net.http.HttpClient`.

## 2) Périmètre fonctionnel (écrans)

Les vues sont dans `src/app/view/*.fxml` et le thème dans `src/app/view/app-theme.css`.

### Authentification / Accueil

- `accueil.fxml` (contrôleur `app.controller.AuthViewController`) : page d’accueil, accès Login/Register
- `login.fxml` (contrôleur `app.controller.LoginController`) : connexion (appel API)
- `register.fxml` (contrôleur `app.controller.AuthViewController`) : écran inscription (navigation uniquement)

### Application (après connexion)

- `index.fxml` (contrôleur `app.controller.IndexController`) : tableau de bord + statistiques (sessions, présences, avis)
- `formations.fxml` (contrôleur `app.controller.NavigationController`) : navigation vers formation/sessions (pas de logique métier dédiée dans un contrôleur spécifique)
- `sessions.fxml` (contrôleur `app.controller.SessionsController`) : liste des sessions + navigation vers détail
- `sessions-detail.fxml` (contrôleur `app.controller.SessionDetailController`) : détail d’une session + présences + avis
- `apprenants.fxml` (contrôleur `app.controller.ApprenantController`) : liste apprenants + filtrage
- `ajout-apprenant.fxml` (contrôleur `app.controller.AjoutApprenantController`) : création apprenant (POST API)
- `formateurs.fxml` (contrôleur `app.controller.FormateurController`) : liste formateurs (**mock** côté client)
- `ajout-formateur.fxml` (contrôleur `app.controller.AjoutFormateurController`) : création formateur (**mock** côté client)
- `inscriptions.fxml` (contrôleur `app.controller.NavigationController`) : écran inscriptions (**mock** côté client via `InscriptionService`)
- `presences.fxml` (contrôleur `app.controller.NavigationController`) : écran présences (navigation, logique de présence principalement dans le détail session)
- `avis.fxml` (contrôleur `app.controller.NavigationController`) : écran avis (navigation, logique dans détail session)

## 3) Stack technique

### Langage / runtime

- Java (recommandé : **Java 17+**, cohérent avec la configuration JavaFX en 17.x)
- JavaFX (OpenJFX) : UI desktop (FXML + Controls + CSS)

### UI

- JavaFX `Application`, `Stage`, `Scene`
- FXML (`javafx.fxml.FXMLLoader`, annotations `@FXML`)
- CSS JavaFX (thème global appliqué via `app.Main.applyTheme`)
- Widgets utilisés (non exhaustif) : `TableView`, `ListView`, `PieChart`, `FilteredList`

### Accès réseau

- `java.net.http.HttpClient` / `HttpRequest` / `HttpResponse` (JDK)
- JSON manipulé en **String** avec parsing simple via **Regex** (pas de mapping JSON automatique dans le code actuel)

## 4) Dépendances & frameworks (toutes celles déclarées / utilisées)

Le projet n’utilise pas Maven/Gradle : la gestion des dépendances est faite par **VS Code Java** via `.vscode/settings.json` et `.vscode/launch.json`.

### Dépendances déclarées dans VS Code (`.vscode/settings.json`)

1) **JavaFX SDK 17.0.18** (JARs) — référencés via chemins absolus :

- `c:\\javafx-sdk-17.0.18\\lib\\javafx.base.jar`
- `c:\\javafx-sdk-17.0.18\\lib\\javafx.controls.jar`
- `c:\\javafx-sdk-17.0.18\\lib\\javafx.fxml.jar`
- `c:\\javafx-sdk-17.0.18\\lib\\javafx.graphics.jar`
- `c:\\javafx-sdk-17.0.18\\lib\\javafx.media.jar`
- `c:\\javafx-sdk-17.0.18\\lib\\javafx.swing.jar`
- `c:\\javafx-sdk-17.0.18\\lib\\javafx.web.jar`
- `c:\\javafx-sdk-17.0.18\\lib\\javafx-swt.jar`

2) **Jackson 2.15.2** — référencé via chemins absolus :

- `c:\\jackson\\jackson-annotations-2.15.2.jar`
- `c:\\jackson\\jackson-core-2.15.2.jar`
- `c:\\jackson\\jackson-databind-2.15.2.jar`

Remarque importante : à la date de génération, **aucune classe n’importe `com.fasterxml.jackson.*`** dans `src/`. Les JARs Jackson sont donc **déclarés** mais **non utilisés directement** dans le code.

### Dépendances déclarées dans le lancement (`.vscode/launch.json`)

- `vmArgs` (modules JavaFX + module-path) :
  - `--module-path "C:/javafx-sdk-17.0.18/lib;C:/jackson"`
  - `--add-modules javafx.controls,javafx.fxml,javafx.web,javafx.media,javafx.swing,javafx.swt`
- `classPaths` : `bin` + JARs Jackson

### Dépendances standard JDK utilisées dans le code

- HTTP client : `java.net.http.*`, `java.net.URI`
- Dates : `java.time.*`
- Collections & Streams : `java.util.*`, `java.util.stream.*`
- Regex : `java.util.regex.*`

## 5) Pré-requis & installation (poste de dev)

### Pré-requis logiciels

- **JDK 17+** installé (JAVA_HOME configuré si nécessaire)
- **JavaFX SDK 17.0.18** installé localement (ou équivalent, adapter les chemins)
- VS Code + extension pack **Java** :
  - recommandé dans `.vscode/extensions.json` : `vscjava.vscode-java-pack`

### Structure attendue des dossiers

- Sources : `src/`
- Sortie compilation : `bin/` (configuré dans `.vscode/settings.json`)
- Dépendances : `lib/` (prévu), mais **actuellement vide** dans le repo

### Portabilité (recommandation)

La config actuelle utilise des chemins absolus (`C:\\javafx-sdk-17.0.18\\...`, `C:\\jackson\\...`).
Pour rendre le projet portable :

- copier les JARs JavaFX/Jackson dans `lib/`
- remplacer les chemins absolus par `lib/**/*.jar` dans `.vscode/settings.json`
- ajuster `launch.json` (module-path) en pointant vers le dossier `lib/`

## 6) Configuration applicative

### Base URL API

Le code utilise `app.service.ApiConfig` :

- variable d’environnement optionnelle : `API_BASE_URL`
- valeur par défaut : `http://localhost:3000`

Exemples :

- Windows (PowerShell) :
  - `setx API_BASE_URL "http://localhost:3000"`
  - puis relancer VS Code / terminal

## 7) Lancement / exécution

### Depuis VS Code

- Ouvrir le dossier `JavaFxProjectGestionFormations` comme workspace
- Utiliser une configuration de debug :
  - `Main` ou `Launch App` dans `.vscode/launch.json`

### Point d’entrée

- Classe principale : `src/app/Main.java`
- Vue initiale : `src/app/view/accueil.fxml`
- Thème : `src/app/view/app-theme.css` (appliqué à chaque `Scene`)

### (Option) Compilation / lancement en ligne de commande (Windows)

Si vous ne lancez pas via VS Code, il faut :

1) compiler en pointant vers JavaFX (module-path)
2) copier les ressources FXML/CSS dans le dossier de sortie (ou exécuter avec `src/` dans le classpath)

Exemple (à adapter selon vos chemins) :

- compilation :
  - `javac -d bin --module-path "C:\\javafx-sdk-17.0.18\\lib" --add-modules javafx.controls,javafx.fxml $(Get-ChildItem -Recurse -Filter *.java src | % FullName)`
- exécution :
  - `java -cp "bin;src" --module-path "C:\\javafx-sdk-17.0.18\\lib" --add-modules javafx.controls,javafx.fxml app.Main`

## 8) Architecture du projet

### Arborescence (principale)

```
src/
  app/
    Main.java
    controller/   (contrôleurs JavaFX)
    model/        (modèles / entités)
    service/      (accès API + logique applicative)
    view/         (FXML + CSS)
bin/             (sortie compilation)
.vscode/         (config VS Code : classpath, launch, extensions)
```

### Packaging & responsabilités

#### `app`

- `Main` :
  - démarre JavaFX (`Application.start`)
  - charge la première vue (`accueil.fxml`)
  - applique la feuille CSS globale via `applyTheme(Scene)`

#### `app.controller` (UI / événements / navigation)

- `NavigationController` :
  - factorise la navigation entre écrans (chargement FXML, changement de `Scene`)
  - gère la déconnexion (`AuthSession.clear()` puis redirection login)
- `IndexController` (hérite de `NavigationController`) :
  - tableau de bord (compteurs + diagrammes donut)
  - agrège les données via `FormationService`, `SessionService`, `PresenceService`, `AvisService`
- `LoginController` :
  - appelle `AuthService.login(...)`
  - charge `index.fxml` si succès
- `SessionsController` :
  - récupère la liste des sessions via `SessionService`
  - gère l’affichage et l’accès au détail
- `SessionDetailController` :
  - récupère une session par id + présences + avis
  - permet d’ajouter un avis et de modifier des présences (selon implémentation UI)
- `ApprenantController` / `AjoutApprenantController` :
  - liste apprenants / ajout apprenant (API)
- `FormateurController` / `AjoutFormateurController` :
  - actuellement basé sur `FormateurService` **mock** (pas d’API)
- `AuthViewController` :
  - navigation entre `accueil`, `login`, `register`

#### `app.model` (données manipulées)

- `Apprenant` : `id_apprenant`, `pseudo`, `prenom`, `nom`, `email`, `telephone`, `mot_de_passe`, `role`
- `Formation` : `id_formation`, `nom`, `description`, `duree`, `niveau`
- `Session` : `id`, `formation`, `formateur`, `date`, `salle`, `present` (flag local UI)
- `Presence` : `id_presence`, `id_session`, `id_apprenant`, `present`
- `Avis` : `id_avis`, `id_session`, `id_apprenant`, `commentaire`, `note`
- `Formateur` : `id_formateur`, `prenom`, `nom`, `email`, `mot_de_passe`, `password`, `role`, `telephone`, `info_complementaires`

#### `app.service` (API + état de session)

- `ApiConfig` : construit les URLs (`API_BASE_URL`, fallback localhost:3000)
- `AuthService` :
  - POST login (plusieurs endpoints candidats)
  - extrait `token`/`accessToken`/`jwt` via regex
- `AuthSession` :
  - stocke `token`, `role`, `email` (en mémoire)
  - produit `Authorization: Bearer <token>`
- Services API (avec `HttpClient`) :
  - `ApprenantService` : GET/POST `/api/apprenants`
  - `FormationService` : GET `/api/formations`, GET `/api/formations/{id}`
  - `SessionService` : GET `/api/sessions`, GET `/api/sessions/{id}`
  - `PresenceService` : GET `/api/presences`, PUT `/api/presences/{id_presence}`
  - `AvisService` : GET `/api/avis`, POST `/api/avis/me`
- Services mock (local) :
  - `FormateurService` : données en dur (liste + ajout simulé)
  - `InscriptionService` : map en mémoire `email -> Set<idSession>`

## 9) Contrats API attendus (résumé)

L’application attend une API REST (ou compatible) dont les réponses sont du JSON.

### Authentification

`AuthService.login(email, motDePasse)` tente successivement :

- endpoints (POST) : `/api/auth/login`, `/auth/login`, `/api/login`, `/login`
- payloads possibles :
  - `{ "email": "...", "password": "..." }`
  - `{ "email": "...", "mot_de_passe": "..." }`
  - `{ "email": "...", "password": "...", "mot_de_passe": "..." }`

Réponse attendue :

- statut : `200` ou `201`
- JSON contenant un champ token :
  - `"token"` ou `"accessToken"` ou `"jwt"`
- (optionnel) `"role"` et `"email"`

Après login : les appels API mettent `Authorization: Bearer <token>` si disponible.

### Ressources métiers

1) **Apprenants**

- `GET /api/apprenants` → liste d’objets contenant au minimum :
  - `id_apprenant`, `pseudo` (optionnel), `prenom`, `nom`, `email`, `telephone` (optionnel), `mot_de_passe` (optionnel), `role`
- `POST /api/apprenants` (Content-Type: application/json) :
  - envoie `prenom`, `nom`, `email`, `mot_de_passe`, `role`
  - attend un `201`

2) **Formations**

- `GET /api/formations` → objets : `id_formation`, `nom`, `description`, `duree`, `niveau`
- `GET /api/formations/{id}` → même structure

3) **Sessions**

- `GET /api/sessions` → objets (tolère `id` ou `id_session`) + champs :
  - `formation` (string), `formateur` (string), `date` (ISO `yyyy-MM-dd`), `salle`
- `GET /api/sessions/{id}` → même structure

4) **Présences**

- `GET /api/presences` → objets : `id_presence`, `id_session`, `id_apprenant`, `present` (bool)
- `PUT /api/presences/{id_presence}` :
  - envoie `id_session`, `id_apprenant`, `present`
  - attend `200`

5) **Avis**

- `GET /api/avis` → objets : `id_avis`, `id_session`, `id_apprenant`, `commentaire`, `note`
- `POST /api/avis/me` :
  - envoie `id_session`, `commentaire`, `note`
  - attend `201`

## 10) UI / Thème

- Thème global : `src/app/view/app-theme.css`
- Application du thème :
  - `app.Main.applyTheme(Scene)` est appelé à chaque chargement d’écran dans les contrôleurs de navigation

## 11) Remarques, limites et améliorations possibles (technique)

1) **Parsing JSON par regex**

- Les services parsèrent le JSON via regex (`Pattern/Matcher`).
- Limites : fragile si le JSON contient des objets imbriqués, des champs dans un ordre différent, des caractères spéciaux, ou si les strings contiennent `}`.
- Amélioration : utiliser Jackson (déjà référencé) avec `ObjectMapper` + DTOs.

2) **Dépendances non portables (chemins absolus)**

- `.vscode/settings.json` et `.vscode/launch.json` pointent vers `C:\\javafx-sdk-17.0.18\\...` et `C:\\jackson\\...`.
- Amélioration : mettre les JARs dans `lib/` + chemins relatifs.

3) **Services mock**

- `FormateurService` et `InscriptionService` sont en mémoire (données de démo).
- Si le backend prévoit ces ressources : créer un `FormateurService` et `InscriptionService` qui consomment l’API.

4) **Gestion des erreurs**

- Les erreurs API sont parfois ramenées uniquement via `statusCode`.
- Amélioration : afficher un message métier plus clair + logger les réponses d’erreur.

## 12) Annexes — mapping vues ↔ contrôleurs

- `accueil.fxml` → `app.controller.AuthViewController`
- `login.fxml` → `app.controller.LoginController`
- `register.fxml` → `app.controller.AuthViewController`
- `index.fxml` → `app.controller.IndexController`
- `formations.fxml` → `app.controller.NavigationController`
- `sessions.fxml` → `app.controller.SessionsController`
- `sessions-detail.fxml` → `app.controller.SessionDetailController`
- `apprenants.fxml` → `app.controller.ApprenantController`
- `ajout-apprenant.fxml` → `app.controller.AjoutApprenantController`
- `formateurs.fxml` → `app.controller.FormateurController`
- `ajout-formateur.fxml` → `app.controller.AjoutFormateurController`
- `inscriptions.fxml` → `app.controller.NavigationController`
- `presences.fxml` → `app.controller.NavigationController`
- `avis.fxml` → `app.controller.NavigationController`
