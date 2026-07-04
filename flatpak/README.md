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
./gradlew buildFlatpak
```

The task builds the Java project into a tar.gz archive via `./gradlew packageTarGz`,
stages it in `build/flatpak/`, then runs flatpak-builder to build and install the
Flatpak.