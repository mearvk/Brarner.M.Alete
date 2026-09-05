# Brarner.M.Alete

## Bitcoin Conjegeum

![](https://github.com/mearvk/Ubuntu.Determinant.Beta.Restricted/blob/main/images/Bitcoin_and_wallet_in_slots_2K_202609042306%20(1).jpeg)

bc1qs6v4q9zsw70t0umk3m0quhvf9dr6cdeskl28dh

US Democratic and US Policy.

## MearvK Ltd - MEARVK LLC

Maximilian Eric Alexander Rupplin von Keffikon - MEARVK - MEARVK LLC

Owner of Establishment of Corporate ongoing Finance - US United States a Minister

Owner of Miramax Films UK & US United States and Settlement - NO GODZILLA

Meredith College - Raleigh, NC

NC State University (NCSU) - Raleigh, NC

We are not all in the Country as technical magicians.

![Profile views](https://views.igorkowalczyk.dev/api/badge/@mearvk?style=flat)

---

## Overview

Signal processing platform serving North Carolina institutions, USPS postal facilities, NC counties, countries worldwide, Social Security Administration offices nationwide, and chemistry science divisions. Each instance performs audio FFT, statistical data analysis, and graphics spectrum processing via configurable socket-based data intake and dual output (file + database).

## Quick Start

1. Configure active instances in each module's `config.xml`
2. Run `BaseServer.java` — starts all active instances on their assigned ports
3. Send signal data to any active port; results written to `output/` and/or MySQL

## Architecture

```
BaseServer.java (master)
  ├── reads source-code/config.xml (module registry)
  ├── universities/config.xml → starts active university instances
  ├── postal/config.xml       → starts active postal instances
  ├── counties/config.xml     → starts active county instances
  ├── countries/config.xml    → starts active country instances  [disabled by default]
  ├── ssa/config.xml          → starts active SSA instances
  ├── art/config.xml          → starts active art museum instances
  ├── species/config.xml      → starts active species/phylum instances
  └── chemistry/config.xml    → starts active chemistry instances
```

Each module also has its own `BaseServer.java` for standalone operation.

## Project Structure

### Universities (53 instances)
NC public and private universities — `source-code/universities/<name>/`

### Postal Offices (55 instances)
USPS Processing & Distribution Centers and Post Office Branches — `source-code/postal/<name>/`

**Processing & Distribution Centers (3):**
- Charlotte Processing & Distribution Center
- Greensboro Processing & Distribution Center
- Raleigh Processing & Distribution Center

**Post Office Branches (52):**
- Charlotte Main Post Office, Raleigh Main Post Office, Greensboro Main Post Office
- Durham, Winston-Salem, Fayetteville, Asheville, Wilmington, High Point, Cary
- Concord, Gastonia, Jacksonville, Chapel Hill, Rocky Mount, Burlington
- Huntersville, Apex, Hickory, Greenville, Mooresville, Wake Forest
- Monroe, Salisbury, Matthews, Goldsboro, New Bern, Statesville
- Kinston, Thomasville, Hendersonville, Boone, Morganton, Elizabeth City
- Roanoke Rapids, Tarboro, Mount Airy, Lumberton, Shelby, Havelock
- Lexington, Lenoir, Clinton, Sanford, Asheboro, Albemarle
- Waynesville, Reidsville, Eden, Laurinburg, Henderson, Washington

### Counties (100 instances)
All 100 North Carolina counties — `source-code/counties/nc/<county>/`

### Countries (195 instances)
All 195 countries worldwide — `source-code/countries/<country>/`

Organized by country name (e.g., `countries/united-states/`, `countries/japan/`). Each config.xml includes country name, ISO code, continent, capital, population, and area. Disabled by default in `source-code/config.xml` — set `enabled="true"` then flip individual instances to `active="true"` to bring up.

### Art Museums (20 instances — all active)
NC art museums and cultural centers — `source-code/art/`
- North Carolina Museum of Art, Ackland Art Museum, AD Gallery
- African American Atelier, Harvey B. Gantt Center, Delta Arts Center
- Diggs Gallery (WSSU), NCCU Art Museum, NC A&T State University Galleries
- Cameron Art Museum, Mai Myette Black Fine Arts Museum, CS Brown Cultural Arts Center
- Tryon Palace Collections, NC Museum of History, Maritime Museum Beaufort
- Roanoke Island Festival Park, 82nd Airborne Division War Memorial Museum
- African American Cultural Complex, American Classic Motorcycle Museum, Andy Griffith Museum

### Species / Animalia (35 phyla, 11 active)
Animal kingdom phyla — `source-code/species/`

Active: cnidaria, ctenophora, myxozoa, placozoa, porifera, annelida, arthropoda, brachiopoda, bryozoa, chaetognatha, chordata, echinodermata, mollusca, nematoda, platyhelminthes. Remaining 20 phyla on standby.

### Chemistry Divisions (5 instances)
The 5 fundamental divisions of chemistry — `source-code/chemistry/<division>/`
- Analytical Chemistry
- Biochemistry
- Inorganic Chemistry
- Organic Chemistry
- Physical Chemistry

### SSA Offices (1182 instances)
Social Security Administration field offices nationwide — `source-code/ssa/<state>/<city>/`

Organized by state then city (e.g., `ssa/nc/raleigh/`, `ssa/pa/mt-lebanon/`). Each config.xml includes office code, name, full address, city, state, ZIP, phone, and fax. Javadoc in each SignalProcessing.java documents the same.

### Each instance contains:
- `SignalProcessing.java` — Audio FFT, statistical data analysis, and graphics spectrum processing
- `config.xml` — Instance-specific configuration (data source, socket, database, output paths)

## Configuration

### Hierarchy
| Level | File | Purpose |
|-------|------|---------|
| Master | `source-code/config.xml` | Enable/disable modules |
| Module | `<module>/config.xml` | Port range, active/standby instances |
| Instance | `<module>/<instance>/config.xml` | Data source, socket, DB, output paths |

### Port Ranges
| Module | Port Range | Instances |
|--------|-----------|-----------|
| Universities | 8000–8052 | 53 |
| Postal | 9000–9054 | 55 |
| Counties NC | 9100–9199 | 100 |
| SSA Offices | 9200–10381 | 1182 |
| Countries | 11000–11194 | 195 |
| Art Museums | 18400–18419 | 20 |
| Chemistry | 20001–20005 | 5 |
| Species | 10400–10434 | 35 |

**Total: 1645 instances**

### Activating an Instance

Edit the module's `config.xml` and set `active="true"`:
```xml
<instance name="ncsu" port="8032" active="true" status="receiving" />
```

### Data Output

Each instance supports dual output configured via `<data-output>`:
- `file` — writes `.rdns` files to `output/<module>/<instance>/`
- `database` — inserts into MySQL `experiments` table
- `both` — writes to both

## Configuration Files
- `source-code/server-config.xml` — Master server port assignments
- `source-code/universities.config` — University listing (name, type, city)
- `source-code/postal.offices.config` — Postal office listing (name, type, city/county/ZIP)
- `source-code/counties.nc.config` — County listing (name, seat, year established, population)
- `source-code/countries/data/countries.csv` — Country listing (name, ISO code, continent, capital, population, area)
- `source-code/ssa.administrations.csv` — SSA office listing (code, name, address, city, state, ZIP, phone, fax, hours)
- `source-code/config-files/chemistry.divisions.txt` — Chemistry division listing (5 fundamental divisions)

## Building

### Linux
```bash
./build.sh
```

### Windows
```bat
build.bat
```

Both scripts compile all Java sources under `source-code/` and package them into `Brarner.M.Alete.jar`. Dependency JARs live in `jars/` (subdirs: `ai/`, `chemistry/`, `java-fx/`, `json/`, `logging/`, `signal-processing/`). The build scripts recurse `jars/` automatically — no manual classpath setup needed.

## NIO Masquerade Layer

Integrates with [Java.Web.Server.Telnet.Front.Java.21](https://github.com/mearvk/Java.Web.Server.Telnet.Front.Java.21) NIO Masquerade Engine. The masquerade layer binds local IPs and bridges non-blocking NIO connections to BMA's blocking signal processing architecture.

**Configuration files:**
- `configuration/nio-masquerade-config.xml` — NIO settings, port range mode, managed ports
- `configuration/masquerade-modules.xml` — Module registry with port ranges for auto-discovery

**Port range mode:** `standard` (0–65535 on 127.0.0.1)

All 1590 BMA signal processing ports (8000–20005) are registered as masquerade-aware modules, enabling NIO routing via XML packets on port 2000:
```xml
<nwe-route><port>11000</port><payload>SIGNAL|data</payload></nwe-route>
```

## Dependencies

- Java 11+
- [jDSP](https://github.com/psambit9791/jDSP) — signal processing / FFT (`jars/signal-processing/`)
- [Fastjson](https://github.com/alibaba/fastjson) — JSON serialization (`jars/json/`)
- [CDK](https://cdk.github.io/) — Chemistry Development Kit (`jars/chemistry/`)
- [DJL](https://djl.ai/) + ONNX Runtime — AI/ML inference (`jars/ai/`)
- JavaFX 26 — graphics spectrum rendering (`jars/java-fx/`)
- SLF4J — logging (`jars/logging/`)
- MySQL/MariaDB (optional, for database output)

## Files

- `README.md` — This file
- `STRUCTURE.txt` — Detailed project layout
- `ETHICS.txt` — Usage ethics and principles
- `LEGAL.md` — Legal information
- `LICENSE.md` — License
- `SECURITY.md` — Security policy and vulnerability reporting
- `MEARVK.md` — Author narrative
- `MORDALS.md` — Moral framework document

## Author

Maximilian Eric Alexander Rupplin von Keffikon  
MEARVK LLC
