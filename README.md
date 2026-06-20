# Brarner.M.Alete

MearvK Ltd - MEARVK LLC

Maximilian Eric Alexander Rupplin von Keffikon - MEARVK - MEARVK LLC

Owner of Establishment of Corporate ongoing Finance - US United States a Minister

Owner of Miramax Films UK & US United States and Settlement - NO GODZILLA

Meredith College - Raleigh, NC

NC State University (NCSU) - Raleigh, NC

![Profile views](https://views.igorkowalczyk.dev/api/badge/@mearvk?style=flat)

---

## Overview

Signal processing platform serving North Carolina institutions, USPS postal facilities, NC counties, and Social Security Administration offices nationwide. Each instance performs audio FFT, statistical data analysis, and graphics spectrum processing via configurable socket-based data intake and dual output (file + database).

## Quick Start

1. Configure active instances in each module's `config.xml`
2. Run `BaseServer.java` — starts all active instances on their assigned ports
3. Send signal data to any active port; results written to `output/` and/or MySQL

## Architecture

```
BaseServer.java (master)
  ├── reads source-code/config.xml (module registry)
  ├── universities/config.xml → starts active university instances
  ├── postal/config.xml      → starts active postal instances
  ├── counties/config.xml    → starts active county instances
  └── ssa/config.xml         → starts active SSA instances
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
| Postal (P&DCs) | 9000–9002 | 3 |
| Postal (Branches) | 9003–9054 | 52 |
| Counties NC | 9100–9199 | 100 |
| SSA Offices | 9200–10381 | 1182 |

**Total: 1390 instances**

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
- `source-code/ssa.administrations.csv` — SSA office listing (code, name, address, city, state, ZIP, phone, fax, hours)

## Dependencies

- Java 11+
- [jDSP](https://github.com/psambit9791/jDSP) — `com.github.psambit9791.jdsp` (Fast Fourier Transform)
- MySQL/MariaDB (optional, for database output)

## Files

- `README.md` — This file
- `STRUCTURE.txt` — Detailed project layout
- `ETHICS.txt` — Usage ethics and principles
- `LEGAL.md` — Legal information
- `LICENSE.md` — License
- `SECURITY.md` — Security policy

## Author

Maximilian Eric Alexander Rupplin von Keffikon  
MEARVK LLC
