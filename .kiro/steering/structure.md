---
inclusion: always
---

# Project structure

```
source-code/
  Main.java              entry point -> delegates to BaseServer
  BaseServer.java        master server: reads config.xml, starts active instances
  config.xml             master registry: which modules are enabled
  bma/
    SignalProcessing.java  THE single shared engine (see conventions.md)
  <module>/
    BaseServer.java      standalone server for just this module
    config.xml           module registry: port range + active/standby instances
    <instance>/          one dir per instance -- holds ONLY config.xml
      config.xml
```

Nesting varies by module: `counties/nc/<county>/`, `ssa/<state>/<city>/`, and
`species/<phylum>/<class>/<family>/` (up to four levels deep).

## Configuration hierarchy (three tiers)

1. `source-code/config.xml` — enables/disables modules.
2. `source-code/<module>/config.xml` — the `<module-config>` root declares a
   `port-start`/`port-end` range and lists `<instance name= port= active=>`.
   The port here is the **authoritative listening port** that `BaseServer` binds.
3. `source-code/<module>/<instance>/config.xml` — per-instance data source,
   socket target, DB connection and output paths.

### Rules that keep configs correct

- **Ports are unique per instance and must fall inside the module range.**
  `BaseServer` skips duplicate or out-of-range ports with a warning. The port in
  the module config is authoritative; an instance's own `<socket-port>` is only
  a *client* connect target used when `data-source=socket`.
- **Not every module uses `<module-config>`.** `chemistry/config.xml` uses a
  `<chemistry-config>` root with no port range. `BaseServer` reads the document
  root generically and treats the port range as optional — do not assume the tag.
- Instance config paths are resolved by `BaseServer` from the module config's own
  directory + the instance name. Never hardcode an instance config path, and
  never introduce the old `<module>.<instance>` dotted-path form (it is a bug).
