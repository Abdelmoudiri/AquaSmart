# AquaSmart

**AquaSmart** est une plateforme SaaS d'irrigation intelligente qui aide les agriculteurs à optimiser la consommation d'eau. Basée sur les données météo, du sol et des capteurs IoT, elle propose un plan d'arrosage automatisé, un suivi en temps réel et des alertes personnalisées, favorisant une agriculture durable et économe en ressources.

---

## Architecture

L'application est construite en **microservices** (Spring Boot) orchestrés par **Docker Compose**, avec un **frontend Angular 19** et une passerelle **Spring Cloud Gateway**.

```
                        ┌─────────────────┐
                        │   Frontend       │  :4200
                        │   (Angular 19)   │
                        └────────┬─────────┘
                                 │
                        ┌────────▼─────────┐
                        │   API Gateway    │  :8080
                        │ (Spring Cloud)   │
                        └────────┬─────────┘
                                 │
          ┌──────────────────────┼──────────────────────┐
          │              ┌───────┴──────┐               │
   ┌──────▼──────┐ ┌─────▼──────┐ ┌────▼──────┐ ┌──────▼──────┐ ┌──────▼──────┐
   │user-service │ │farm-service│ │  weather  │ │ irrigation  │ │   alert     │
   │   :8085     │ │   :8082    │ │  :8083    │ │   :8086     │ │   :8087     │
   └─────────────┘ └────────────┘ └───────────┘ └─────────────┘ └─────────────┘
          │                │                              │
   ┌──────▼────────────────▼──────────────────────────────▼──────┐
   │                     MySQL 8.0  :3308                         │
   └──────────────────────────────────────────────────────────────┘
                              ┌──────────────┐
                              │  Redis 7     │  :6379  (cache météo)
                              └──────────────┘
                       ┌──────────────────────┐
                       │  Eureka Discovery    │  :8761
                       └──────────────────────┘
```

---

## Services

| Service | Port | Base de données | Description |
|---|---|---|---|
| `frontend` | 4200 | — | Interface Angular 19 |
| `gateway` | 8080 | — | Spring Cloud Gateway + CORS |
| `discovery-service` | 8761 | — | Eureka Server |
| `user-service` | 8085 | `aquasmart_users` | Auth JWT, refresh token, reset password |
| `farm-service` | 8082 | `aquasmart_farms` | Gestion fermes, parcelles, cultures |
| `weather-service` | 8083 | — (Redis) | Météo OpenWeatherMap + cache |
| `irrigation-service` | 8086 | `aquasmart_irrigation` | Planification et recommandations |
| `alert-service` | 8087 | `aquasmart_alerts` | Alertes et notifications |

---

## Lancer le projet

### Prérequis

- [Docker](https://www.docker.com/) & Docker Compose
- Clé API [OpenWeatherMap](https://openweathermap.org/api)

### Configuration

Créer un fichier `.env` à la racine de `AquaSmart-App/` :

```env
OPENWEATHERMAP_API_KEY=votre_cle_api_ici
MAIL_USERNAME=votre_email@example.com
MAIL_PASSWORD=votre_mot_de_passe
```

### Démarrage

```bash
cd AquaSmart-App
docker compose up --build
```

L'application est accessible sur **http://localhost:4200**

---

## API Routes (via Gateway :8080)

| Méthode | Route | Service |
|---|---|---|
| POST | `/api/auth/register` | user-service |
| POST | `/api/auth/login` | user-service |
| POST | `/api/auth/refresh` | user-service |
| POST | `/api/auth/forgot-password` | user-service |
| GET/POST | `/api/farms/**` | farm-service |
| GET/POST | `/api/parcels/**` | farm-service |
| GET/POST | `/api/crops/**` | farm-service |
| GET | `/api/weather/**` | weather-service |
| GET/POST | `/api/irrigation/**` | weather-service |
| GET/POST | `/api/alerts/**` | alert-service |

---

## Technologies

**Backend**
- Java 17 + Spring Boot 3
- Spring Cloud Gateway + Eureka (Netflix OSS)
- Spring Security + JWT
- Spring Data JPA + MySQL 8
- OpenFeign (communication inter-services)
- Redis (cache météo)
- MapStruct + Lombok

**Frontend**
- Angular 19 (standalone components)
- TypeScript
- Nginx (production)

**Infrastructure**
- Docker & Docker Compose
- Multi-stage Dockerfiles

---

## Structure du projet

```
AquaSmart-App/
├── docker-compose.yml
├── frontend/           # Angular 19
├── gateway/            # Spring Cloud Gateway
├── discovery-service/  # Eureka Server
├── user-service/       # Auth + utilisateurs
├── farm-service/       # Fermes, parcelles, cultures
├── weather-service/    # Météo + recommandations
├── irrigation-service/ # Irrigation intelligente
└── alert-service/      # Alertes et notifications
```

