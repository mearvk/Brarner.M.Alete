---
inclusion: always
---

# Product

Brarner.M.Alete is a Java signal-processing platform. A master server reads a
configuration tree and starts one lightweight socket server per **instance**;
every instance performs the same three analyses on an incoming signal:

- **Audio FFT** — magnitude spectrum via jDSP `FastFourier`.
- **Data statistics** — descriptive statistics (n, mean, variance, stddev, min, max, sum).
- **Graphics spectrum** — the FFT magnitude as a plottable CSV series.

Results are written to a file and/or a MySQL `experiments` table, selected
per instance by its `config.xml`.

## Instances are data, not code

Instances are grouped into **modules** (universities, postal, counties, ssa,
species, countries, art, chemistry). There are ~8,900 active instance
directories; each directory holds **only a `config.xml`**. All instances share a
single engine — see `conventions.md`. When adding or changing instances, edit
configuration, never copy code.

## Scope guardrails

- This is signal processing and reporting. Keep new work inside that scope.
- Instance `config.xml` files contain real-world metadata (names, addresses,
  founding years, etc.); treat that data as authoritative and do not fabricate it.
