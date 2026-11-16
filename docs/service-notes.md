# Atelier 4 — Inversion de Contrôle (IoC), Injection de Dépendances (DI) et Couche Service

---

## A. Modes d'injection comparés

Expérience menée conformément à l'étape 2 : comparaison entre l'injection par constructeur (recommandée) et l'injection par attribut (`@Autowired`).

| Critère | Injection par constructeur | Injection par attribut (`@Autowired`) |
|---|---|---|
| **Champs `final` possibles ?** | **Oui** — initialise obligatoirement toutes les dépendances lors de l'instanciation | **Non** — Spring doit injecter les champs après l'appel du constructeur par réflexion |
| **Dépendance visible dans la signature ?** | **Oui** — explicite dans la liste des paramètres du constructeur | **Non** — dépendances cachées dans des champs privés |
| **Utilisation avec `new` hors conteneur ?** | **Oui** — instanciable simplement dans un test unitaire avec des mocks (`new ContratServiceImpl(mockRepo)`) | **Difficile** — nécessite un framework de réflexion ou `ReflectionTestUtils` ; sinon les champs restent `null` |
| **Objet garanti dans un état valide ?** | **Oui** — impossible d'instancier la classe sans fournir toutes ses dépendances requises | **Non** — risque de `NullPointerException` si une dépendance manque ou n'est pas injectée |
| **Signalé par SonarQube for IDE ?** | **Non** — conforme aux meilleures pratiques Clean Code | **Oui** — alerte de règle `java:S6813` (*"Field dependency injection should not be used"*) |

> **Convention adoptée dans AutoLoc :** Injection par constructeur exclusive avec champs `private final` et constructeur généré par Lombok `@RequiredArgsConstructor`.

---

## B. Couche Service — Dépendances et justifications des 9 services

Toutes les dépendances sont typées par des interfaces repository et injectées par constructeur (`@RequiredArgsConstructor` avec champs `final`).

| Service (`ServiceImpl`) | Interface exposée | Dépendances injectées (Interfaces) | Mode d'injection | Justification |
|---|---|---|---|---|
| `ContratServiceImpl` | `IContratService` | `IContratRepository` | Constructeur (`@RequiredArgsConstructor`) | CRUD contrat, validation d'un montant total positif, cascade vers les paiements. |
| `PaiementServiceImpl` | `IPaiementService` | `IPaiementRepository` | Constructeur (`@RequiredArgsConstructor`) | Lecture seule (`findById`, `findAll`) : la création et la modification passent obligatoirement par le `Contrat`. |
| `AgenceServiceImpl` | `IAgenceService` | `IAgenceRepository` | Constructeur (`@RequiredArgsConstructor`) | CRUD agence, validation des attributs obligatoires (nom non vide) et gestion du cycle de vie. |
| `EmployeServiceImpl` | `IEmployeService` | `IEmployeRepository` | Constructeur (`@RequiredArgsConstructor`) | CRUD employé, vérification des attributs obligatoires (nom et rôle) et rattachement d'agence. |
| `VehiculeServiceImpl` | `IVehiculeService` | `IVehiculeRepository` | Constructeur (`@RequiredArgsConstructor`) | CRUD véhicule, validation de l'immatriculation obligatoire et tarif journalier positif. |
| `EquipementServiceImpl` | `IEquipementService` | `IEquipementRepository` | Constructeur (`@RequiredArgsConstructor`) | CRUD équipement, validation du libellé obligatoire. |
| `ClientServiceImpl` | `IClientService` | `IClientRepository` | Constructeur (`@RequiredArgsConstructor`) | CRUD client, validation des données d'identification (nom et e-mail obligatoires), initialisation de dateInscription. |
| `ReservationServiceImpl` | `IReservationService` | `IReservationRepository` | Constructeur (`@RequiredArgsConstructor`) | CRUD réservation, validation temporelle (dateFin >= dateDebut) et intégrité client/véhicule. |
| `MaintenanceServiceImpl` | `IMaintenanceService` | `IMaintenanceRepository` | Constructeur (`@RequiredArgsConstructor`) | CRUD maintenance, validation temporelle (dateFin >= dateDebut) et suivi de véhicule. |

---

## C. Diagnostic des messages d'erreur du conteneur Spring IoC

### Message 1
```text
APPLICATION FAILED TO START
Description:
Parameter 0 of constructor in tn.esprit.autoloc.web.controller.ContratController 
required a bean of type 'tn.esprit.autoloc.service.IContratService' that could not be found.
Action:
Consider defining a bean of type 'tn.esprit.autoloc.service.IContratService' in your configuration.
```
* **Exception levée :** `NoSuchBeanDefinitionException` (enveloppée dans une `UnsatisfiedDependencyException`).
* **Deux causes les plus probables côté `ContratServiceImpl` :**
  1. **Annotation manquante :** La classe `ContratServiceImpl` a oublié d'être annotée avec `@Service` (ou `@Component`), donc Spring ne l'a pas enregistrée comme bean.
  2. **Package hors du scan :** La classe `ContratServiceImpl` se trouve dans un package qui n'est pas un sous-package de la classe annotée `@SpringBootApplication` (ex: package `com.example.service` alors que l'application est dans `com.example.location`).
* **Correction :** Ajouter l'annotation `@Service` sur `ContratServiceImpl` et s'assurer qu'elle se trouve bien dans l'arborescence scannée par `@SpringBootApplication`.

---

### Message 2
```text
Description:
Parameter 0 of constructor in tn.esprit.autoloc.web.controller.NotificationController 
required a single bean, but 2 were found:
    - emailNotificateur
    - smsNotificateur
Action:
Consider marking one of the beans as @Primary, updating the consumer to accept multiple beans, 
or using @Qualifier to identify the bean that should be consumed
```
* **Exception levée :** `NoUniqueBeanDefinitionException`.
* **Cause :** Deux classes distinctes (`EmailNotificateur` et `SmsNotificateur`) implémentent la même interface demandée, et Spring ne peut pas arbitrer sans instruction.
* **Deux corrections différentes :**
  1. **Option 1 — `@Primary` sur la classe :** Poser l'annotation `@Primary` directement sur la classe candidate par défaut :
     ```java
     @Component
     @Primary
     public class EmailNotificateur implements Notificateur { ... }
     ```
  2. **Option 2 — `@Qualifier` au point d'injection :** Poser `@Qualifier("emailNotificateur")` sur le paramètre du constructeur du contrôleur :
     ```java
     public NotificationController(@Qualifier("emailNotificateur") Notificateur notificateur) {
         this.notificateur = notificateur;
     }
     ```

---

### Message 3
```text
The dependencies of some of the beans in the application context form a cycle:
   clientServiceImpl
        |        ^
        v        |
   reservationServiceImpl
```
* **Exception levée :** `BeanCurrentlyInCreationException`.
* **Pourquoi l'injection par constructeur rend-elle ce cycle impossible à contourner ?**
  Pour instancier `clientServiceImpl`, Spring doit appeler son constructeur et donc lui fournir une instance déjà existante de `reservationServiceImpl`. Mais pour instancier `reservationServiceImpl`, Spring doit appeler son constructeur et donc lui fournir `clientServiceImpl`. Aucun des deux objets ne peut être créé en premier : le graphe d'instanciation est bloqué. Contrairement à l'injection par attribut qui pouvait tolérer le cycle en créant des objets incomplets à l'état brut puis en renseignant les champs plus tard, l'injection par constructeur garantit que seuls des objets complets existent, révélant ainsi les faiblesses de conception dès le démarrage.
* **Piste de conception pour supprimer le cycle :**
  1. **Extraction de responsabilité :** Extraire la logique ou la responsabilité commune dans un troisième service (ex: `ReservationValidationService` ou un service coordinateur de plus haut niveau).
  2. **Découplage par événements ou injection fine :** Injecter le repository de l'autre entité plutôt que le service entier, ou émettre un événement applicatif Spring (`ApplicationEventPublisher` et `@EventListener`).

---

## D. Anomalies SonarQube for IDE (SonarLint)

| Règle SonarQube | Intitulé officiel | Explication du problème | Correction apportée |
|---|---|---|---|
| `java:S6813` | *"Field dependency injection should not be used"* | L'injection de dépendances par `@Autowired` sur des attributs privés masque les dépendances, empêche les champs `final` et complique les tests unitaires isolés. | Remplacement systématique par l'injection par constructeur avec champs `private final` et constructeur généré par `@RequiredArgsConstructor`. |
| `java:S112` | *"Generic exceptions should never be thrown"* | Lever des exceptions génériques telles que `RuntimeException` ou `Exception` dans le code métier nuit à la précision de la gestion d'erreurs et empêche les contrôleurs REST de renvoyer des statuts HTTP appropriés (404 vs 500). | Création et utilisation d'une exception métier explicite : `ResourceNotFoundException` (traduite en 404). |
| `java:S114` | *"Interface names should comply with a naming convention"* | SonarLint signale par défaut le préfixe hongrois `I` sur les interfaces Java (`^[A-Z][a-zA-Z0-9]*$`). | Convention pédagogique explicite de l'architecture en couches d'AutoLoc (`IContratService`, `IVehiculeService`…). La règle est documentée et assumée dans l'architecture. |
| `java:S1128` | *"Unnecessary imports should be removed"* | Présence d'imports inutilisés lors des refactorisations successives. | Suppression de tous les imports orphelins dans les services et repositories. |

---

## E. Questions de compréhension

### 1. Où est le `new` de `ContratServiceImpl` et qui l'exécute ?
Il n'y a **aucun `new ContratServiceImpl(...)`** dans le code source écrit par le développeur. C'est le conteneur Spring IoC (`ApplicationContext`) qui instancie la classe par introspection/réflexion au démarrage de l'application, après l'avoir découverte grâce à l'annotation `@Service` et au scan de composants déclenché par `@SpringBootApplication`.

### 2. Pourquoi un contrôleur dépendra-t-il de `IContratService` et non de `ContratServiceImpl` ?
Pour respecter le principe d'**Inversion des Dépendances (DIP)** des principes SOLID : les modules de haut niveau (contrôleurs) ne doivent pas dépendre des implémentations de bas niveau, mais d'**abstractions (interfaces)**.  
Cela assure un couplage faible :
- Le contrôleur ignore les détails d'implémentation (JPA, cache, appels distants).
- Les tests unitaires du contrôleur peuvent facilement fournir un mock de `IContratService`.
- On peut substituer l'implémentation (ex: version avec cache ou décorateur) sans modifier le contrôleur.

### 3. Pourquoi un service singleton doit-il rester sans état ? Donner un exemple de bug.
Parce qu'un bean singleton est partagé par l'ensemble des requêtes HTTP et exécuté concurremment par plusieurs threads du serveur web (Tomcat).  
**Exemple de bug :** Si le service possède un champ mutable `private Contrat currentContrat;` :
1. La requête du client A appelle `create` : le thread 1 affecte `currentContrat = contratA`.
2. Simultanément, la requête du client B appelle `create` : le thread 2 affecte `currentContrat = contratB`.
3. Le thread 1 poursuit son traitement et enregistre le paiement pour `currentContrat` qui vaut désormais `contratB`. Le paiement du client A est crédité par erreur sur le contrat du client B (race condition critique).

### 4. Pourquoi `update` charge-t-il l'entité existante avant d'appeler `save` au lieu de lui passer directement l'objet reçu ?
Parce qu'appeler directement `save(nouveau)` déclenche un `merge()` qui écraserait toutes les colonnes non renseignées par des valeurs `null`. De plus, pour les associations configurées avec `orphanRemoval = true`, les collections liées non fournies seraient interprétées comme supprimées et vidées en base.  
En chargeant l'entité managée via `findById()`, puis en recopiant uniquement les attributs modifiables autorisés, on garantit la préservation des clés étrangères, de l'historique et de l'intégrité des associations (ex. préserver la collection de paiements d'un contrat).

### 5. Différences techniques et lecture seule :
* **`@Component` vs `@Bean` :**  
  - `@Component` s'applique sur une classe pour en faire un bean scanné automatiquement par Spring (utilisé pour les classes dont on possède le code source).  
  - `@Bean` s'applique sur une méthode au sein d'une classe `@Configuration` pour créer explicitement une instance (indispensable pour intégrer des classes tierces ou des bibliothèques externes).
* **`@Primary` vs `@Qualifier` :**  
  - `@Primary` définit le candidat par défaut directement sur la classe d'implémentation.  
  - `@Qualifier("nomDuBean")` force le choix d'un bean particulier au point d'injection (sur le constructeur ou le champ).
* **Lecture seule dans `IPaiementService` :**  
  Dans la relation de composition `Contrat` <-> `Paiement`, le paiement n'a pas de cycle de vie autonome : il ne doit être créé, modifié ou supprimé qu'à travers le contrat (agrégat racine) pour maintenir la cohérence du montant total et du solde. Le service `IPaiementService` reste donc strictement en lecture seule (`findById`, `findAll`).

