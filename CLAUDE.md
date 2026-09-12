# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

**Smbox** is a personal brainstorming notes **desktop** app — Spring Boot 3.5.16 + Thymeleaf + SQLite, shown in a JavaFX WebView. Single-module Gradle project (Java 25). Runs on Linux and Windows.

## Commands

```bash
# Development (opens the JavaFX window; hot reload via spring-boot-devtools)
./gradlew bootRun

# Run tests
./gradlew test

# Native installers (jpackage cannot cross-compile)
./gradlew jpackageLinux     # .deb — run on Linux
./gradlew jpackageWindows   # .msi — run on Windows
```

Entry point is `SmboxLauncher` (must not extend `javafx.application.Application`, or `bootRun` fails with "JavaFX runtime components are missing").

## First-time setup

None. On first launch the app creates `${user.home}/smbox/data/` and the SQLite file, and Hibernate `ddl-auto=update` creates tables. Logs go to `${user.home}/smbox/smbox.log`. A second instance is refused via `${user.home}/smbox/smbox.lock`.

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

- Tailwind CSS is built locally (`./gradlew buildTailwindCss` / `processResources`) into `src/main/resources/static/css/app.css`. FontAwesome 6.0.0 and Inter/Merriweather are self-hosted under `src/main/resources/static/`.
- `tailwind.config.js` maps `font-sans` → Inter, `font-serif` → Merriweather.
- Layout is a fixed-width (`w-64`) left sidebar (categories + "Add New" links) with `md:pl-64` main content, max-width `3xl`, centered. Sidebar stacks above content below the `md` breakpoint.
- No top navbar — all navigation lives in the sidebar.
- Accent color is Tailwind `indigo-600`; success actions (publish/save) use `emerald-600`; destructive actions use `red-600` outline style.

## Patterns

- New post `date` auto-set to today (`SimpleDateFormat("yyyy-MM-dd")`); `subject` defaults to `"Brainstorm dd/MM/yyyy"`.
- Save/update both use `postRepository.save(post)` — JPA handles insert vs update via presence of `id`.
- No existing tests; test path is `src/test/java/br/com/psoa/smbox/`.
