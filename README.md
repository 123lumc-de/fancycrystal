# FancyCrystal

[![Build](https://github.com/LMC-Studios/FancyCrystal/actions/workflows/build.yml/badge.svg)](https://github.com/LMC-Studios/FancyCrystal/actions/workflows/build.yml)
[![bStats](https://img.shields.io/badge/bStats-34195-blue)](https://bstats.org/plugin/bukkit/FancyCrystal/34195)

Eine custom Crystal-Währung für Paper-Server mit Vault-Integration und voll konfigurierbarem Shop.

## Features

- 💎 Eigene Crystal-Währung über Vault (`Economy`-Service)
- 🛒 Chest-GUI-Shop mit vollständig konfigurierbaren Items
- 👥 Spieler-zu-Spieler Zahlungen (`/crystalspay`)
- 🛠️ Admin-Commands (set, add, remove)
- 📊 bStats-Integration (Plugin-ID: **34195**)
- ⚙️ MiniMessage-Support in allen Nachrichten

## Commands

| Command | Beschreibung | Permission |
|---|---|---|
| `/crystals` | Eigenen Kontostand anzeigen | `fancycrystal.use` |
| `/crystalshop` | Shop öffnen | `fancycrystal.shop` |
| `/crystalspay <Spieler> <Betrag>` | Crystals senden | `fancycrystal.use` |
| `/setcrystals <Spieler> <Betrag>` | Kontostand setzen | `fancycrystal.admin` |
| `/addcrystals <Spieler> <Betrag>` | Crystals hinzufügen | `fancycrystal.admin` |
| `/removecrystals <Spieler> <Betrag>` | Crystals entfernen | `fancycrystal.admin` |

## Build

```bash
mvn clean package

Die fertige JAR liegt danach in target/FancyCrystal-1.0.0.jar.
Voraussetzungen

    Paper 1.21+ (Java 25)

    Vault

   
---

## ✅ Zusammenfassung

| Datei | Zweck |
|---|---|
| `pom.xml` | Maven-Build mit Paper, Vault, bStats + Shade |
| `plugin.yml` | Commands, Permissions, Main-Class |
| `config.yml` | Alle Nachrichten + Shop-Items einstellbar |
| `FancyCrystalPlugin.java` | Hauptklasse, Vault-Registrierung, bStats |
| `CrystalStorage.java` | YAML-Persistenz + Stats für bStats |
| `CrystalEconomy.java` | Vault-`Economy`-Implementierung |
| `ShopItem.java` | Datenmodell für ein Shop-Item |
| `ShopConfig.java` | Liest Shop-Items aus `config.yml` |
| `ShopHolder.java` | Marker fürs GUI |
| `ShopGUI.java` | Baut das Inventar |
| `ShopListener.java` | Klick-Handling |
| 6 Command-Klassen | Alle `/crystals*`, `/setcrystals`, `/addcrystals`, `/removecrystals` |
| `build.yml` | GitHub Actions Workflow |
| `README.md` | Repo-Beschreibung |

**Nächster Schritt**: Repo `LMC-Studios/FancyCrystal` auf GitHub anlegen, alle Dateien pushen, Action läuft automatisch. Wenn du willst, baue ich dir noch **Tab-Completer**, **SQLite-Support** oder **Mehrsprachigkeit** dazu. Sag einfach Bescheid! 💎 
