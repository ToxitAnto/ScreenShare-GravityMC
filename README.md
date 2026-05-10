# 🎮 ScreenShare — GravityMC

> Plugin **Velocity** per gestire sessioni di screenshare su giocatori sospettati di cheating.  
> Versione `2.0.0` · Autore **ckanto** · Java 17+ · Velocity 3.3+

---

## 📋 Indice

- [Funzionalità](#-funzionalità)
- [Requisiti](#-requisiti)
- [Installazione](#-installazione)
- [Configurazione](#-configurazione)
- [Comandi](#-comandi)
- [Permessi](#-permessi)
- [Flusso di una sessione](#-flusso-di-una-sessione)
- [Pannello ban](#-pannello-ban)
- [Struttura del progetto](#-struttura-del-progetto)
- [Build](#-build)
- [Changelog](#-changelog)

---

## ✨ Funzionalità

- ✅ **Trasporto automatico di entrambi** — lo staff e il sospettato vengono portati nel server SS
- ✅ **Rank nel tab-list** — `[STAFF]` allo staffer, `[SOSPETTO]` al target, resettati a fine sessione
- ✅ **Pannello ban cliccabile in chat** — pulsanti Adventure ClickEvent, nessuna GUI inventory
- ✅ **Ban preimpostati** — 30 giorni, ammissione, custom con motivo libero
- ✅ **Reset rank automatico** — al ban o alla liberazione i rank tornano normali
- ✅ **Blocco comandi** — il sospettato non può usare `/hub`, `/tp`, `/home` ecc. durante la SS
- ✅ **Setspawn con coordinate** — `/screenshareadmin setspawn <x> <y> <z> [yaw] [pitch]`
- ✅ **Comandi ban personalizzabili** — tutto configurabile nel `config.yml` senza toccare il codice
- ✅ **Zero dipendenze extra** — usa il canale `bungeecord:main` nativo di Paper per il teleport

---

## 📦 Requisiti

| Componente | Versione |
|---|---|
| Java | 17 o superiore |
| Velocity | 3.3.0-SNAPSHOT o superiore |
| Server backend | Paper / Purpur 1.21+ |
| Plugin ban | Qualsiasi (LiteBans, AdvancedBan…) compatibile con console |

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

Il plugin genererà automaticamente il `config.yml` nella cartella `plugins/screenshare/`.

### 4. Imposta lo spawn

Vai nel server SS, apri **F3** per leggere le coordinate, poi esegui:

```
/screenshareadmin setspawn <x> <y> <z> [yaw] [pitch]
```

Esempio:
```
/screenshareadmin setspawn 100.5 64.0 -200.0 90.0 0.0
```

> **Nota:** Velocity è un proxy e non può leggere la posizione del giocatore direttamente.  
> Le coordinate vanno passate manualmente oppure modificate nel `config.yml`.

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
  staff-helper-color: "BLUE"   # Colore del rank staff nel tab (BLUE/RED/GREEN/GOLD/AQUA)
  staff-rank: "STAFF"          # Testo mostrato nel tab per lo staffer
  suspect-rank: "SOSPETTO"     # Testo mostrato nel tab per il sospettato

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

| Comando | Descrizione | Permesso |
|---|---|---|
| `/ss <player>` | Avvia la sessione SS su un giocatore | `screenshare.staff` |
| `/ssfinish` | Termina la sessione SS attiva | `screenshare.staff` |
| `/ssban <player> 30d` | Banna per 30 giorni (cliccabile dal pannello) | `screenshare.staff` |
| `/ssban <player> ammissione` | Banna per ammissione (cliccabile dal pannello) | `screenshare.staff` |
| `/ssban <player> custom <motivo>` | Banna con motivo personalizzato | `screenshare.staff` |
| `/screenshareadmin setspawn <x> <y> <z> [yaw] [pitch]` | Imposta le coordinate di spawn SS | `screenshare.admin` |
| `/screenshareadmin info` | Mostra spawn attuale e sessioni attive | `screenshare.admin` |
| `/screenshareadmin reload` | Ricarica il `config.yml` | `screenshare.admin` |

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
        ├─► Entrambi vengono connessi al server SS (spawn.server)
        │
        ├─► Velocity invia le coordinate via canale BungeeCord "Forward"
        │   → il server Paper esegue il teleport alle coordinate salvate
        │
        ├─► Tab-list aggiornata:
        │     Staff   → [STAFF] NomeStaff
        │     Target  → [SOSPETTO] NomeTarget
        │
        ├─► Target riceve la notifica e non può usare i comandi bloccati
        │
        └─► Staff riceve il pannello ban in chat

2. Staff clicca un pulsante dal pannello
        │
        ├─► Ban (30d / ammissione / custom) → comando eseguito dalla console
        │                                   → sessione chiusa automaticamente
        │
        └─► Libera → /ssfinish → rank resettati per entrambi
```

---

## 🖥️ Pannello ban

Appena la sessione parte, lo staff riceve in chat:

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
| `[BAN CUSTOM]` | Pre-compila la chat con `/ssban <player> custom ` — lo staff aggiunge il motivo |
| `[LIBERA ✔]` | Esegue `/ssfinish`, resetta i rank e libera il sospettato |

> I pulsanti usano **Adventure ClickEvent** — funzionano in qualsiasi client Minecraft senza mod.

---

## 📁 Struttura del progetto

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
    │       ├── ScreenShareManager.java        ← Gestione sessioni e rank tab-list
    │       ├── SpawnManager.java              ← Connessione server + teleport coordinate
    │       └── BanPanelManager.java           ← Pannello ban cliccabile in chat
    └── resources/
        └── config.yml
```

---

## 🔨 Build

```bash
# Clona il repo
git clone https://github.com/gravitymc/screenshare.git
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
- **FIX** — Lo staff viene ora portato nel server SS insieme al sospettato
- **FIX** — Rank tab-list corretti: `[STAFF]` per lo staffer, `[SOSPETTO]` per il target
- **FIX** — Reset automatico dei rank a fine sessione
- **NUOVO** — Pannello ban cliccabile in chat con Adventure ClickEvent
- **NUOVO** — Comando `/ssban` con supporto `30d` / `ammissione` / `custom`
- **NUOVO** — Comando `/screenshareadmin setspawn <x> <y> <z>` con coordinate precise
- **NUOVO** — Comando `/screenshareadmin info` e `reload`
- **NUOVO** — Sezione `commands` nel `config.yml` per personalizzare i ban

### v1.0.0
- Release iniziale
- Trasporto del solo sospettato nel server SS
- Blocco comandi durante la screenshare
- Comandi `/ss`, `/ssfinish`, `/screenshareadmin`

---

<div align="center">
  <sub>Made with ❤️ for GravityMC · <a href="https://github.com/gravitymc">github.com/gravitymc</a></sub>
</div>
