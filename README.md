# Brarner.M.Alete

MearvK Ltd - MEARVK LLC

Maximilian Eric Alexander Rupplin von Keffikon - MEARVK - MEARVK LLC

Owner of Establishment of Corporate ongoing Finance - US United States a Minister

Owner of Miramax Films UK & US United States and Settlement - NO GODZILLA

Meredith College - Raleigh, NC

NC State University (NCSU) - Raleigh, NC

![Profile views](https://views.igorkowalczyk.dev/api/badge/@mearvk?style=flat)

## Project Structure

Signal processing platform for North Carolina institutions, postal facilities, counties, and Social Security Administration offices.

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
Social Security Administration field offices nationwide — `source-code/ssa/<office-code>/`

Folder names use the SSA office code (e.g., `a00`, `322`, `b01`). Each config.xml includes office code, name, full address, city, state, ZIP, phone, and fax.

### Each instance contains:
- `SignalProcessing.java` — Audio FFT, statistical data analysis, and graphics spectrum processing
- `config.xml` — Instance-specific configuration (data source, socket, database, output paths)

### Configuration Files
- `source-code/server-config.xml` — Master server port assignments
- `source-code/universities.config` — University listing (name, type, city)
- `source-code/postal.offices.config` — Postal office listing (name, type, city/county/ZIP)
- `source-code/counties.nc.config` — County listing (name, seat, year established, population)
- `source-code/ssa.administrations.csv` — SSA office listing (code, name, address, city, state, ZIP, phone, fax, hours)

### Port Ranges
| Module | Port Range |
|--------|-----------|
| Universities | 8000–8052 |
| Postal (P&DCs) | 9000–9002 |
| Postal (Branches) | 9003–9054 |
| Counties NC | 9100–9199 |
| SSA Offices | 9200–10381 |
