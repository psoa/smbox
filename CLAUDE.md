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
- Thymeleaf templates in `src/main/resources/templates/` use a shared `layout.html` fragment (fixed left sidebar + main content + footer).
- `CategoryController`/`CategoryRepository`/`Category` follow the same pattern as `Post`, at `/categories`. Posts and categories are many-to-many (`post_category` join table, owning side on `Post`).
- `GlobalModelAttributes` (`@ControllerAdvice`) injects `sidebarCategories` into every page's model so the sidebar can render on all routes.

**Known type inconsistency**: `PostRepository` extends `JpaRepository<Post, Integer>` but controller uses `Long` IDs. Spring's coercion makes it work, but the generic should ideally be `Long`.

## UI conventions

- Tailwind CSS (Play CDN, `cdn.tailwindcss.com`) + FontAwesome 6.0.0 + Google Fonts (Inter, Merriweather) — all via CDN, no build step.
- `tailwind.config` (inline script in `layout.html`) maps `font-sans` → Inter, `font-serif` → Merriweather.
- Layout is a fixed-width (`w-64`) left sidebar (categories + "Add New" links) with `md:pl-64` main content, max-width `3xl`, centered. Sidebar stacks above content below the `md` breakpoint.
- No top navbar — all navigation lives in the sidebar.
- Accent color is Tailwind `indigo-600`; success actions (publish/save) use `emerald-600`; destructive actions use `red-600` outline style.

## Patterns

- New post `date` auto-set to today (`SimpleDateFormat("yyyy-MM-dd")`); `subject` defaults to `"Brainstorm dd/MM/yyyy"`.
- Save/update both use `postRepository.save(post)` — JPA handles insert vs update via presence of `id`.
- No existing tests; test path is `src/test/java/br/com/psoa/smbox/`.
