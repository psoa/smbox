# Getting Started

Smbox is a desktop notes app for Linux and Windows. It stores data in SQLite under your home directory and does not need a separate server.

## Install

Requires **JDK 25**. The Gradle wrapper (9.7.1) can run on that JDK.

**Linux:** build and install the `.deb`:

```bash
./gradlew jpackageLinux
# installer is under build/jpackage/
```

**Windows:** on a Windows machine (jpackage cannot cross-compile):

```bash
./gradlew jpackageWindows
```

**Development:**

```bash
./gradlew bootRun
```

## Data

On first launch Smbox creates:

| Path | Purpose |
| --- | --- |
| `~/smbox/data/smbox.db` | Notes database (`%USERPROFILE%\smbox\data\smbox.db` on Windows) |
| `~/smbox/smbox.log` | Application log |
| `~/smbox/smbox.lock` | Single-instance lock |
| `~/smbox/window.properties` | Saved window size and position |

A second launch shows "Smbox is already running" instead of opening another window.
