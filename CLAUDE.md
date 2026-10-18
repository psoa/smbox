# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

**Smbox** is a personal brainstorming notes **desktop** app: Spring Boot 3.5.16 + Thymeleaf + SQLite, shown in a JavaFX WebView. Single-module Gradle project (Java 25 toolchain, OpenJFX 25). Runs on Linux and Windows. `AGENTS.md` has a longer per-class reference.

## Commands

```bash
./gradlew bootRun                                   # opens the JavaFX window (devtools hot reload)
./gradlew test                                      # JUnit 5
./gradlew test --tests br.com.psoa.smbox.SmboxPathsTest   # single test class (append .methodName for one method)
./gradlew buildTailwindCss                          # rebuild static/css/app.css (also runs on processResources)
./gradlew exportJar [-PexportDir=<path>]            # bootJar copied to ~/app/smbox/smbox.jar by default
./gradlew jpackageLinux                             # .deb → build/jpackage/ (Linux only)
./gradlew jpackageWindows                           # .msi → build/jpackage/ (Windows only; no cross-compiling)
```

The first build downloads the Tailwind CLI (v3.4.19) from GitHub into `build/tailwindcli/`, so it needs network access.

## Desktop shell (how startup works)

1. `SmboxLauncher.main` is the JVM entry point (`springBoot.mainClass`). It **must not** extend `javafx.application.Application`, or `bootRun` fails with "JavaFX runtime components are missing". It creates `~/smbox/data/`, then takes an exclusive file lock (`SingleInstance`, `~/smbox/smbox.lock`). If the lock is already held, it launches `AlreadyRunningApp` (a dialog) and exits.
2. `SmboxApplication` is both the `@SpringBootApplication` and the JavaFX `Application`. `init()` starts Spring on `127.0.0.1` with `server.port=0` (ephemeral) and overrides `spring.datasource.url` with `SmboxPaths.jdbcUrl()`, which uses forward slashes so Windows paths work. An `ApplicationReadyEvent` listener completes a future with the real port, and `start()` loads `http://127.0.0.1:<port>/` into a `WebView`.
3. `WindowPrefs` saves and restores the window size and position in `~/smbox/window.properties`. `stop()` closes the Spring context.

`SmboxPaths` is the single source of truth for `~/smbox/...` locations: the DB at `data/smbox.db`, plus the lock file, log file and window prefs. The tests override `user.home` with a `@TempDir`, so follow that pattern for anything touching these paths.

JavaFX is bundled as ordinary classpath jars (classifier chosen from the **build host's** OS and arch), not via the module path or jlink, so Spring Boot's `JarLauncher` works unchanged. jpackage uses the toolchain JDK as `--runtime-image` because Temurin 25 ships without `jmods/`.

## Web app (MVC)

- `PostController`: `/` redirects to `/posts`. Also handles `/add`, `/{id}`, `/edit/{id}`, `/delete/{id}`. The edit form POSTs to `/edit` (the id travels in the model attribute). Search (`?search=`) and category filtering (`?category=`) run **in memory** with Java Streams over every post, sorted by date descending, not in SQL.
- `CategoryController` follows the same pattern under `/categories/...`. Posts and categories are many-to-many through the `post_category` join table. The owning side is `Post.categories`; `Category.posts` is `mappedBy` + `@JsonIgnore`. Forms send the checked categories as `categoryIds`, and the controller resolves them with `categoryRepository.findAllById`.
- `GlobalModelAttributes` (`@ControllerAdvice`) adds `sidebarCategories` to every model, because `layout.html` renders the sidebar on every page.
- Spring Data REST: `PostRepository` is exposed at `/api`, and `CategoryRepository` at `/categories`, which is the same path as the MVC controller.
- Save and update both call `repository.save(...)`. JPA decides between insert and update based on whether `id` is set.
- Known wart: `PostRepository extends JpaRepository<Post, Integer>` while the IDs are `Long`. It works through overloads and coercion, but the generic should be `Long`.

### Persistence

- `Post.date` is a `String` in `yyyy-MM-dd` (search matches against this string). A new post defaults to today's date and the subject `"Brainstorm dd/MM/yyyy"`.
- There are no migrations. The schema is managed by `spring.jpa.hibernate.ddl-auto=update`, which must stay set: Spring Boot treats SQLite as a non-embedded DB and defaults to `none`, so a fresh DB would get no tables. `update` only adds tables and columns. Renames and drops need manual SQL against `~/smbox/data/smbox.db`.

### i18n

- All UI strings are message keys (`#{...}`) in `messages.properties` (English) and `messages_pt.properties`. Add new keys to **both** files.
- The locale is switched with `?lang=xx` (`LocaleChangeInterceptor`) and stored in the `smbox-locale` cookie. The default is English.
- Templates format dates with the `localizedDateFormatter` bean (`LocalizedDateFormatter.formatIsoDate(isoDate, locale)`).

## UI conventions

- Templates in `src/main/resources/templates/` wrap their content in `layout.html` via `th:fragment="layout (content, pageTitle)"`. The layout has a fixed `w-64` left sidebar (categories + "Add New" links, stacked above the content below `md`), `md:pl-64` main content capped at `max-w-3xl`, and a footer. There is no top navbar.
- Tailwind source is `src/main/tailwind/input.css`. It scans `templates/**/*.html`, so a class that appears only in Java code won't be generated. The generated `static/css/app.css` is committed. `font-sans` = Inter (UI), `font-serif` = Merriweather (titles/content). FontAwesome 6.0.0 and the fonts are self-hosted under `static/`, so the app makes no CDN calls.
- Accent color is `indigo-600`, success actions (save/publish) use `emerald-600`, and destructive actions use a `red-600` outline.
