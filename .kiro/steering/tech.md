---
inclusion: always
---

# Tech stack & commands

- **Language:** Java (builds under JDK 9+; developed/validated on JDK 25).
- **Key dependency:** jDSP `3.1.1` (`com.github.psambit9791.jdsp`) for the FFT.
  Only one jDSP version may be present — see below.
- **Other bundled libs** live under `jars/` (`ai/`, `chemistry/`, `java-fx/`,
  `json/`, `logging/`, `signal-processing/`).
- **Database:** MySQL table `experiments(experiment_name, experiment_data)`.

## Build

The primary build is the shell script, which globs every jar under `jars/`:

```bash
./build.sh        # Linux/macOS  -> Brarner.M.Alete.jar
build.bat         # Windows
```

`pom.xml` is a *separate* multi-module Maven project (projects, primary-source,
servlets, ejbs, ear) and does **not** build `source-code/`. The two build systems
are not unified yet; if you touch one, note the other in your summary.

## Quick compile / smoke test (engine only)

```bash
CP=$(find jars/signal-processing -name '*.jar' | tr '\n' ':')
javac -d build/classes -cp "$CP" \
  source-code/bma/SignalProcessing.java source-code/BaseServer.java source-code/Main.java
```

Running `BaseServer` binds one port per active instance; verify startup prints
"listening on port" lines with no "already in use" / exception output.

## Classpath hazard

`build.sh` puts **all** jars on the classpath. Never commit two versions of the
same library (e.g. `jdsp-3.1.0.jar` *and* `jdsp-3.1.1.jar`) — duplicate classes
make resolution nondeterministic. Keep exactly one.
