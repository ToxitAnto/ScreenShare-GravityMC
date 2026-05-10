<div align="center">

<img src="https://img.shields.io/badge/Minecraft-1.21.x-brightgreen?style=for-the-badge&logo=minecraft" alt="Minecraft 1.21.x"/>
<img src="https://img.shields.io/badge/Java-17+-orange?style=for-the-badge&logo=openjdk" alt="Java 17+"/>
<img src="https://img.shields.io/badge/Velocity-3.3+-blueviolet?style=for-the-badge" alt="Velocity 3.3+"/>
<img src="https://img.shields.io/badge/Paper-1.21+-blue?style=for-the-badge" alt="Paper 1.21+"/>
<img src="https://img.shields.io/badge/License-MIT-lightgrey?style=for-the-badge" alt="License MIT"/>

# 🔍 ScreenShare

**Plugin Velocity per la gestione avanzata di sessioni screenshare**  
Sviluppato per **GravityMC** da [ckanto](https://github.com/ckanto)

[📦 Download](#-installazione) · [📖 Comandi](#-comandi) · [🐛 Bug Report](https://github.com/ckanto/screenshare/issues) · [💬 Discord](#)

</div>

---

## 📋 Indice

- [Panoramica](#-panoramica)
- [Funzionalità](#-funzionalità)
- [Requisiti](#-requisiti)
- [Installazione](#-installazione)
- [Configurazione](#%EF%B8%8F-configurazione)
- [Comandi](#-comandi)
- [Permessi](#-permessi)
- [Flusso di una sessione](#-flusso-di-una-sessione)
- [Pannello ban](#-pannello-ban)
- [Struttura progetto](#-struttura-progetto)
- [Build dal sorgente](#-build-dal-sorgente)
- [Changelog](#-changelog)

---

## 🌐 Panoramica

ScreenShare è un plugin **Velocity** completo per la gestione delle sessioni di screenshare su giocatori sospettati di cheating. Progettato per essere leggero e senza dipendenze esterne, usa il canale `bungeecord:main` nativo di Paper per teletrasportare i giocatori alle coordinate precise configurate, senza companion plugin aggiuntivi.

Quando uno staffer esegue `/ss <player>`, sia lo staffer che il sospettato vengono trasportati nel server SS, ricevono rank dedicati nel tab-list e lo staffer ottiene un **pannello ban cliccabile in chat** per gestire l'intera sessione con un click.

---

## ✨ Funzionalità

### 🎯 Sessioni SS
- Trasporto automatico di **entrambi** (staffer e sospettato) nel server SS
- Teleport alle coordinate precise configurate via `/screenshareadmin setspawn`
- Sessioni multiple gestibili contemporaneamente da staff diversi
- Termine sessione automatico al ban o alla liberazione

### 🏷️ Sistema Rank
- Tab-list aggiornata automaticamente all'avvio della sessione
  - Staffer → `[STAFF] NomeStaffer`
  - Sospettato → `[SOSPETTO] NomeSospettato`
- Reset automatico dei rank originali a fine sessione
- Colore del rank staff configurabile (`BLUE`, `RED`, `GREEN`, `GOLD`, `AQUA`)

### ⚖️ Pannello ban
- Pannello cliccabile in chat con **Adventure ClickEvent** (no GUI inventory)
- Ban preimpostati: **30 giorni**, **ammissione**, **custom con motivo libero**
- Tutti i comandi di ban personalizzabili nel `config.yml`
- Esecuzione ban tramite console, compatibile con qualsiasi plugin ban

### 🚫 Blocco comandi
- Lista comandi bloccati al sospettato durante la sessione (configurabile)
- Blocca `/hub`, `/tp`, `/home`, `/spawn` e tutti i comandi di fuga

### ⚙️ Zero dipendenze
- Usa il canale `bungeecord:main` nativo di Paper — nessun plugin aggiuntivo
- Tutti i messaggi personalizzabili
- Ricarica configurazione a caldo con `/screenshareadmin reload`

---

## 📦 Requisiti

| Componente | Versione | Tipo |
|---|---|---|
| Java | 17+ | Obbligatoria |
| Velocity | 3.3.0-SNAPSHOT+ | Obbligatoria |
| Paper / Purpur (server SS) | 1.21+ | Obbligatoria |
| Plugin ban (LiteBans, AdvancedBan…) | qualsiasi | Obbligatoria |

> **Nessun companion plugin richiesto.** Il teleport alle coordinate usa il canale `bungeecord:main` già incluso in Paper/Spigot.

---

## 🚀 Installazione

### 1. Build

```bash
mvn clean package
# Output: target/ScreenShare-2.0.0.jar
```

### 2. Copia il JAR

```bash
cp target/ScreenShare-2.0.0.jar /percorso/velocity/plugins/
```

### 3. Riavvia Velocity

Il plugin genererà automaticamente il `config.yml` in `plugins/screenshare/`.

### 4. Configura il server SS

Apri `plugins/screenshare/config.yml` e imposta il nome del server SS:

```yaml
spawn:
  server: "screenshare"   # deve corrispondere al nome in velocity.toml
```

### 5. Imposta lo spawn

Vai nel server SS, apri **F3** per leggere le coordinate e lancia il comando dal proxy:

```
/screenshareadmin setspawn <x> <y> <z> [yaw] [pitch]
```

Esempio:
```
/screenshareadmin setspawn 100.5 64.0 -200.0 90.0 0.0
```

> **Perché le coordinate vanno passate a mano?**  
> Velocity è un proxy e non ha accesso diretto alla posizione del giocatore nel mondo Paper. Le coordinate si leggono con F3 sul server SS e si passano al comando.

---

## ⚙️ Configurazione

File: `plugins/screenshare/config.yml`

```yaml
server-name: "GravityMC"

spawn:
  server: "screenshare"   # Nome esatto del server in velocity.toml
  x: 0.0                  # Coordinate impostate con /screenshareadmin setspawn
  y: 64.0
  z: 0.0
  yaw: 0.0
  pitch: 0.0

ranks:
  staff-helper-color: "BLUE"   # BLUE | RED | GREEN | GOLD | AQUA | DARK_RED
  staff-rank:   "STAFF"        # Testo del rank nel tab per lo staffer
  suspect-rank: "SOSPETTO"     # Testo del rank nel tab per il sospettato

# Comandi eseguiti dalla console — usa %player% e %reason%
commands:
  ban-30d:        "ban %player% 30d [SS] Cheating"
  ban-ammissione: "ban %player% [SS] Ammissione hack"
  ban-custom:     "ban %player% [SS] %reason%"

messages:
  prefix:           "&dGravity&fMC "
  ss-start:         "&aScreenshare avviata su &e%player%&a. Vai nella stanza SS!"
  ss-already-in:    "&c%player% e gia in screenshare!"
  ss-finish:        "&aScreenshare terminata."
  ss-no-active:     "&cNon hai nessuna screenshare attiva."
  ss-no-player:     "&cGiocatore non trovato."
  ss-notify-target: "&cSei stato chiamato in screenshare dallo staff! Connettiti su discord!"
  spawn-set:        "&aSpawn SS impostato!"
  spawn-not-set:    "&cIl server della screenshare non e stato impostato!"
  no-permission:    "&cNon hai i permessi!"
  no-commands:      "&cNon puoi usare comandi durante la screenshare!"
  ban-executed:     "&aBan eseguito su &c%player% &a(tipo: %type%)."

restricted-commands:
  - "hub"
  - "tpa"
  - "tpaccept"
  - "tpdeny"
  - "spawn"
  - "home"
  - "sethome"
  - "warp"
  - "tp"
  - "teleport"
  - "back"
  - "kit"
  - "pay"
  - "msg"
  - "tell"
  - "r"
  - "reply"
```

---

## 💬 Comandi

### Staff

| Comando | Descrizione |
|---|---|
| `/ss <player>` | Avvia la sessione SS: porta entrambi nel server SS e apre il pannello ban |
| `/ssfinish` | Termina la sessione SS attiva e resetta i rank di entrambi |
| `/ssban <player> 30d` | Banna il sospettato per 30 giorni (cliccabile dal pannello) |
| `/ssban <player> ammissione` | Banna per ammissione di cheating (cliccabile dal pannello) |
| `/ssban <player> custom <motivo>` | Banna con motivo personalizzato |

### Admin

| Comando | Descrizione |
|---|---|
| `/screenshareadmin setspawn <x> <y> <z> [yaw] [pitch]` | Salva le coordinate di spawn del server SS |
| `/screenshareadmin info` | Mostra server, coordinate spawn e sessioni attive |
| `/screenshareadmin reload` | Ricarica il `config.yml` senza riavviare |

---

## 🔐 Permessi

| Permesso | Descrizione | Default |
|---|---|---|
| `screenshare.staff` | Accesso a `/ss`, `/ssfinish`, `/ssban` | `false` |
| `screenshare.admin` | Accesso a `/screenshareadmin` | `op` |

---

## 🔄 Flusso di una sessione

```
1. Staff esegue /ss <player>
        │
        ├─► Verifica che il player non sia già in sessione
        │
        ├─► Entrambi vengono connessi al server SS
        │
        ├─► Velocity invia le coordinate via canale BungeeCord "Forward"
        │   → Paper esegue il teleport alle coordinate configurate
        │
        ├─► Tab-list aggiornata:
        │     Staffer    →  [STAFF] NomeStaffer
        │     Sospettato →  [SOSPETTO] NomeSospettato
        │
        ├─► Sospettato riceve notifica e i comandi bloccati vengono attivati
        │
        └─► Staffer riceve il pannello ban in chat

2. Staff clicca un pulsante
        │
        ├─► [BAN 30G / AMMISSIONE / CUSTOM]
        │     → Comando eseguito dalla console
        │     → Sessione chiusa automaticamente
        │     → Rank resettati per entrambi
        │
        └─► [LIBERA ✔]
              → /ssfinish eseguito
              → Rank resettati per entrambi
```

---

## 🖥️ Pannello ban

Appena la sessione parte, lo staffer riceve in chat:

```
──────────── PANNELLO SS ────────────
Target: NomePlayer

[BAN 30G]  [AMMISSIONE]  [BAN CUSTOM]  [LIBERA ✔]
─────────────────────────────────────
```

| Pulsante | Azione |
|---|---|
| `[BAN 30G]` | Esegue il ban da 30 giorni e chiude la sessione |
| `[AMMISSIONE]` | Esegue il ban per ammissione e chiude la sessione |
| `[BAN CUSTOM]` | Pre-compila la barra chat con `/ssban <player> custom ` — lo staff aggiunge il motivo |
| `[LIBERA ✔]` | Termina la sessione, resetta i rank e libera il sospettato |

> I pulsanti usano **Adventure ClickEvent** — funzionano in qualsiasi client Minecraft senza mod aggiuntive.

---

## 📁 Struttura progetto

```
screenshare/
├── pom.xml
└── src/main/
    ├── java/it/gravitymc/screenshare/
    │   ├── ScreenShare.java                   ← Entry point Velocity
    │   ├── commands/
    │   │   ├── SSCommand.java                 ← /ss <player>
    │   │   ├── SSFinishCommand.java           ← /ssfinish
    │   │   ├── SSBanCommand.java              ← /ssban
    │   │   └── SSAdminCommand.java            ← /screenshareadmin
    │   ├── listeners/
    │   │   └── PlayerCommandListener.java     ← Blocca comandi al sospettato
    │   └── managers/
    │       ├── ScreenShareManager.java        ← Sessioni e rank tab-list
    │       ├── SpawnManager.java              ← Connessione server + teleport coordinate
    │       └── BanPanelManager.java           ← Pannello ban cliccabile in chat
    └── resources/
        └── config.yml
```

---

## 🔨 Build dal sorgente

Assicurati di avere **Java 17+** e **Maven 3.8+** installati.

```bash
# Clona il repo
git clone https://github.com/ckanto/screenshare.git
cd screenshare

# Build
mvn clean package

# Il JAR si trova in:
# target/ScreenShare-2.0.0.jar
```

**Dipendenze Maven** (gestite automaticamente):

| Dipendenza | Versione | Scope |
|---|---|---|
| `velocity-api` | `3.3.0-SNAPSHOT` | `provided` |
| `snakeyaml` | `2.2` | `compile` (shaded) |

---

## 📝 Changelog

### v2.0.0
- **FIX** — Lo staffer viene ora portato nel server SS insieme al sospettato
- **FIX** — Rank tab-list corretti: `[STAFF]` per lo staffer, `[SOSPETTO]` per il target
- **FIX** — Reset automatico dei rank a fine sessione (ban o liberazione)
- **NUOVO** — Pannello ban cliccabile in chat con Adventure ClickEvent
- **NUOVO** — Comando `/ssban` con supporto `30d` / `ammissione` / `custom`
- **NUOVO** — `/screenshareadmin setspawn <x> <y> <z>` con coordinate precise
- **NUOVO** — `/screenshareadmin info` e `reload`
- **NUOVO** — Sezione `commands` nel `config.yml` per personalizzare i ban senza toccare il codice

### v1.0.0
- Release iniziale
- Trasporto del solo sospettato nel server SS
- Blocco comandi durante la screenshare
- Comandi `/ss`, `/ssfinish`, `/screenshareadmin`

---

## 👤 Autore

Sviluppato con ❤️ da **ckanto** per **GravityMC**

- GitHub: [@ckanto](https://github.com/ckanto)
- Server: GravityMC

---

<div align="center">
<sub>ScreenShare © 2024 ckanto — GravityMC. Released under the MIT License.</sub>
</div>
