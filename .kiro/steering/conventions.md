---
inclusion: always
---

# Coding conventions

## One shared engine — never copy per instance

`source-code/bma/SignalProcessing.java` is the **single** signal-processing
engine for every instance. It is parameterised by an instance's qualified name
and its `config.xml` path. This class previously existed as ~9,000 near-identical
copies; that is fixed and must not regress.

- Do **not** create a `SignalProcessing.java` (or any per-instance logic) inside
  an instance directory. Instance directories contain only `config.xml`.
- Behaviour differences between instances are expressed as **configuration**, not
  code. If a genuine new behaviour is needed, add it to the shared engine behind a
  config switch.

## Fail loudly, not silently

Do not swallow exceptions into hardcoded fallbacks. The engine throws
`IOException` (e.g. missing config, empty input) and `BaseServer` logs and skips
bad instances. Preserve this: a misconfiguration must surface, not be hidden.

## Output format contract

All results share one format so they stay machine- and human-readable. Keep it
stable; if you extend it, extend the header, do not break existing columns.

- A commented header block, each line `# key: value`, naming: `instance`,
  `analysis`, `generated` (UTC ISO-8601), `samples`, plus analysis-specific keys.
  Downstream parsers skip `#` lines.
- Floating-point values use a fixed scale (6 dp) formatted in `Locale.US`, so the
  decimal separator is always `.` regardless of the host locale. Never print raw
  `Double.toString`.
- Bodies: audio = `bin,magnitude` CSV; graphics = `x,y` CSV; data = aligned
  `key = value` lines.

## Style

- 4-space indentation, braces on the same line, no wildcard imports in new files
  where practical.
- Favour `try`-with-resources for sockets, streams, files and JDBC.
- End files with a trailing newline.
