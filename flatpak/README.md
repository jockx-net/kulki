# Flatpak build instructions

We will assume the machine has flatpak already installed, but flatpak-builder may need to be installed first. 
For maximum portability let's use the Flatpak distribution of flatpak-builder: 
```
flatpak remote-add --if-not-exists --user flathub https://dl.flathub.org/repo/flathub.flatpakrepo
flatpak install --user org.flatpak.Builder
```
Then to install the app on local PC, put `bin` and `lib` directories produced by the jpackage 
into the same location as `net.jockx.Kulki.yml` manifest, and execute:
```
flatpak run org.flatpak.Builder --force-clean --user --install-deps-from=flathub --repo=repo --install builddir net.jockx.Kulki.yml
```
 The command produces a lot of files in the work directory, so it's best not run directly in the source tree.