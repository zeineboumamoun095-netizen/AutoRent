# Étude comparative des hébergeurs gratuits
## Projet: AutoRent (Spring Boot + MySQL)

---

## Tableau comparatif

| Critère                        | Render              | Railway              | Vercel              | Netlify             |
|-------------------------------|---------------------|----------------------|---------------------|---------------------|
| **Facilité d'utilisation**    | ⭐⭐⭐⭐⭐ Très simple | ⭐⭐⭐⭐ Simple         | ⭐⭐⭐⭐⭐ Très simple | ⭐⭐⭐⭐⭐ Très simple |
| **Intégration GitHub**        | ✅ Native, auto-deploy | ✅ Native            | ✅ Native           | ✅ Native           |
| **Support Spring Boot**       | ✅ Docker + Dockerfile | ✅ Dockerfile        | ❌ Frontend only    | ❌ Frontend only    |
| **Base de données gratuite**  | ✅ PostgreSQL 90j    | ✅ MySQL/PostgreSQL   | ❌ Aucune           | ❌ Aucune           |
| **Limite plan gratuit**       | 750h/mois, sleep 15min | 500h/mois, $5 crédit | Fonctions serverless seulement | Sites statiques seulement |
| **Variables d'environnement** | ✅ Interface web     | ✅ Interface web      | ✅ Interface web    | ✅ Interface web    |
| **Logs en temps réel**        | ✅ Oui              | ✅ Oui               | ✅ Oui              | ✅ Oui              |
| **Domaine gratuit**           | ✅ .onrender.com    | ✅ .railway.app      | ✅ .vercel.app      | ✅ .netlify.app     |
| **Support Docker**            | ✅ Complet          | ✅ Complet           | ❌ Non              | ❌ Non              |

---

## Analyse détaillée

### 1. Render
**Points forts:**
- Détecte automatiquement le Dockerfile
- Déploiement automatique à chaque push sur `main`
- Base de données PostgreSQL gratuite pendant 90 jours
- Interface très claire et intuitive
- Logs en temps réel accessibles

**Points faibles:**
- L'application "s'endort" après 15 min d'inactivité (plan gratuit)
- Base de données expire après 90 jours gratuitement
- Redémarrage lent (~30s) après inactivité

---

### 2. Railway
**Points forts:**
- Support MySQL natif (compatible avec notre projet)
- Déploiement très rapide
- Pas de "sleep" automatique
- $5 de crédit gratuit par mois

**Points faibles:**
- Crédit limité à $5/mois (peut être insuffisant)
- Moins documenté que Render
- Nécessite une carte bancaire pour certaines fonctionnalités

---

### 3. Vercel
**Points forts:**
- Excellent pour les applications React/Next.js
- Très rapide pour les sites statiques

**Points faibles:**
- ❌ Ne supporte pas Spring Boot (backend Java)
- ❌ Pas de base de données intégrée
- Conçu uniquement pour le frontend

---

### 4. Netlify
**Points forts:**
- Idéal pour les sites statiques et JAMstack
- Très simple d'utilisation

**Points faibles:**
- ❌ Ne supporte pas Spring Boot
- ❌ Pas de base de données
- Limité aux applications frontend

---

## Justification du choix: Render

**Nous choisissons Render** pour les raisons suivantes:

1. **Support complet de Spring Boot** via Docker
2. **Déploiement automatique** depuis la branche `main` de GitHub
3. **Gratuit** sans carte bancaire requise
4. **Variables d'environnement** pour sécuriser DB_URL, DB_USERNAME, DB_PASSWORD
5. **Logs accessibles** pour déboguer en production
6. **Interface simple** — configuration en quelques clics

> Vercel et Netlify sont éliminés car ils ne supportent pas Java/Spring Boot.
> Railway est une bonne alternative mais le crédit limité peut poser problème.

---

## Guide de déploiement sur Render

### Prérequis
- Compte GitHub avec le projet poussé
- Compte Render (gratuit sur render.com)
- Dockerfile présent à la racine du projet

### Étapes

1. **Créer une base de données** sur Render:
    - New → PostgreSQL (ou utiliser Aiven/Railway pour MySQL)
    - Copier l'Internal Database URL

2. **Créer le Web Service**:
    - New → Web Service
    - Connecter le dépôt GitHub
    - Runtime: Docker
    - Branch: `main`

3. **Variables d'environnement**:
   ```
   DB_URL        = jdbc:mysql://...
   DB_USERNAME   = votre_username
   DB_PASSWORD   = votre_password
   SPRING_PROFILES_ACTIVE = prod
   ```

4. **Auto-deploy**: activé automatiquement sur chaque push vers `main`