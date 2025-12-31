---
<p align="center">
  <img src="https://www.simplon.co/images/logo-simplon.png" alt="SIMPLON" height="80"/>
  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
  <img src="https://youcode.ma/assets/img/logo.png" alt="YOUCODE" height="80"/>
</p>

---

<p align="center">
  <img src="logo-aquasmart.png" alt="AquaSmart Logo" height="150"/>
</p>

<h1 align="center">AquaSmart</h1>
<h2 align="center">Plateforme SaaS d'Optimisation de l'Eau pour l'Agriculture</h2>

---

<h1 align="center">CAHIER DES CHARGES</h1>

---

<p align="center">
  <strong>Réalisé par :</strong> Abdeljabbar MOUDIRI – YouCode Safi<br/>
  <strong>Encadré par :</strong> Latifa AMOUGUAY – YouCode Safi
</p>

<p align="center">
  <strong>Date :</strong> Décembre 2025
</p>

---

# Chapitre I : Contexte du Projet

## 1. Introduction

Dans un contexte de raréfaction des ressources en eau et de défis climatiques croissants, l'agriculture marocaine, qui représente 80% de la consommation nationale d'eau, doit impérativement évoluer vers des pratiques plus durables et efficientes.

**AquaSmart** est une plateforme SaaS (Software as a Service) innovante conçue pour révolutionner la gestion de l'irrigation agricole au Maroc et dans la région MENA. Cette solution numérique intelligente combine technologies IoT, données météorologiques en temps réel et algorithmes de recommandation pour optimiser la consommation d'eau tout en améliorant la productivité agricole.

Ce projet s'inscrit dans le cadre du projet fil rouge de la formation YouCode et vise à démontrer nos compétences en développement d'applications web modernes basées sur une architecture microservices.

## 2. Problématique

Au Maroc et dans la région MENA, plusieurs défis critiques impactent la gestion de l'eau en agriculture :

- **Gaspillage massif d'eau** : Les pratiques d'irrigation traditionnelles et non optimisées entraînent un gaspillage estimé à 30-40% de l'eau utilisée
- **Manque de données en temps réel** : Les agriculteurs n'ont pas accès à des informations précises sur les besoins réels en eau de leurs cultures
- **Absence d'outils de décision** : Irrigation basée sur l'intuition et les habitudes plutôt que sur des données scientifiques
- **Baisse de productivité** : Le sur-arrosage ou sous-arrosage affecte négativement les rendements agricoles
- **Coûts élevés** : Factures d'eau et d'énergie importantes dues à l'inefficacité des systèmes d'irrigation
- **Impact environnemental** : Épuisement des nappes phréatiques et dégradation des sols
- **Fracture numérique** : Difficultés d'accès aux technologies modernes pour les petits exploitants

**Question centrale** : Comment permettre aux agriculteurs marocains d'optimiser leur consommation d'eau de manière simple, accessible et efficace grâce au numérique ?

## 3. Solution Proposée

**AquaSmart** propose une solution technologique complète et accessible répondant aux défis identifiés :

### 3.1 Vision de la solution

Une plateforme SaaS intelligente accessible via navigateur web et application mobile, permettant aux agriculteurs de :
- Recevoir des **recommandations d'irrigation personnalisées** basées sur des données météo réelles
- Suivre la **consommation d'eau en temps réel** avec historiques et statistiques
- Être alertés automatiquement en cas de **risques de sur-arrosage ou sécheresse**
- Planifier leurs cycles d'irrigation de manière **optimale et automatisée**
- Accéder à des informations sur les **subventions et programmes de soutien**

### 3.2 Approche technique

L'architecture microservices garantit :
- **Scalabilité** : Capacité à gérer des milliers d'utilisateurs simultanés
- **Modularité** : Ajout facile de nouvelles fonctionnalités (IA, IoT)
- **Sécurité** : Authentification JWT et protection des données sensibles
- **Accessibilité** : Interface intuitive adaptée aux utilisateurs ruraux
- **Intégration IoT** : Connexion possible avec des capteurs pour automatisation complète

### 3.3 Valeur ajoutée

- ✅ Réduction de 30-40% de la consommation d'eau
- ✅ Amélioration des rendements agricoles de 15-20%
- ✅ Économies sur les factures d'eau et d'énergie
- ✅ Contribution à la durabilité environnementale
- ✅ Digitalisation du secteur agricole marocain

## 4. Objectifs du Projet

### 4.1 Objectifs fonctionnels

- Fournir un **outil numérique d'aide à la décision** pour l'irrigation intelligente
- Réduire significativement le **gaspillage d'eau** dans les exploitations agricoles
- Améliorer les **rendements agricoles** grâce à une irrigation optimisée
- Offrir une **interface claire et intuitive** accessible aux agriculteurs, ONG et coopératives
- Permettre l'accès à des **statistiques et alertes en temps réel**
- Promouvoir la **transition vers une agriculture durable** au Maroc

### 4.2 Objectifs techniques

- Développer une architecture **microservices moderne et scalable**
- Implémenter un système d'**authentification sécurisé** (JWT)
- Intégrer des **API externes** (OpenWeatherMap) pour données météo
- Créer une **API REST** robuste et bien documentée
- Assurer la **conteneurisation** complète avec Docker
- Garantir la **haute disponibilité** et la performance

### 4.3 Objectifs pédagogiques

- Mettre en pratique les compétences acquises en développement Full Stack
- Maîtriser l'architecture microservices avec Spring Cloud
- Appliquer les bonnes pratiques de sécurité et de développement
- Gérer un projet de bout en bout (conception, développement, déploiement)

## 5. Public Ciblé

### 5.1 Utilisateurs primaires

- **Agriculteurs individuels** : Petits et moyens exploitants cherchant à optimiser leurs ressources
- **Coopératives agricoles** : Gestion centralisée de multiples exploitations
- **Exploitations familiales** : Transmission de pratiques durables entre générations

### 5.2 Utilisateurs secondaires

- **ONG et associations rurales** : Accompagnement et formation des agriculteurs
- **Techniciens agricoles** : Conseillers et experts en irrigation
- **Institutions publiques** : Ministères, offices régionaux de mise en valeur agricole

### 5.3 Utilisateurs tertiaires

- **Fournisseurs de matériel IoT** : Partenariats pour capteurs et équipements
- **Programmes de développement** : Organismes de financement et subventions
- **Chercheurs** : Études sur l'impact de la digitalisation agricole

## 6. Périmètre du Projet

### 6.1 Inclus dans le MVP (Version 1.0)

✅ **Module Authentification et Gestion des Utilisateurs**
- Inscription et connexion sécurisée (JWT)
- Gestion des rôles (Admin, Agriculteur, ONG)
- Profil utilisateur

✅ **Module Gestion des Exploitations**
- CRUD des exploitations agricoles
- Association utilisateur-exploitation
- Informations : localisation, superficie, types de cultures

✅ **Module Météo**
- Intégration API OpenWeatherMap
- Données météo en temps réel et prévisions
- Historique météorologique

✅ **Module Planification d'Irrigation**
- Recommandations automatiques basées sur météo et culture
- Calendrier d'arrosage personnalisé
- Calcul des besoins en eau

✅ **Module Suivi de Consommation**
- Enregistrement de l'irrigation effectuée
- Historique détaillé
- Statistiques et graphiques (jour, semaine, mois)

✅ **Module Alertes Intelligentes**
- Notifications sur excès ou manque d'eau
- Alertes personnalisables selon seuils
- Système de notification en temps réel

✅ **Tableau de Bord**
- Vue synthétique de l'exploitation
- Widgets météo, consommation, alertes
- Indicateurs clés de performance (KPI)

✅ **Module Informations et Subventions**
- Base de données des programmes d'aide
- Conseils et bonnes pratiques

### 6.2 Prévu pour la Version 2.0 (Fonctionnalités avancées)

🔄 **Intégration IoT**
- Connexion capteurs humidité du sol (ESP32)
- Capteurs température/humidité (DHT22)
- Automatisation complète de l'irrigation

🔄 **Stockage Temps Réel**
- Intégration InfluxDB pour données capteurs
- Visualisation via Grafana

🔄 **Intelligence Artificielle**
- Prédictions basées sur Machine Learning
- Suggestions automatiques optimisées par IA

🔄 **Application Mobile**
- Version Android/iOS native
- Notifications push

### 6.3 Hors périmètre

❌ E-commerce et vente de matériel
❌ Gestion financière et comptabilité agricole
❌ Suivi de la production et récoltes (focus uniquement irrigation)
❌ Réseau social pour agriculteurs
❌ Marketplace de services agricoles

### 6.4 Contraintes et Limitations

- **Temps** : 6-8 semaines de développement
- **Budget** : Prototype IoT limité à ~300 MAD
- **Technologies** : Stack imposée (Spring Boot, Angular, MongoDB)
- **Déploiement** : Hébergement gratuit ou low-cost (Render, Railway)
- **Données** : Dépendance à l'API OpenWeatherMap (quotas gratuits)

---

# Chapitre II : Description Fonctionnelle et Technique

## 1. Description Fonctionnelle (Métier)

### 1.1 Modules Principaux et Fonctionnalités

#### Module 1 : Authentification et Gestion des Utilisateurs

**Objectif métier** : Sécuriser l'accès à la plateforme et gérer les différents profils d'utilisateurs.

**Fonctionnalités détaillées** :
- **Inscription** : Création de compte avec email, mot de passe, nom, prénom, téléphone
- **Connexion sécurisée** : Authentification via JWT (JSON Web Token)
- **Gestion des rôles** :
  - **ADMIN** : Administration complète de la plateforme
  - **FARMER** (Agriculteur) : Gestion de ses propres exploitations
  - **NGO** (ONG) : Accès multi-exploitations pour accompagnement
- **Profil utilisateur** : Modification des informations personnelles
- **Réinitialisation mot de passe** : Récupération de compte sécurisée

**Acteurs** : Tous les utilisateurs de la plateforme

**Règles métier** :
- Email unique par utilisateur
- Mot de passe fort requis (8 caractères min, majuscule, chiffre)
- Session limitée dans le temps (token expiration)

---

#### Module 2 : Gestion des Exploitations Agricoles

**Objectif métier** : Permettre la création et la gestion complète des exploitations agricoles.

**Fonctionnalités détaillées** :
- **Ajouter une exploitation** :
  - Nom de l'exploitation
  - Localisation géographique (coordonnées GPS)
  - Superficie totale (en hectares)
  - Types de cultures pratiquées
  - Système d'irrigation actuel
- **Modifier une exploitation** : Mise à jour des informations
- **Supprimer une exploitation** : Suppression logique avec confirmation
- **Lister les exploitations** : Vue d'ensemble des fermes gérées
- **Association utilisateur-exploitation** : Gestion des accès

**Acteurs** : Agriculteurs, ONG, Administrateurs

**Règles métier** :
- Une exploitation appartient à un utilisateur principal
- Une ONG peut gérer plusieurs exploitations
- Coordonnées GPS validées
- Superficie > 0

---

#### Module 3 : Intégration Météorologique

**Objectif métier** : Fournir des données météo précises et actualisées pour optimiser l'irrigation.

**Fonctionnalités détaillées** :
- **Météo en temps réel** :
  - Température actuelle
  - Humidité de l'air
  - Vitesse et direction du vent
  - Précipitations
  - Pression atmosphérique
- **Prévisions météo** : 7 jours
- **Historique météorologique** : 30 derniers jours
- **Alertes météo** : Vagues de chaleur, gel, tempêtes

**Acteurs** : Système (automatique), Agriculteurs (consultation)

**Règles métier** :
- Actualisation toutes les 3 heures
- Source : API OpenWeatherMap
- Données géolocalisées par exploitation

---

#### Module 4 : Planification d'Irrigation

**Objectif métier** : Calculer et recommander les besoins optimaux en eau pour chaque culture.

**Fonctionnalités détaillées** :
- **Calcul des besoins en eau** :
  - Basé sur l'évapotranspiration (ETP)
  - Type de culture et stade de croissance
  - Données météo (température, humidité, vent)
  - Type de sol
- **Recommandations d'irrigation** :
  - Quantité d'eau à apporter (mm ou m³)
  - Fréquence d'arrosage optimale
  - Meilleur moment de la journée
- **Calendrier d'irrigation** :
  - Planning hebdomadaire et mensuel
  - Rappels et notifications
  - Ajustement automatique selon météo

**Acteurs** : Système (calcul), Agriculteurs (consultation et ajustement)

**Règles métier** :
- Coefficient cultural (Kc) par type de culture
- Prise en compte des précipitations récentes
- Ajustement selon efficience du système d'irrigation
- Minimum et maximum d'apport d'eau

---

#### Module 5 : Suivi de Consommation d'Eau

**Objectif métier** : Tracer et analyser la consommation réelle d'eau pour identifier les économies possibles.

**Fonctionnalités détaillées** :
- **Enregistrement manuel de l'irrigation** :
  - Date et heure
  - Quantité d'eau utilisée
  - Zone irriguée
  - Type de culture
- **Enregistrement automatique** (si IoT connecté) :
  - Données transmises par capteurs
  - Débit mesuré
- **Historique détaillé** :
  - Consultation par période (jour, semaine, mois, année)
  - Export des données (CSV, PDF)
- **Statistiques et visualisations** :
  - Graphiques de consommation
  - Comparaison consommation réelle vs recommandée
  - Taux d'économie réalisé
  - Evolution dans le temps

**Acteurs** : Agriculteurs, ONG, Administrateurs

**Règles métier** :
- Données conservées sur 2 ans minimum
- Validation des quantités saisies
- Agrégation automatique pour statistiques

---

#### Module 6 : Alertes Intelligentes

**Objectif métier** : Prévenir les agriculteurs en cas de risques liés à l'irrigation.

**Fonctionnalités détaillées** :
- **Types d'alertes** :
  - **Alerte sécheresse** : Sol trop sec détecté
  - **Alerte sur-arrosage** : Excès d'eau identifié
  - **Alerte météo** : Pluie prévue, gel, canicule
  - **Alerte économie** : Dépassement du budget eau
- **Configuration des seuils** :
  - Personnalisation par l'utilisateur
  - Seuils par défaut selon culture
- **Canaux de notification** :
  - Notifications in-app
  - Email
  - SMS (V2)
- **Historique des alertes** : Consultation et archivage

**Acteurs** : Système (génération), Agriculteurs (réception)

**Règles métier** :
- Fréquence maximale : 3 alertes/jour (éviter spam)
- Priorisation des alertes critiques
- Désactivation possible par type d'alerte

---

#### Module 7 : Tableau de Bord

**Objectif métier** : Offrir une vue d'ensemble synthétique et actionnable de l'état de l'exploitation.

**Fonctionnalités détaillées** :
- **Vue globale** :
  - Météo du jour
  - Nombre d'exploitations gérées
  - Consommation d'eau du mois
  - Alertes actives
- **Widgets personnalisables** :
  - Graphique consommation 7 derniers jours
  - Prochaines irrigations planifiées
  - Economie d'eau réalisée
  - Niveau d'humidité des sols (si IoT)
- **Indicateurs de performance (KPI)** :
  - % d'économie d'eau
  - Respect du planning d'irrigation
  - Coût moyen de l'eau/hectare
- **Accès rapide** aux fonctionnalités principales

**Acteurs** : Tous les utilisateurs

**Règles métier** :
- Actualisation en temps réel
- Interface responsive (mobile, tablette, desktop)
- Export des données possible

---

#### Module 8 : Informations et Subventions (Optionnel)

**Objectif métier** : Informer sur les aides financières et programmes de soutien à l'agriculture durable.

**Fonctionnalités détaillées** :
- **Base de données des subventions** :
  - Programmes nationaux (Maroc Vert, Plan Irrigation)
  - Aides régionales
  - Financements internationaux
- **Eligibilité** : Test d'éligibilité automatique
- **Conseils et bonnes pratiques** :
  - Techniques d'irrigation efficace
  - Choix des cultures adaptées au climat
- **Actualités** : Veille sur le secteur AgriTech

**Acteurs** : Agriculteurs, ONG

---

### 1.2 Rôles et Permissions

| Rôle | Permissions |
|---|---|
| **ADMIN** | Gestion complète : utilisateurs, exploitations, configuration, statistiques globales |
| **FARMER** | Gestion de ses exploitations, consultation météo, planification irrigation, suivi consommation, réception alertes |
| **NGO** | Accès multi-exploitations (accompagnement), consultation statistiques, génération rapports |

---

## 2. Description Technique

### 2.1 Architecture Globale

**Type d'architecture** : **Microservices distribués**

L'application AquaSmart est basée sur une architecture microservices qui sépare les responsabilités en services indépendants et scalables.

### 2.2 Stack Technique Complète

| Couche | Technologie |
|---|---|
| **Frontend** | Angular 18, HTML5, CSS3, TypeScript, Tailwind CSS, Chart.js |
| **Backend** | Spring Boot 3.2, Java 17, Spring Security 6, Spring Data MongoDB, Spring Cloud |
| **Authentification** | JWT (JSON Web Token) avec Spring Security |
| **Base de données** | MongoDB 7.x (principale); InfluxDB (optionnel pour capteurs IoT) |
| **API externe** | OpenWeatherMap API |
| **Conteneurisation** | Docker + Docker Compose |
| **Déploiement** | Render / Railway (Backend), Vercel / Netlify (Frontend), MongoDB Atlas |
| **Monitoring** | Spring Boot Actuator, Grafana (optionnel) |
| **Outils Dev** | Postman, Git/GitHub, VS Code, IntelliJ IDEA, MongoDB Compass, Maven, npm |

### 2.3 Architecture Microservices

L'application est composée des microservices suivants :

#### **1. User Service** (Service Utilisateur)
- **Responsabilité** : Gestion des utilisateurs et authentification
- **Endpoints** : `/api/auth/*`, `/api/users/*`
- **Base de données** : MongoDB - Collection `users`

#### **2. Farm Service** (Service Exploitation)
- **Responsabilité** : Gestion des exploitations agricoles
- **Endpoints** : `/api/farms/*`
- **Base de données** : MongoDB - Collection `farms`

#### **3. Weather Service** (Service Météo)
- **Responsabilité** : Intégration API météo et cache des données
- **Endpoints** : `/api/weather/*`
- **Intégration** : API OpenWeatherMap

#### **4. Irrigation Service** (Service Irrigation)
- **Responsabilité** : Planification et suivi de l'irrigation
- **Endpoints** : `/api/irrigation/*`
- **Base de données** : MongoDB - Collections `irrigation_plans`, `irrigation_records`

#### **5. Alert Service** (Service Alertes)
- **Responsabilité** : Génération et envoi d'alertes
- **Endpoints** : `/api/alerts/*`
- **Base de données** : MongoDB - Collection `alerts`

#### **6. IoT Service** (Optionnel - V2)
- **Responsabilité** : Réception données capteurs
- **Endpoints** : `/api/iot/*`
- **Base de données** : InfluxDB (time-series)

#### **7. Config Server**
- **Responsabilité** : Configuration centralisée de tous les services
- **Technologie** : Spring Cloud Config

#### **8. Eureka Server**
- **Responsabilité** : Service Discovery (registre des microservices)
- **Technologie** : Spring Cloud Netflix Eureka

#### **9. API Gateway**
- **Responsabilité** : Point d'entrée unique, routage, load balancing
- **Technologie** : Spring Cloud Gateway
- **Fonctionnalités** : Validation JWT, Rate Limiting, CORS, Logging centralisé

### 2.4 Communication Inter-Services

- **Mode** : REST synchrone avec Feign Client
- **Format** : JSON
- **Résilience** : Circuit Breaker (Resilience4j)

### 2.5 Sécurité

| Composant | Technologie | Description |
|---|---|---|
| **Authentification** | JWT (JSON Web Token) | Token stateless signé |
| **Hachage mot de passe** | BCrypt | Algorithme de hachage sécurisé |
| **Autorisation** | Spring Security + Annotations | Contrôle d'accès basé sur les rôles (RBAC) |
| **CORS** | Spring Security CORS Config | Configuration cross-origin |
| **HTTPS** | SSL/TLS | Chiffrement des communications (production) |
| **Validation** | Bean Validation API | Validation côté serveur |

### 2.6 Conteneurisation et Déploiement

**Docker** : Chaque microservice dispose de son propre Dockerfile

**Docker Compose** : Orchestration locale de tous les services (développement)

**Hébergement Production** :
- Backend : Render / Railway (tier gratuit)
- Frontend : Vercel / Netlify (tier gratuit)
- MongoDB : MongoDB Atlas (512 MB gratuit)
- InfluxDB : InfluxDB Cloud (tier gratuit)

### 2.7 Matériel IoT (Prototype Optionnel - V2)

| Matériel | Description | Prix (MAD) |
|---|---:|---:|
| ESP32 | Microcontrôleur WiFi | ~100 |
| Capteur humidité sol | Mesure l'humidité du sol | ~30 |
| DHT22 | Capteur température/humidité air | ~50 |
| Pompe à eau miniature | Test d'irrigation automatique | ~40 |
| Breadboard + câbles | Connexion des composants | ~40 |
| **Total prototype (estimé)** | | **~260 MAD** |

---

# Chapitre III : Planification et Organisation

## 1. Outils de Gestion de Projet

### 1.1 Planification JIRA

**Lien JIRA** : *(À communiquer)*

Le projet sera organisé en Sprints avec les tâches suivantes :
- User Stories par fonctionnalité
- Tâches techniques (backend, frontend, DevOps)
- Bugs et améliora tions
- Suivi de l'avancement avec Kanban Board

### 1.2 Modélisation UML

**Lien Lucidchart** : *(À communiquer)*

Diagrammes à créer :
- **Diagramme de cas d'utilisation** : Interactions utilisateurs-système
- **Diagramme de classes** : Modèle de données et relations
- **Diagrammes de séquence** : Flux d'authentification, irrigation, alertes
- **Diagramme de déploiement** : Architecture microservices

### 1.3 Code Source

**Lien GitHub** : *(À communiquer)*

Repository structuré :
- `/user-service`
- `/farm-service`
- `/weather-service`
- `/irrigation-service`
- `/alert-service`
- `/eureka-server`
- `/api-gateway`
- `/frontend-angular`
- `/docker-compose.yml`
- `/docs` - Documentation

### 1.4 Présentation Visuelle

**Lien Canva** : *(À communiquer)*

Présentation incluant :
- Pitch du projet
- Problématique et solution
- Démonstration de l'application
- Architecture technique
- Impact et perspectives

---

## 2. Maquettes et Interface Utilisateur

Les maquettes seront conçues avec **Figma** ou **Canva** et incluront :

- **Page d'accueil** : Présentation du SaaS et ses bénéfices
- **Page d'inscription/connexion** : Formulaires d'authentification
- **Tableau de bord agriculteur** : Vue synthétique (météo, consommation, alertes)
- **Page gestion des exploitations** : Liste et CRUD des fermes
- **Page historique d'irrigation** : Graphiques et statistiques
- **Page alertes et notifications** : Centre de notification
- **Section subventions** : Informations et conseils
- **Interface admin** : Gestion des utilisateurs et statistiques globales

**Principes UI/UX** :
- Design moderne et épuré
- Interface responsive (mobile-first)
- Navigation intuitive
- Accessibilité (contraste, taille texte)
- Palette de couleurs : Bleu (eau), Vert (agriculture), Blanc

---

## 3. Plan de Réalisation

| Phase | Description | Durée | Livrables |
|---|---|---|---|
| **Phase 1** | Étude et conception UML + cahier des charges | 1 semaine | Cahier des charges, Diagrammes UML, Maquettes |
| **Phase 2** | Développement backend (Spring Boot + MongoDB) | 2 semaines | Microservices opérationnels, API REST documentée |
| **Phase 3** | Développement frontend (Angular) | 2 semaines | Interface web fonctionnelle, Intégration API |
| **Phase 4** | Intégration API météo et système d'alertes | 1 semaine | Météo temps réel, Alertes automatiques |
| **Phase 5** | Tests, Dockerisation et documentation | 1 semaine | Tests unitaires/intégration, Docker Compose, Docs |
| **Phase 6** (optionnelle) | Module IoT et InfluxDB | 1 semaine | Prototype IoT fonctionnel, Dashboard Grafana |
| **Total estimé** | | **6 à 8 semaines** | Application complète et déployée |

---

## 4. Livrables Finaux

### 4.1 Livrables Techniques

- ✅ **Cahier des charges** (ce document)
- ✅ **Diagrammes UML** : Cas d'utilisation, classes, séquence, déploiement
- ✅ **Code source** : Repository GitHub complet et documenté
- ✅ **Base de données** : Schémas MongoDB exportés
- ✅ **API Documentation** : Swagger/OpenAPI accessible
- ✅ **Docker Compose** : Configuration pour déploiement local
- ✅ **Application déployée** : URL production (backend + frontend)

### 4.2 Livrables Documentaires

- ✅ **Documentation technique** : Installation, configuration, architecture
- ✅ **Documentation utilisateur** : Guide d'utilisation de la plateforme
- ✅ **Rapport final** : Bilan du projet, difficultés rencontrées, perspectives
- ✅ **Présentation (Canva)** : Pitch de 10-15 minutes
- ✅ **Vidéo démo** (optionnel) : Screencast de l'application

---

## 5. Impact Attendu

### 5.1 Impact Environnemental

- 🌍 **Réduction de 30-40%** de la consommation d'eau agricole
- 🌍 **Préservation des nappes phréatiques** et des ressources en eau
- 🌍 **Diminution de l'empreinte carbone** (moins de pompage)
- 🌍 **Amélioration de la qualité des sols** (pas de sur-irrigation)

### 5.2 Impact Économique

- 💰 **Économies sur factures d'eau et d'énergie** pour les agriculteurs
- 💰 **Amélioration des rendements agricoles** de 15-20%
- 💰 **Accès facilité aux subventions** et programmes de soutien
- 💰 **Création de valeur** dans le secteur AgriTech marocain

### 5.3 Impact Social

- 👥 **Sensibilisation à l'agriculture durable**
- 👥 **Formation et accompagnement** des agriculteurs au numérique
- 👥 **Autonomie des petits exploitants** dans la gestion de l'eau
- 👥 **Renforcement des coopératives** par des outils collaboratifs

### 5.4 Impact Technique et Pédagogique

- 🎓 **Valorisation des compétences** en développement Full Stack
- 🎓 **Maîtrise de l'architecture microservices**
- 🎓 **Application concrète** des technologies modernes (Spring Cloud, Angular, Docker)
- 🎓 **Contribution au secteur AgriTech** émergent au Maroc
- 🎓 **Potentiel de déploiement réel** auprès de coopératives partenaires

---

## 6. Perspectives et Évolutions Futures

### Version 2.0 (Post-MVP)

- 🚀 **Application mobile native** (Android/iOS)
- 🚀 **Intégration IoT complète** avec automatisation de l'irrigation
- 🚀 **Intelligence Artificielle** : Prédictions basées sur Machine Learning
- 🚀 **Notifications SMS** et push mobiles
- 🚀 **Marketplace** de matériel agricole et capteurs
- 🚀 **Réseau social** pour échange de bonnes pratiques
- 🚀 **Partenariats institutionnels** (Ministère Agriculture, ORMVA)

### Version 3.0 (Vision long terme)

- 🔮 **Expansion régionale** (MENA, Afrique de l'Ouest)
- 🔮 **Gestion complète de l'exploitation** (production, finance, vente)
- 🔮 **Certification agriculture durable** (bio, commerce équitable)
- 🔮 **Blockchain** pour traçabilité de l'eau et des cultures
- 🔮 **API publique** pour chercheurs et développeurs

---

<p align="center">
  <strong>Fin du Cahier des Charges</strong><br/>
  <em>AquaSmart - Optimiser l'eau, Cultiver l'avenir</em><br/>
  <strong>© 2025 - YouCode Safi</strong>
</p>
