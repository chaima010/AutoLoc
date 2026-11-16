# Atelier 3 — Couche Repository Spring Data JPA

## 1. Choix d'interface — Justification par entité

> **Rappel hiérarchie Spring Data JPA (Spring Data 3+)**
>
> ```
> Repository<T, ID>                          interface marqueur, aucune méthode
>  ├── CrudRepository<T, ID>                 CRUD de base (Iterable)
>  │    └── ListCrudRepository<T, ID>        idem, collections en List
>  └── PagingAndSortingRepository<T, ID>     tri et pagination (plus de CRUD depuis SD3)
>       └── ListPagingAndSortingRepository   idem, collections en List
>
> JpaRepository<T, ID>  =  ListCrudRepository
>                        + ListPagingAndSortingRepository
>                        + QueryByExampleExecutor
>                        + flush / saveAndFlush / getReferenceById / suppressions en lot
> ```

| Interface (package `repository`) | Entend | Justification |
|---|---|---|
| `ICandidateRepository` | `JpaRepository<Candidate, Long>` | CRUD complet ; `saveAndFlush` utilisé dans `CandidateService.update` et `assignAddress` ; `findAll` renvoie une `List` (iteration directe dans `findAll()`) ; méthodes dérivées `existsByEmail*` pour unicité de l'e-mail. |
| `IAddressRepository` | `JpaRepository<Address, Long>` | Lectures directes des adresses existantes ; `getReferenceById` disponible pour lier une adresse à un candidat sans SELECT supplémentaire. |
| `IJobRepository` | `JpaRepository<Job, Long>` | CRUD complet ; requête JPQL `search` avec JOIN FETCH pour éviter N+1 ; `existsByCategoryId` pour la règle 409 avant suppression d'une catégorie. |
| `ICategoryRepository` | `JpaRepository<Category, Long>` | CRUD complet ; méthodes dérivées `existsByName*` pour unicité du nom de catégorie. |
| `IApplicationRepository` | `JpaRepository<Application, Long>` | CRUD complet ; requête JPQL `search` avec triple JOIN FETCH (candidat + job + catégorie) ; trois méthodes `existsBy*` pour les règles métier (doublon candidature, blocage suppression candidat/job). |

> **Remarque importante — suppressions en lot :**  
> Les méthodes `deleteAllInBatch` et `deleteAllByIdInBatch` héritées de `JpaRepository`
> contournent le contexte de persistance : la **cascade** et l'**orphanRemoval** ne s'appliquent pas.
> Dans AWD, supprimer des candidatures avec `deleteAllInBatch` laisserait des incohérences si des
> contraintes de clés étrangères existent. Toujours préférer `deleteById` ou `delete` pour respecter
> le cycle de vie JPA.

---

## 2. Convention de nommage — Préfixe `I`

Toutes les interfaces repository commencent par `I` (ex. `ICandidateRepository`).  
L'annotation `@Repository` n'est **pas** nécessaire : Spring Data génère le proxy au démarrage.

Les anciennes interfaces (`CandidateRepository`, `AddressRepository`, `JobRepository`,
`CategoryRepository`, `ApplicationRepository`) ont été **supprimées**. Tous les services
injectent et utilisent directement les interfaces `I…Repository` :

| Service | Ancienne injection | Nouvelle injection |
|---|---|---|
| `CandidateService` | `CandidateRepository` | `ICandidateRepository` |
| `AddressService` | `AddressRepository` | `IAddressRepository` |
| `JobService` | `JobRepository` + `ApplicationRepository` | `IJobRepository` + `IApplicationRepository` |
| `CategoryService` | `CategoryRepository` + `JobRepository` | `ICategoryRepository` + `IJobRepository` |
| `ApplicationService` | `ApplicationRepository` | `IApplicationRepository` |

---

## 3. Anomalies SonarQube for IDE (SonarLint) relevées et corrigées

### Anomalie 1 — Annotation `@Repository` superflue sur interface Spring Data

| Champ | Détail |
|---|---|
| **Règle SonarQube** | `java:S6856` — *"Spring '@Repository' annotation should not be used on interfaces extending Spring Data Repository"* |
| **Fichier concerné** | `CandidateRepository.java`, `AddressRepository.java`, `JobRepository.java`, `CategoryRepository.java`, `ApplicationRepository.java` (dans le code d'origine) |
| **Description Sonar** | L'annotation `@Repository` est redondante sur une interface qui étend `org.springframework.data.repository.Repository` (ou `JpaRepository`). Spring Data détecte automatiquement ces interfaces et crée un proxy annoté à l'exécution. |
| **Correction apportée** | Suppression de l'annotation `@Repository` sur toutes les nouvelles interfaces `ICandidateRepository`, `IAddressRepository`, `IJobRepository`, `ICategoryRepository` et `IApplicationRepository`. |

---

### Anomalie 2 — Imports inutilisés (Unnecessary imports)

| Champ | Détail |
|---|---|
| **Règle SonarQube** | `java:S1128` — *"Unnecessary imports should be removed"* |
| **Fichier concerné** | Classes de service et repositories lors des refactorisations successives |
| **Description Sonar** | Les déclarations d'imports superflues ou inutilisées alourdissent le code, polluent l'historique de versions et nuisent à la lisibilité. |
| **Correction apportée** | Nettoyage de tous les imports inutilisés (`import org.springframework.stereotype.Repository;`, anciens repositories non qualifiés) dans l'ensemble des fichiers modifiés. |

---

### Anomalie 3 — Convention de nommage des interfaces (`I…`)

| Champ | Détail |
|---|---|
| **Règle SonarQube** | `java:S114` — *"Interface names should comply with a naming convention"* |
| **Fichier concerné** | `ICandidateRepository`, `IAddressRepository`, `IJobRepository`, etc. |
| **Description Sonar** | Par défaut, SonarQube signale la notation hongroise (préfixe `I`) avec l'expression régulière `^[A-Z][a-zA-Z0-9]*$`. |
| **Correction / Justification** | Cette règle est explicative dans le cadre de l'atelier : la convention pédagogique impose le préfixe `I` (`IVehiculeRepository`, `IContratRepository`, `ICandidateRepository`…). L'alerte SonarLint a été examinée et la convention du projet est documentée et assumée dans l'architecture. |

---

### Anomalie 4 — Usage de `@Data` Lombok sur une entité JPA

| Champ | Détail |
|---|---|
| **Règle SonarQube** | `java:S6539` — *"Entities should not use Lombok @Data"* |
| **Fichier concerné** | Entités JPA (`Candidate.java`, `Address.java`, `Job.java`, etc.) |
| **Description Sonar** | L'annotation `@Data` génère automatiquement un `toString()`, `equals()` et `hashCode()` utilisant tous les champs, y compris les associations lazy/bidirectionnelles (`@OneToOne`, `@ManyToOne`, `@OneToMany`). Cela engendre des `StackOverflowError` par récursion et perturbe le dirty-checking d'Hibernate dans les collections `Set`. |
| **Correction apportée** | Aucune entité n'utilise `@Data` : les entités du projet AWD utilisent des getters/setters explicites sans surcharger aveuglément `equals`/`hashCode`, respectant ainsi les recommandations SonarQube et Spring Data JPA. |

---

## 4. Comportement de `save()` — Tableau récapitulatif

| Situation | Opération JPA déclenchée | Log SQL attendu |
|---|---|---|
| `id == null` (entité nouvelle) | `persist` → **INSERT** | `insert into candidate (firstname, lastname, email, address_id) values (?, ?, ?, ?)` |
| `id` renseigné (entité existante) | `merge` → SELECT + **UPDATE** si changement | `select … where id=?` puis `update candidate set … where id=?` |

> **Règle :** toujours utiliser **l'objet retourné** par `save()` ; il contient l'identifiant assigné par MySQL (stratégie `IDENTITY`) et peut différer de l'objet passé en paramètre après le `merge`.

---

## 5. `deleteById` vs suppressions en lot

| Méthode | Passe par le contexte de persistance ? | Cascade / orphanRemoval ? |
|---|---|---|
| `deleteById(id)` | ✅ Oui (SELECT puis DELETE) | ✅ Appliqués |
| `delete(entity)` | ✅ Oui | ✅ Appliqués |
| `deleteAllInBatch()` | ❌ Non (DELETE direct) | ❌ Non appliqués |
| `deleteAllByIdInBatch(ids)` | ❌ Non (DELETE direct) | ❌ Non appliqués |

Dans AWD, la règle métier interdit la suppression d'un candidat ou d'un job ayant des candidatures (409 Conflict). La vérification se fait via `IApplicationRepository.existsByCandidateId` / `existsByJobId` avant toute suppression.

---

## 6. Commit Git

```bash
# Ajout des nouvelles interfaces et documentation
git add awd-monolith/src/main/java/com/awd/monolith/candidat/repository/ICandidateRepository.java
git add awd-monolith/src/main/java/com/awd/monolith/candidat/repository/IAddressRepository.java
git add awd-monolith/src/main/java/com/awd/monolith/job/repository/IJobRepository.java
git add awd-monolith/src/main/java/com/awd/monolith/job/repository/ICategoryRepository.java
git add awd-monolith/src/main/java/com/awd/monolith/candidature/repository/IApplicationRepository.java
git add awd-monolith/docs/repository-notes.md

# Prise en compte de la mise à jour des services
git add awd-monolith/src/main/java/com/awd/monolith/candidat/service/CandidateService.java
git add awd-monolith/src/main/java/com/awd/monolith/candidat/service/AddressService.java
git add awd-monolith/src/main/java/com/awd/monolith/job/service/JobService.java
git add awd-monolith/src/main/java/com/awd/monolith/job/service/CategoryService.java
git add awd-monolith/src/main/java/com/awd/monolith/candidature/service/ApplicationService.java

# Suppression des anciennes interfaces (code mort éliminé)
git rm awd-monolith/src/main/java/com/awd/monolith/candidat/repository/CandidateRepository.java
git rm awd-monolith/src/main/java/com/awd/monolith/candidat/repository/AddressRepository.java
git rm awd-monolith/src/main/java/com/awd/monolith/job/repository/JobRepository.java
git rm awd-monolith/src/main/java/com/awd/monolith/job/repository/CategoryRepository.java
git rm awd-monolith/src/main/java/com/awd/monolith/candidature/repository/ApplicationRepository.java

git commit -m "Atelier 3 : couche Repository Spring Data JPA — interfaces I…Repository + suppression code mort"
```

