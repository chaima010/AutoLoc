# Note d'Architecture et Documentation — Atelier 5
**Spring MVC, SpringDoc OpenAPI & Tests Postman**  
*Projet AutoLoc (`autoloc-api` / `com.example.location`)*

---

## A. Tableau des Endpoints REST

L'ensemble des endpoints de l'API REST est préfixé globalement par `/api` via la propriété `server.servlet.context-path=/api`. Les contrôleurs définissent uniquement leur racine de ressource (au pluriel).

| Contrôleur | Verbe HTTP & Route | Méthode du Service | Code Succès | Codes Erreur | Description |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **ContratController** | `GET /api/contrats` | `contratService.findAll()` | `200 OK` | — | Lister tous les contrats |
| | `GET /api/contrats/{id}` | `contratService.findById(id)` | `200 OK` | `404 Not Found` | Consulter un contrat par identifiant |
| | `POST /api/contrats` | `contratService.create(contrat)` | `201 Created` | `400 Bad Request` | Créer un nouveau contrat (éventuellement avec paiement en cascade) |
| | `PUT /api/contrats/{id}` | `contratService.update(id, contrat)` | `200 OK` | `400 Bad Request`, `404 Not Found` | Modifier un contrat existant |
| | `DELETE /api/contrats/{id}` | `contratService.deleteById(id)` | `204 No Content` | `404 Not Found` | Supprimer un contrat (cascade vers le paiement associé) |
| **PaiementController** *(Lecture seule)* | `GET /api/paiements` | `paiementService.findAll()` | `200 OK` | — | Lister tous les paiements |
| | `GET /api/paiements/{id}` | `paiementService.findById(id)` | `200 OK` | `404 Not Found` | Consulter un paiement par identifiant |
| | `POST /api/paiements` | *(Non implémenté)* | — | `405 Method Not Allowed` | Tentative de création directe d'un paiement refusée |
| **AgenceController** | `GET /api/agences` | `agenceService.findAll()` | `200 OK` | — | Lister toutes les agences |
| | `GET /api/agences/{id}` | `agenceService.findById(id)` | `200 OK` | `404 Not Found` | Consulter une agence par son identifiant |
| | `POST /api/agences` | `agenceService.create(agence)` | `201 Created` | `400 Bad Request` | Créer une agence (nom, ville obligatoires) |
| | `PUT /api/agences/{id}` | `agenceService.update(id, agence)` | `200 OK` | `400 Bad Request`, `404 Not Found` | Mettre à jour une agence |
| | `DELETE /api/agences/{id}` | `agenceService.deleteById(id)` | `204 No Content` | `404 Not Found` | Supprimer une agence |
| **EmployeController** | `GET /api/employes` | `employeService.findAll()` | `200 OK` | — | Lister tous les employés |
| | `GET /api/employes/{id}` | `employeService.findById(id)` | `200 OK` | `404 Not Found` | Consulter un employé |
| | `POST /api/employes` | `employeService.create(employe)` | `201 Created` | `400 Bad Request` | Créer un employé (rattaché à une agence) |
| | `PUT /api/employes/{id}` | `employeService.update(id, employe)` | `200 OK` | `400 Bad Request`, `404 Not Found` | Modifier un employé |
| | `DELETE /api/employes/{id}` | `employeService.deleteById(id)` | `204 No Content` | `404 Not Found` | Supprimer un employé |
| **VehiculeController** | `GET /api/vehicules` | `vehiculeService.findAll()` | `200 OK` | — | Lister tous les véhicules |
| | `GET /api/vehicules/{id}` | `vehiculeService.findById(id)` | `200 OK` | `404 Not Found` | Consulter un véhicule |
| | `POST /api/vehicules` | `vehiculeService.create(vehicule)` | `201 Created` | `400 Bad Request` | Créer un véhicule (tarif positif requis) |
| | `PUT /api/vehicules/{id}` | `vehiculeService.update(id, vehicule)` | `200 OK` | `400 Bad Request`, `404 Not Found` | Modifier un véhicule |
| | `DELETE /api/vehicules/{id}` | `vehiculeService.deleteById(id)` | `204 No Content` | `404 Not Found` | Supprimer un véhicule |
| **EquipementController** | `GET /api/equipements` | `equipementService.findAll()` | `200 OK` | — | Lister tous les équipements |
| | `GET /api/equipements/{id}` | `equipementService.findById(id)` | `200 OK` | `404 Not Found` | Consulter un équipement |
| | `POST /api/equipements` | `equipementService.create(equipement)` | `201 Created` | `400 Bad Request` | Créer un équipement (libellé non vide) |
| | `PUT /api/equipements/{id}` | `equipementService.update(id, equipement)` | `200 OK` | `400 Bad Request`, `404 Not Found` | Modifier un équipement |
| | `DELETE /api/equipements/{id}` | `equipementService.deleteById(id)` | `204 No Content` | `404 Not Found` | Supprimer un équipement |
| **ClientController** | `GET /api/clients` | `clientService.findAll()` | `200 OK` | — | Lister tous les clients |
| | `GET /api/clients/{id}` | `clientService.findById(id)` | `200 OK` | `404 Not Found` | Consulter un client |
| | `POST /api/clients` | `clientService.create(client)` | `201 Created` | `400 Bad Request` | Créer un client |
| | `PUT /api/clients/{id}` | `clientService.update(id, client)` | `200 OK` | `400 Bad Request`, `404 Not Found` | Modifier un client |
| | `DELETE /api/clients/{id}` | `clientService.deleteById(id)` | `204 No Content` | `404 Not Found` | Supprimer un client |
| **ReservationController** | `GET /api/reservations` | `reservationService.findAll()` | `200 OK` | — | Lister toutes les réservations |
| | `GET /api/reservations/{id}` | `reservationService.findById(id)` | `200 OK` | `404 Not Found` | Consulter une réservation |
| | `POST /api/reservations` | `reservationService.create(reservation)` | `201 Created` | `400 Bad Request` | Créer une réservation (date fin >= date début) |
| | `PUT /api/reservations/{id}` | `reservationService.update(id, reservation)` | `200 OK` | `400 Bad Request`, `404 Not Found` | Modifier une réservation |
| | `DELETE /api/reservations/{id}` | `reservationService.deleteById(id)` | `204 No Content` | `404 Not Found` | Supprimer une réservation |
| **MaintenanceController** | `GET /api/maintenances` | `maintenanceService.findAll()` | `200 OK` | — | Lister toutes les opérations de maintenance |
| | `GET /api/maintenances/{id}` | `maintenanceService.findById(id)` | `200 OK` | `404 Not Found` | Consulter une maintenance |
| | `POST /api/maintenances` | `maintenanceService.create(maintenance)` | `201 Created` | `400 Bad Request` | Créer une maintenance |
| | `PUT /api/maintenances/{id}` | `maintenanceService.update(id, maintenance)` | `200 OK` | `400 Bad Request`, `404 Not Found` | Modifier une maintenance |
| | `DELETE /api/maintenances/{id}` | `maintenanceService.deleteById(id)` | `204 No Content` | `404 Not Found` | Supprimer une maintenance |

---

## B. Annotations Jackson et Sérialisation JSON

### 1. Associations et choix des annotations

| Entité source | Attribut / Cible | Type relation | Annotation retenue | Justification |
| :--- | :--- | :--- | :--- | :--- |
| `Contrat` | `paiement` (`Paiement`) | `@OneToOne` (composition) | `@JsonManagedReference("contrat-paiement")` | Le contrat est le parent logique et propriétaire de la composition du paiement ; il doit sérialiser son paiement associé. |
| `Paiement` | `contrat` (`Contrat`) | `@OneToOne` (inverse) | `@JsonBackReference("contrat-paiement")` | Empêche la sérialisation récursive vers le contrat parent tout en permettant à Jackson de restaurer la référence bidirectionnelle lors de la désérialisation. |
| `Agence` | `employes` (`List<Employe>`) | `@OneToMany` | `@JsonIgnore` | Évite le cycle `Agence -> Employe -> Agence` et le surcoût de sérialisation des listes volumineuses en cascade. |
| `Agence` | `vehicules` (`List<Vehicule>`) | `@OneToMany` | `@JsonIgnore` | Évite le cycle `Agence -> Vehicule -> Agence`. |
| `Client` | `reservations` (`List<Reservation>`) | `@OneToMany` | `@JsonIgnore` | Évite le cycle `Client -> Reservation -> Client`. |
| `Vehicule` | `maintenances` (`List<Maintenance>`) | `@OneToMany` | `@JsonIgnore` | Évite le cycle `Vehicule -> Maintenance -> Vehicule`. |
| `Vehicule` | `reservations` (`List<Reservation>`) | `@OneToMany` | `@JsonIgnore` | Évite le cycle `Vehicule -> Reservation -> Vehicule`. |
| `Vehicule` | `equipements` (`List<Equipement>`) | `@ManyToMany` | `@JsonIgnore` | Évite la boucle Many-to-Many entre véhicules et équipements. |
| `Equipement` | `vehicules` (`List<Vehicule>`) | `@ManyToMany` | `@JsonIgnore` | Évite la boucle inverse Many-to-Many. |
| `Reservation` | `contrat` (`Contrat`) | `@OneToOne` | `@JsonIgnore` | Évite le cycle bidirectionnel `Reservation -> Contrat -> Reservation`. |

### 2. Différence entre `@JsonBackReference` et `@JsonIgnore`

* **`@JsonBackReference`** :
  * Utilisé en paire indissociable avec `@JsonManagedReference(name)`.
  * Il est conçu pour les relations de composition hiérarchiques parent-enfant.
  * **Comportement :** À la sérialisation (Java → JSON), l'attribut annoté est omis pour stopper la récursion. À la désérialisation (JSON → Java), Jackson réinjecte automatiquement la référence de l'objet parent dans l'enfant !
* **`@JsonIgnore`** :
  * Annotation unidirectionnelle et inconditionnelle.
  * L'attribut annoté est **totalement ignoré** tant à la sérialisation qu'à la désérialisation.
  * Si on l'utilisait sur `Paiement.contrat`, la relation bidirectionnelle ne serait pas reconstituée lors d'un POST en cascade. Inversement, sur les listes inverses (comme `Agence.vehicules`), `@JsonIgnore` est idéal car l'enfant (`Vehicule`) référence simplement son parent par son identifiant (`{ "agence": {"idAgence": 1} }`).

---

## C. Comparatif Postman vs Swagger (OpenAPI)

| Critère | Postman | Swagger UI / SpringDoc OpenAPI |
| :--- | :--- | :--- |
| **Rôle principal** | Client HTTP complet de test, validation et automatisation | Spécification formelle du contrat d'API et documentation interactive |
| **Cycle de vie** | Les requêtes et scénarios sont conçus manuellement par le testeur | Documentation générée dynamiquement au démarrage par réflexion sur le code |
| **Automatisation & Scripts** | Permet des scripts de pré-requête et d'assertions (`pm.test`, `pm.environment.set`) | Essai unitaire manuel (« Try it out »), pas de chaîne de tests scriptée |
| **Synchronisation avec le code** | Peut diverger si l'API évolue sans mise à jour de la collection | Toujours à 100% synchronisé avec les contrôleurs Java réels |

### Exemple concret rencontré dans l'atelier :
* **Postman :** A permis d'automatiser le flux complet du CRUD : le POST sur `/api/contrats` a extrait automatiquement l'identifiant généré via `pm.environment.set("contratId", pm.response.json().idContrat)` pour alimenter immédiatement les requêtes suivantes de consultation (`GET /api/contrats/{{contratId}}`), de mise à jour (`PUT`), et de suppression (`DELETE`).
* **Swagger UI :** A permis au développeur et aux intégrateurs tiers de visualiser instantanément le schéma JSON attendu pour `Contrat`, les codes de réponse documentés (`200`, `201`, `400`, `404`), et d'exécuter des requêtes curl d'un simple clic sur `http://localhost:8080/api/swagger-ui.html`.

---

## D. Anomalies SonarQube for IDE & Bonnes Pratiques

Lors de l'analyse du package `web.controller` avec SonarLint / SonarQube for IDE, les règles suivantes ont été ciblées et respectées :

1. **Règle `java:S6813` — *Field dependency injection should be avoided (Constructor injection preferred)***
   * *Explication :* L'injection par champ via `@Autowired` masque les dépendances réelles de la classe, empêche l'immutabilité (`final`) et complique l'instanciation manuelle lors des tests unitaires isolés.
   * *Correction appliquée :* Tous les contrôleurs utilisent des champs privés `final` et l'annotation Lombok `@RequiredArgsConstructor` pour générer un constructeur explicite.
2. **Règle `java:S1128` — *Unused imports should be removed***
   * *Explication :* Laisser des imports résiduels pollue la lisibilité du code et peut masquer des conflits de classes de même nom.
   * *Correction appliquée :* Nettoyage strict des imports dans tous les contrôleurs et classes de configuration.
3. **Règle architecturale — *Thin Controllers (Contrôleurs minces)***
   * *Explication :* Un contrôleur REST ne doit jamais contenir de logique métier ni manipuler directement un `Repository`.
   * *Correction appliquée :* Les contrôleurs dépendent uniquement des interfaces de service (`IContratService`, etc.) et se limitent à la désérialisation, l'aiguillage HTTP et l'assignation des statuts (`@ResponseStatus`).

---

## E. Réponses aux Questions de Compréhension

### 1. Quel composant reçoit toutes les requêtes, et comment retrouve-t-il la méthode du contrôleur à appeler ?
Toutes les requêtes HTTP entrantes sont interceptées par la **`DispatcherServlet`** (Front Controller de Spring MVC).  
Elle délègue la recherche au **`HandlerMapping`** (notamment `RequestMappingHandlerMapping`), qui consulte la table de routage construite au démarrage de l'application à partir des annotations `@RestController`, `@RequestMapping`, `@GetMapping`, `@PostMapping`, etc., pour identifier la méthode exacte du contrôleur correspondant au couple `{Méthode HTTP, Chemin d'URL}`.

### 2. Pourquoi `ContratController` dépend-il de `IContratService` et non de `ContratRepository` ni de `ContratServiceImpl` ?
* **Contre `ContratRepository` :** Respect strict de l'architecture en couches (Séparation des préoccupations). Le contrôleur appartient à la couche présentation/web ; il ne doit pas court-circuiter la couche métier (`Service`) où sont logées les règles de validation, la gestion des transactions et les contrôles d'intégrité.
* **Contre `ContratServiceImpl` :** Principe d'inversion des dépendances (DIP des principes SOLID). Dépendre de l'interface `IContratService` garantit un couplage faible, permettant de substituer l'implémentation métier sans modifier le contrôleur, et facilitant grandement l'injection de mocks lors des tests.

### 3. Pourquoi un POST renvoie-t-il 201 et un DELETE 204 ? Pourquoi une ressource absente donne-t-elle 404 et non 500 ?
* **POST → `201 Created` :** Indique explicitement que la requête a abouti à la création d'une nouvelle ressource sur le serveur (avec son nouvel identifiant).
* **DELETE → `204 No Content` :** L'action de suppression a réussi, mais il n'y a plus aucun contenu à renvoyer dans le corps de la réponse.
* **404 vs 500 :**
  * Le code `404 Not Found` est une erreur **client** (le client a demandé un identifiant qui n'existe pas en base : c'est un état attendu et normal du domaine).
  * Le code `500 Internal Server Error` est une erreur **serveur** inattendue (bug, panne de base, exception non gérée). Renvoyer un 500 pour une ressource introuvable masquerait un mauvais fonctionnement de l'application.

### 4. Pourquoi `PaiementController` n'expose-t-il que des GET ? Quelle réponse reçoit un POST `/api/paiements` ?
* **Pourquoi seulement des GET ?** Dans la modélisation métier d'AutoLoc, le `Paiement` est lié au `Contrat` dans une relation de **composition forte**. Un paiement n'a pas d'existence autonome en dehors de son contrat : son cycle de vie est intégralement pris en charge par le contrat (cascade JPA `ALL` et `orphanRemoval`). Les paiements sont donc créés, modifiés ou supprimés uniquement à travers le contrat.
* **Réponse à un POST `/api/paiements` :** Comme aucune méthode `@PostMapping` n'est définie dans `PaiementController`, Spring MVC renvoie automatiquement un statut HTTP **`405 Method Not Allowed`** avec l'en-tête `Allow: GET, HEAD, OPTIONS`.

### 5. Quels risques présente l'exposition directe des entités JPA ? Pourquoi `Contrat.paiements` utilise-t-il `@JsonManagedReference` alors que `Agence.vehicules` utilise `@JsonIgnore` ?
* **Risques de l'exposition directe des entités JPA :**
  1. *Couplage fort :* Toute modification du modèle physique de base de données brise immédiatement le contrat de l'API REST pour les clients externes.
  2. *Fuite de données internes :* Des colonnes sensibles, techniques ou internes (mots de passe, versions Hibernate, métadonnées d'audit) risquent d'être exposées involontairement dans le JSON.
  3. *Problèmes de sérialisation et performance :* Boucles infinies de récursion sur les associations bidirectionnelles, et requêtes N+1 imprévues déclenchées par Jackson lors de l'accès aux proxies `@ManyToOne` ou collections `@OneToMany` LAZY.
* **Pourquoi `@JsonManagedReference` sur `Contrat.paiement` et `@JsonIgnore` sur `Agence.vehicules` ?**
  * Pour `Contrat ↔ Paiement`, il s'agit d'une composition où l'on souhaite expressément inclure le paiement dans le JSON du contrat lors de sa consultation et de sa création. La paire `@JsonManagedReference` / `@JsonBackReference` permet de sérialiser l'objet enfant tout en reconstituant le lien bidirectionnel en mémoire.
  * Pour `Agence ↔ Vehicule`, les véhicules sont des entités indépendantes créées individuellement. Sérialiser la liste des véhicules dans chaque agence produirait des JSON énormes et une boucle avec `vehicule.agence`. L'usage de `@JsonIgnore` coupe la boucle simplement, le véhicule désignant son agence par son identifiant.
