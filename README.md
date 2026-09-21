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
