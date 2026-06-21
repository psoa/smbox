# Smbox - AI Agent Guide

## Project Overview
**Smbox** is a personal brainstorming notes web application built with Spring Boot 3.3.5, Thymeleaf, and SQLite. It provides CRUD operations for managing dated posts with full-text search capability.

## Architecture & Key Components

### MVC Structure
- **Controller**: `PostController` (package `br.com.psoa.smbox`) - handles all HTTP requests/responses
  - CRUD endpoints: `/posts` (list), `/add` (form), `/{id}` (show), `/edit/{id}` (edit), `/delete/{id}`
  - Search functionality via `?search=` query parameter (client-side filtering)
- **Model**: `Post` (package `br.com.psoa.smbox.model`) - JPA entity with `@Entity` and `@Table("post")`
  - Fields: `id` (Long, auto-generated), `date` (String, YYYY-MM-DD), `subject`, `content` (TEXT column)
- **Repository**: `PostRepository` - Spring Data JPA interface with `@RepositoryRestResource`
  - Exposes REST API at `/api/` endpoint automatically
  - Custom methods: `findById(Long)`, `deleteById(Long)`

### Database Layer
- **Database**: SQLite at `${HOME}/smbox/data/smbox.db`
- **ORM**: Hibernate 6.2.0 with SQLite dialect
- **Configuration**: `application.properties` sets datasource URL and Hibernate dialect
- **Critical Note**: Database file must exist before app start (manual setup required via `SMBOX_HOME` env var)

### UI & Templates
- **Template Engine**: Thymeleaf with layout fragment pattern
- **Base Layout**: `layout.html` - defines navbar, main container, footer, and custom CSS variables
- **CSS System**: Bootstrap 5.3.0 + custom CSS with CSS variables (`--primary-color`, `--accent-color`, etc.)
- **Key Templates**:
  - `list.html` - displays posts with search bar, client-side filtering on subject/content/date
  - `show.html` - single post view
  - `add.html` / `edit.html` - form views with Thymeleaf `@ModelAttribute` binding

## Critical Development Workflows

### Build & Run
```bash
# Development (with hot reload via spring-boot-devtools)
./gradlew bootRun

# Production build & deploy
./gradlew buildAndCopy  # Creates smbox.jar in $HOME/smbox and copies run.sh

# Manual execution
java -jar smbox.jar    # Requires SMBOX_HOME/data/smbox.db to exist
```

### Database Setup
Required before first run:
```bash
export SMBOX_HOME=/path/to/install
mkdir -p $SMBOX_HOME/data
touch $SMBOX_HOME/data/smbox.db
```

### Testing
- Test framework: JUnit 5 (Spring Boot Test starter configured)
- No existing tests present - add to `src/test/java/br/com/psoa/smbox/`

## Project-Specific Patterns & Conventions

### Date Handling
- **Database storage**: String format `yyyy-MM-dd`
- **Display format**: Also `yyyy-MM-dd` for search functionality
- **Post creation**: Auto-populated with current date in controller (`SimpleDateFormat("yyyy-MM-dd")`)
- **Subject default**: Auto-generated as `"Brainstorm dd/MM/yyyy"` when creating new post

### Form Processing Pattern
- GET endpoint returns form view with model attribute (e.g., `@GetMapping("/add")`)
- POST endpoint receives `@ModelAttribute("post")` with `BindingResult` for validation
- Uses `postRepository.save(post)` for both INSERT and UPDATE (JPA handles UPSERT via `id`)
- Redirects to `/posts` after successful save

### Search Implementation
- Executed client-side in list view via Java Streams (not database query)
- Filters on `.toLowerCase().contains()` for subject, content, and exact match for date
- Fetches all posts with `Sort.by("date").descending()` then filters in memory
- **Performance consideration**: May become slow with large post counts

### REST API
- Enabled via `@RepositoryRestResource(collectionResourceRel = "api", path = "api")`
- Exposes standard CRUD endpoints at `/api/` (e.g., `GET /api/`, `POST /api/`)
- Uses auto-generated HAL links
- Repository type mismatch: extends `JpaRepository<Post, Integer>` but methods use `Long`

## Important Implementation Notes

### Type Inconsistency
- `PostRepository` extends `JpaRepository<Post, Integer>` but methods expect `Long` IDs
- Controller uses `Long` for path variables - this works due to Spring's type coercion
- Fix: Update generic type to `JpaRepository<Post, Long>` for consistency

### UI Design System
- Fixed navbar (top: 70px), fixed footer (height: 60px)
- Main container max-width: 740px (reading-focused layout)
- Serif font (`Merriweather`) for titles/content, sans-serif (`Inter`) for UI
- Responsive: Media query breakpoint at 768px

### External Dependencies
- Bootstrap 5.3.0 (CDN)
- FontAwesome 6.0.0 (CDN)
- Google Fonts (Inter, Merriweather)

## When Modifying This Codebase

1. **Add new Post fields**: Update `Post.java` model, `Post.html` templates, and search filter in controller
2. **Add new endpoints**: Follow `@GetMapping`/`@PostMapping` pattern in `PostController`, create corresponding Thymeleaf template
3. **Modify search**: Edit stream filter in `getAllPosts()` method - consider database query optimization
4. **Change styling**: Modify CSS variables in `layout.html` `<style>` block for theme changes
5. **Add validation**: Implement in `BindingResult` check or use `@Valid` + annotations on `Post` model
6. **Database migrations**: SQLite schema changes require manual SQL in database file

