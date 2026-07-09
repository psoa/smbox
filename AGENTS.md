# Smbox - AI Agent Guide

## Project Overview
**Smbox** is a personal brainstorming notes web application built with Spring Boot 3.3.5, Thymeleaf, and SQLite. It provides CRUD operations for managing dated posts with full-text search capability.

## Architecture & Key Components

### MVC Structure
- **Controller**: `PostController` (package `br.com.psoa.smbox`) - handles all HTTP requests/responses
  - CRUD endpoints: `/` (redirects to `/posts`), `/posts` (list), `/add` (form), `/{id}` (show), `/edit/{id}` (edit form), `/edit` (POST update), `/delete/{id}`
  - Filtering via `?search=` (text) and `?category=` (category ID) query parameters (server-side Java Streams)
- **Controller**: `CategoryController` (package `br.com.psoa.smbox`) - full CRUD for categories
  - Endpoints: `/categories` (list), `/categories/{id}` (show), `/categories/add`, `/categories/edit/{id}`, `/categories/edit` (POST), `/categories/delete/{id}`
- **ControllerAdvice**: `GlobalModelAttributes` - injects `sidebarCategories` (all categories) into every view's model
- **Model**: `Post` (package `br.com.psoa.smbox.model`) - JPA entity with `@Entity` and `@Table(name="post")`
  - Fields: `id` (Long, `post_id`), `date` (String, `post_date`, YYYY-MM-DD), `subject` (`post_subject`), `content` (TEXT, `post_content`), `categories` (Set<Category>, many-to-many via `post_category` join table)
- **Model**: `Category` (package `br.com.psoa.smbox.model`) - JPA entity with `@Entity` and `@Table(name="category")`
  - Fields: `id` (Long, `category_id`), `name` (`category_name`), `description` (`category_description`), `posts` (Set<Post>, mapped-by side, `@JsonIgnore`)
- **Repository**: `PostRepository` - Spring Data JPA interface with `@RepositoryRestResource`
  - Exposes REST API at `/api/` endpoint automatically
  - Custom methods: `findById(Long)`, `deleteById(Long)`
- **Repository**: `CategoryRepository` - exposes REST API at `/categories/` endpoint
  - `@RepositoryRestResource(collectionResourceRel = "categories", path = "categories")`

### Database Layer
- **Database**: SQLite at `${HOME}/smbox/data/smbox.db`
- **ORM**: Hibernate 6.2.0 with SQLite dialect
- **Configuration**: `application.properties` sets datasource URL and Hibernate dialect
- **Critical Note**: Database file must exist before app start

### UI & Templates
- **Template Engine**: Thymeleaf with layout fragment pattern (`th:fragment="layout (content, pageTitle)"`)
- **Base Layout**: `layout.html` - fixed left sidebar (`md:w-64`) listing categories and "Add New" links, plus main content area with `md:pl-64` offset and a bottom footer
- **CSS System**: Tailwind CSS (Play CDN via `cdn.tailwindcss.com`) + FontAwesome 6.0.0 + Google Fonts; no build step required
  - `tailwind.config` inline script maps `font-sans` → Inter, `font-serif` → Merriweather
  - Accent color: `indigo-600`; success actions: `emerald-600`; destructive: `red-600` outline
- **Key Templates**:
  - `list.html` - displays posts with search bar and category filter; client-side no-JS filtering is absent — all filtering is Java Streams in controller
  - `show.html` - single post view with edit/delete links
  - `add.html` / `edit.html` - form views with category checkbox list (`allCategories` model attribute, `categoryIds` request param)
  - `categories/` directory mirrors the same pattern for Category CRUD

## Critical Development Workflows

### Build & Run
```bash
# Development (with hot reload via spring-boot-devtools)
./gradlew bootRun

# Run tests
./gradlew test

# Production build & deploy: builds jar, copies to $HOME/apps/smbox/, stops running instance and restarts
./gradlew deploy

# Manual start/stop (from $HOME/apps/smbox/) — writes PID to smbox.pid
./run.sh
./stop.sh
```

### Database Setup
Required before first run:
```bash
mkdir -p $HOME/smbox/data
touch $HOME/smbox/data/smbox.db
```
DB URL is hardcoded in `application.properties` as `jdbc:sqlite:${HOME}/smbox/data/smbox.db`.

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
- Edit POST submits to `/edit` (not `/edit/{id}`) — `id` is carried in the model attribute
- Category assignment resolved in controller: `categoryRepository.findAllById(categoryIds)` → `post.setCategories(...)`
- Redirects to `/posts` (or `/categories`) after successful save

### Search & Filtering Implementation
- Executed server-side via Java Streams on full result set (not database query, not browser JS)
- Text search (`?search=`): `.toLowerCase().contains()` on subject and content; `.contains()` on date string
- Category filter (`?category=`): matches any category in `post.getCategories()` by ID
- Fetches all posts with `Sort.by("date").descending()` then applies both filters in sequence
- `selectedCategory` and `search` added to model for template state preservation
- **Performance consideration**: May become slow with large post counts

### REST API
- `PostRepository` enabled via `@RepositoryRestResource(collectionResourceRel = "api", path = "api")` → `/api/`
- `CategoryRepository` enabled via `@RepositoryRestResource(collectionResourceRel = "categories", path = "categories")` → `/categories/`
- Uses auto-generated HAL links

## Important Implementation Notes

### Type Inconsistency
- `PostRepository` extends `JpaRepository<Post, Integer>` but methods expect `Long` IDs
- Controller uses `Long` for path variables - this works due to Spring's type coercion
- Fix: Update generic type to `JpaRepository<Post, Long>` for consistency

### UI Design System
- Fixed left sidebar: `md:fixed md:w-64`, stacks above content below `md` breakpoint
- Main content: `md:pl-64` offset, `max-w-3xl` max-width, reading-focused
- Serif font (`Merriweather`) for titles/content, sans-serif (`Inter`) for UI elements
- Tailwind utility classes only — no custom CSS variables; the one custom class is `.prose-content { white-space: pre-wrap; }`

### External Dependencies
- Tailwind CSS Play CDN (`cdn.tailwindcss.com`)
- FontAwesome 6.0.0 (CDN)
- Google Fonts (Inter, Merriweather)

## When Modifying This Codebase

1. **Add new Post fields**: Update `Post.java` model (add column mapping), all post templates (`add.html`, `edit.html`, `show.html`, `list.html`), and stream filter in `PostController.getAllPosts()`
2. **Add new endpoints**: Follow `@GetMapping`/`@PostMapping` pattern in the relevant controller, create corresponding Thymeleaf template under `src/main/resources/templates/`
3. **Modify search**: Edit stream filter in `PostController.getAllPosts()` — consider database query optimization for large datasets
4. **Change styling**: Use Tailwind utility classes directly in templates; adjust sidebar or layout in `layout.html`
5. **Add validation**: Implement in `BindingResult` check or use `@Valid` + annotations on model (`Post.java` / `Category.java`)
6. **Database migrations**: SQLite schema changes require manual SQL against `$HOME/smbox/data/smbox.db`
7. **Add categories to a new entity**: Follow the `Post`↔`Category` many-to-many pattern — owning side on `Post`, `@JsonIgnore` on the inverse side
