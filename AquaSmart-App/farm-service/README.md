# AquaSmart Farm Service

## Description
Service de gestion des fermes aquaponiques pour la plateforme AquaSmart.

## Fonctionnalités
- Gestion CRUD des fermes
- Gestion des bassins (poissons, plantes, biofiltre)
- Suivi du statut des fermes et bassins
- Recherche et filtrage

## Prérequis
- Java 17+
- Maven 3.9+
- MySQL 8+

## Configuration

### Variables d'environnement
| Variable | Description | Défaut |
|----------|-------------|--------|
| `PORT` | Port du service | `8082` |
| `DB_URL` | URL de la base de données | `jdbc:mysql://localhost:3306/aquasmart_farms` |
| `DB_USERNAME` | Utilisateur DB | `root` |
| `DB_PASSWORD` | Mot de passe DB | `root` |
| `EUREKA_ENABLED` | Activer Eureka | `false` |
| `EUREKA_SERVER_URL` | URL du serveur Eureka | `http://localhost:8761/eureka/` |

## Démarrage

### Développement local
```bash
mvn spring-boot:run
```

### Avec Docker
```bash
docker build -t aquasmart/farm-service .
docker run -p 8082:8082 aquasmart/farm-service
```

## API Endpoints

### Fermes
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `POST` | `/api/farms` | Créer une ferme |
| `GET` | `/api/farms` | Lister toutes les fermes |
| `GET` | `/api/farms/{id}` | Récupérer une ferme |
| `GET` | `/api/farms/{id}/details` | Ferme avec bassins |
| `GET` | `/api/farms/my-farms` | Mes fermes |
| `GET` | `/api/farms/status/{status}` | Fermes par statut |
| `GET` | `/api/farms/search?keyword=` | Rechercher |
| `PUT` | `/api/farms/{id}` | Mettre à jour |
| `PATCH` | `/api/farms/{id}/status` | Changer le statut |
| `DELETE` | `/api/farms/{id}` | Supprimer |

### Bassins
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `POST` | `/api/farms/{farmId}/basins` | Créer un bassin |
| `GET` | `/api/farms/{farmId}/basins` | Lister les bassins |
| `GET` | `/api/farms/{farmId}/basins/{id}` | Récupérer un bassin |
| `GET` | `/api/farms/{farmId}/basins/type/{type}` | Bassins par type |
| `GET` | `/api/farms/{farmId}/basins/status/{status}` | Bassins par statut |
| `PUT` | `/api/farms/{farmId}/basins/{id}` | Mettre à jour |
| `PATCH` | `/api/farms/{farmId}/basins/{id}/status` | Changer le statut |
| `PATCH` | `/api/farms/{farmId}/basins/{id}/volume` | Mettre à jour le volume |
| `DELETE` | `/api/farms/{farmId}/basins/{id}` | Supprimer |

## Modèles

### Types de ferme
- `MEDIA_BASED` - Système à base de média
- `NFT` - Nutrient Film Technique
- `DWC` - Deep Water Culture
- `VERTICAL` - Système vertical
- `HYBRID` - Système hybride

### Types de bassin
- `FISH_TANK` - Bassin à poissons
- `GROW_BED` - Lit de culture
- `SUMP_TANK` - Réservoir de décantation
- `BIOFILTER` - Biofiltre
- `NURSERY` - Nurserie

### Statuts
**Ferme:** `ACTIVE`, `INACTIVE`, `MAINTENANCE`, `UNDER_CONSTRUCTION`

**Bassin:** `OPERATIONAL`, `EMPTY`, `MAINTENANCE`, `CYCLING`, `QUARANTINE`
