# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

**Smbox** is a personal brainstorming notes web app — Spring Boot 3.3.5 + Thymeleaf + SQLite. Single-module Gradle project (Java 21).

## Commands

```bash
# Development (hot reload via spring-boot-devtools)
./gradlew bootRun

# Run tests
./gradlew test

# Deploy: builds jar, copies to $HOME/apps/smbox/, stops running instance and restarts
./gradlew deploy

# Manual start/restart (from $HOME/apps/smbox/) — writes PID to smbox.pid
./run.sh
```

## First-time setup

The SQLite DB file must exist before the app starts:
```bash
mkdir -p $HOME/smbox/data
touch $HOME/smbox/data/smbox.db
```

DB URL is hardcoded in `application.properties` as `jdbc:sqlite:${HOME}/smbox/data/smbox.db`.

## Architecture

**MVC (Spring Boot + Thymeleaf)**

- `PostController` — all HTTP routes (`/posts`, `/add`, `/{id}`, `/edit/{id}`, `/delete/{id}`). Search is via `?search=` and filtered **in memory** (Java Streams on full result set), not via SQL.
- `Post` (JPA entity) — fields: `id` (Long, auto), `date` (String `yyyy-MM-dd`), `subject`, `content` (TEXT).
- `PostRepository` — `@RepositoryRestResource` auto-exposes REST API at `/api/` with HAL links.
- Thymeleaf templates in `src/main/resources/templates/` use a shared `layout.html` fragment for navbar/footer/CSS.

**Known type inconsistency**: `PostRepository` extends `JpaRepository<Post, Integer>` but controller uses `Long` IDs. Spring's coercion makes it work, but the generic should ideally be `Long`.

## UI conventions

- Bootstrap 5.3.0 + FontAwesome 6.0.0 + Google Fonts (Inter, Merriweather) — all via CDN.
- CSS variables defined in `layout.html` `<style>` block (`--primary-color`, `--accent-color`, etc.).
- Fixed navbar (70px), fixed footer (60px), max-width 740px reading layout.
- Responsive breakpoint at 768px.

## Patterns

- New post `date` auto-set to today (`SimpleDateFormat("yyyy-MM-dd")`); `subject` defaults to `"Brainstorm dd/MM/yyyy"`.
- Save/update both use `postRepository.save(post)` — JPA handles insert vs update via presence of `id`.
- No existing tests; test path is `src/test/java/br/com/psoa/smbox/`.
