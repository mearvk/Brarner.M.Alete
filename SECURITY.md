# Security Policy — Brarner.M.Alete

MEARVK LLC | Maximilian Eric Alexander Rupplin von Keffikon

---

## Supported Versions

| Version | Supported |
|---------|-----------|
| 1.x (current) | ✅ |
| pre-release / snapshots | ❌ |

---

## Network Exposure

BMA opens raw TCP `ServerSocket` listeners on up to 1645 ports across 8 modules. By default:

- All sockets bind to `0.0.0.0` (all interfaces)
- No authentication is performed on incoming connections
- No TLS/SSL is applied to socket streams

**Recommendation:** Run BMA behind a firewall or bind sockets to `127.0.0.1` only unless external access is explicitly required. To restrict binding, modify the `ServerSocket` constructor in each module's `BaseServer.java`:

```java
// Restrict to localhost only
new ServerSocket(port, 50, InetAddress.getByName("127.0.0.1"))
```

---

## Known Security Considerations

| Area | Risk | Mitigation |
|------|------|------------|
| Unauthenticated sockets | Any process on the network can send data to any active port | Firewall / bind to localhost |
| No input validation | Malformed `readInt()` / `readDouble()` streams can cause thread exceptions | Wrap handlers in try-catch (already done); add length bounds checks |
| MySQL credentials in config.xml | `<db-pass>` stored in plaintext | Use environment variables or a secrets manager; restrict file permissions (`chmod 600`) |
| Unbounded thread creation | Each accepted connection spawns a new thread | Consider a thread pool (`ExecutorService`) for high-load deployments |
| SSA duplicate port (fixed) | `nc/charlotte` and `nc/raleigh` both used port 9771 | Fixed: `nc/raleigh` reassigned to port 9797 |

---

## Reporting a Vulnerability

If you discover a security vulnerability in this project:

1. **Do not open a public GitHub issue.**
2. Email the maintainer directly or open a private security advisory via GitHub's Security tab.
3. Include:
   - Description of the vulnerability
   - Steps to reproduce
   - Affected module(s) and port(s)
   - Suggested fix if known

Response target: acknowledgement within 5 business days, patch within 30 days for confirmed issues.

---

## Hardening Checklist

- [ ] Firewall all BMA ports (8000–20005) from public internet
- [ ] Bind sockets to `127.0.0.1` if only local access is needed
- [ ] Set `<db-pass>` via environment variable, not plaintext XML
- [ ] Set `chmod 600` on all instance `config.xml` files containing credentials
- [ ] Disable unused modules in `source-code/config.xml` (`enabled="false"`)
- [ ] Keep `countries` module disabled until instances are explicitly needed
- [ ] Review active instance count before deployment — each active port is an open listener
