# SMU Scientific Hub

This portal aims to provide an open platform that supports and facilitates collaborations between SMU and local industry. Industry can publish a challenge, and our platform will recommend SMU researcher(s), based on AI, Machine Learning, Deep Learning, Natural Language Processing, Knowledge Graph, and Data Mining techniques.

## Project Overview
This is a frontend-backend separated project, with each running on different ports via Docker Compose.

- Frontend Port: `9036`
- Backend Port: `9037`

### How to Run the Project
This repo is intended to be run via **Docker Compose only** (no `sbt` commands).

#### Prerequisites
- **Docker Desktop** (or a compatible Docker Engine that supports `docker compose`)
- **MySQL running on your host machine** (not in Compose) and reachable as `host.docker.internal:3306`
  - Database: `cshub`
  - Username: `root`
  - Password: `Pass4me21$`

  > Use your own local MySQL credentials. Update `docker-compose.yml` to match.

> Note: The included `docker-compose.yml` is configured to connect the backend container to a MySQL instance on your host using `host.docker.internal`. If your DB credentials/host differ, update them in `docker-compose.yml`.

#### One-time setup: you must provide your own MySQL data
This repo **does not** automatically provision or populate your MySQL database when running via Docker Compose. In `docker-compose.yml`, the backend is started with database evolutions disabled, so the DB must already exist and contain the required tables/data.

- **Create/initialize the schema**: use `ddl.sql` (repo root) or the SQL under `backend/conf/evolutions/default/` as your starting point (depending on what your course staff expects).
- **Seed any required application data**: if your environment needs initial rows (users, orgs, projects, etc.), you must import/provide that data in your own MySQL instance.

#### One-time setup: you must provide/update your own GitHub-related data
Any **GitHub-related information shown in the app** (e.g., user GitHub usernames, profile links, repository URLs associated with projects) is **application data** stored in your MySQL database.

- **There is no auto-sync from GitHub** in this repo (no configured GitHub API credentials are included), so each user/team must **enter/seed/update** the GitHub-related fields in their own MySQL data as appropriate for their deployment.

#### Start the app
From the repo root:

```bash
docker compose up -d
```

#### View logs

```bash
docker compose logs -f backend frontend
```

#### Stop the app

```bash
docker compose down
```

#### Access the app
- Frontend (UI): `http://localhost:9036`
- Backend (API): `http://localhost:9037`

#### Important note about Docker images
`docker-compose.yml` references these images:
- `cshub-backend:latest`
- `cshub-frontend:latest`

Docker Compose will try to use these images from your local Docker cache (or pull them if available from a registry). If you do not have these images available, ask your instructor/maintainers for the expected images (or an updated Compose file that includes `build:` sections).

## Contribution Guidelines
For contributors, please follow these guidelines when making changes to the code:

- Create a new branch named after yourself.
- Make modifications in your own branch.
- Submit your changes via a pull request for review and merging.

By following this process, we ensure that all code changes are tracked and reviewed properly before being merged into the main project.
# CSHub-Fall2025-BRK
