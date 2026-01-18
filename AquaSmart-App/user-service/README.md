# User Service - AquaSmart

Service de gestion des utilisateurs et d'authentification JWT pour la plateforme AquaSmart.

## 📋 Fonctionnalités

- ✅ Inscription et connexion avec JWT
- ✅ Gestion des rôles (ADMIN, AGRICULTEUR, ONG)
- ✅ CRUD complet des utilisateurs
- ✅ Sécurité avec Spring Security
- ✅ MongoDB pour la persistance
- ✅ Validation des données
- ✅ Gestion globale des exceptions

## 🛠️ Technologies

- **Framework**: Spring Boot 3.2.0
- **Sécurité**: Spring Security + JWT
- **Base de données**: MongoDB
- **Cloud**: Spring Cloud (Eureka, Config)
- **Validation**: Jakarta Validation
- **Mapping**: MapStruct
- **Logging**: SLF4J + Lombok

## 🚀 Démarrage rapide

### Prérequis
- Java 17+
- Maven 3.9+
- MongoDB (local ou Docker)

### Installation

1. **Démarrer MongoDB** (avec Docker):
```bash
docker run -d -p 27017:27017 --name mongodb mongo:latest
```

2. **Compiler le projet**:
```bash
cd user-service
mvn clean install
```

3. **Lancer l'application**:
```bash
mvn spring-boot:run
```

Le service sera disponible sur `http://localhost:8081`

### Avec Docker

```bash
docker build -t aquasmart-user-service .
docker run -p 8081:8081 \
  -e MONGODB_URI=mongodb://host.docker.internal:27017/aquasmart_users \
  aquasmart-user-service
```

## 📡 API Endpoints

### Authentification (Public)

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/api/users/auth/register` | Inscription |
| POST | `/api/users/auth/login` | Connexion |
| GET | `/api/users/auth/health` | Health check |

### Utilisateurs (Authentifié)

| Méthode | Endpoint | Rôle requis | Description |
|---------|----------|-------------|-------------|
| GET | `/api/users` | ADMIN | Liste tous les utilisateurs |
| GET | `/api/users/{id}` | ALL | Récupère un utilisateur |
| GET | `/api/users/email/{email}` | ALL | Récupère par email |
| GET | `/api/users/role/{role}` | ADMIN | Filtre par rôle |
| GET | `/api/users/active` | ADMIN | Utilisateurs actifs |
| GET | `/api/users/region/{region}` | ADMIN, ONG | Filtre par région |
| PUT | `/api/users/{id}` | ALL | Met à jour un utilisateur |
| PATCH | `/api/users/{id}/toggle-status` | ADMIN | Active/désactive |
| POST | `/api/users/{id}/roles/{role}` | ADMIN | Ajoute un rôle |
| DELETE | `/api/users/{id}/roles/{role}` | ADMIN | Supprime un rôle |
| DELETE | `/api/users/{id}` | ADMIN | Supprime un utilisateur |

## 📝 Exemples de requêtes

### Inscription

```bash
curl -X POST http://localhost:8081/api/users/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "farmer@aquasmart.com",
    "password": "password123",
    "firstName": "Ahmed",
    "lastName": "Benali",
    "phoneNumber": "+212600000000",
    "roles": ["AGRICULTEUR"],
    "city": "Safi",
    "region": "Marrakech-Safi"
  }'
```

### Connexion

```bash
curl -X POST http://localhost:8081/api/users/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "farmer@aquasmart.com",
    "password": "password123"
  }'
```

Réponse:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "userId": "65a1b2c3d4e5f6g7h8i9j0k1",
  "email": "farmer@aquasmart.com",
  "firstName": "Ahmed",
  "lastName": "Benali",
  "roles": ["AGRICULTEUR"],
  "expiresIn": 86400000
}
```

### Récupérer un utilisateur (avec JWT)

```bash
curl -X GET http://localhost:8081/api/users/65a1b2c3d4e5f6g7h8i9j0k1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## ⚙️ Configuration

Variables d'environnement disponibles:

| Variable | Description | Défaut |
|----------|-------------|--------|
| `PORT` | Port du service | 8081 |
| `MONGODB_URI` | URI MongoDB | `mongodb://localhost:27017/aquasmart_users` |
| `JWT_SECRET` | Secret pour JWT | (voir application.yml) |
| `JWT_EXPIRATION` | Durée du token (ms) | 86400000 (24h) |
| `EUREKA_ENABLED` | Activer Eureka | false |
| `EUREKA_SERVER_URL` | URL Eureka | `http://localhost:8761/eureka/` |

## 🔐 Sécurité

- **Authentification**: JWT (JSON Web Token)
- **Hashage**: BCrypt pour les mots de passe
- **CORS**: Configuré pour Angular (port 4200)
- **Validation**: Jakarta Validation sur tous les endpoints

## 🏗️ Structure du projet

```
user-service/
├── src/main/java/com/aquasmart/userservice/
│   ├── controller/          # Controllers REST
│   ├── service/             # Logique métier
│   ├── repository/          # Repositories MongoDB
│   ├── model/               # Entités et enums
│   ├── dto/                 # DTOs et mappers
│   ├── security/            # Configuration sécurité
│   │   └── jwt/             # Utilitaires JWT
│   ├── exception/           # Gestion des exceptions
│   └── UserServiceApplication.java
├── src/main/resources/
│   └── application.yml      # Configuration
├── Dockerfile
└── pom.xml
```

## 📊 Rôles utilisateur

| Rôle | Description |
|------|-------------|
| **ADMIN** | Administrateur plateforme - accès complet |
| **AGRICULTEUR** | Exploitant agricole - gestion de ses fermes |
| **ONG** | Organisation - suivi des agriculteurs |

## 🧪 Tests

```bash
# Lancer les tests
mvn test

# Avec couverture
mvn clean test jacoco:report
```

## 📦 Build & Déploiement

```bash
# Build JAR
mvn clean package

# Build Docker image
docker build -t aquasmart/user-service:1.0.0 .

# Run with Docker Compose
docker-compose up -d
```

## 🐛 Gestion des erreurs

Le service retourne des réponses d'erreur structurées:

```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 409,
  "error": "Email Already Exists",
  "message": "Un compte avec cet email existe déjà",
  "path": "/api/users/auth/register"
}
```

Codes HTTP:
- `200` - Succès
- `201` - Créé
- `400` - Validation échouée
- `401` - Non authentifié
- `403` - Accès refusé
- `404` - Non trouvé
- `409` - Conflit (email existe)
- `500` - Erreur serveur

## 📚 Documentation

Pour plus d'informations, consultez:
- [Cahier des charges](../../AquaSmart-Docs/Cahier_des_Charges.md)
- [README principal](../../README.md)

## 👨‍💻 Auteur

**Abdeljabbar MOUDIRI** - YouCode Safi

---

**AquaSmart** - Optimisation de l'eau pour une agriculture durable 🌾💧
