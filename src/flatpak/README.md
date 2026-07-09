# Flatpak build instructions

We will assume the machine has flatpak already installed, but flatpak-builder may
need to be installed first. For maximum portability let's use the Flatpak distribution
of flatpak-builder:

```
flatpak remote-add --if-not-exists --user flathub https://dl.flathub.org/repo/flathub.flatpakrepo
flatpak install --user org.flatpak.Builder
```

Run the Gradle task from the project root:
```
./gradlew flatpakBuild
```

The task stages the native binary and metadata into `build/flatpak/staging/`,
then runs flatpak-builder to build the Flatpak into `build/flatpak/`.

To install the Flatpak locally:
```
./gradlew flatpakInstall
```

## Cache directories

flatpak-builder creates several cache artifacts during the build. All of them
are written inside `build/flatpak/` to keep the project root clean:

- `build/flatpak/builddir/` — build directory
- `build/flatpak/repo/` — local flatpak repo
- `build/flatpak/.flatpak*` — flatpak-builder state files
