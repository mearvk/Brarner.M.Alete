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

Two equivalent builds produce the same runnable `Brarner.M.Alete.jar`
(`Main-Class: Main`), both scoped to the **engine only** — they compile `bma/`,
`Main`, the master `BaseServer` and the per-module `BaseServer`s, and exclude the
optional `ai/` (DJL) and `video/` (JavaFX) trees. Only `jars/signal-processing/*`
is placed on the classpath.

```bash
./build.sh        # Linux/macOS
build.bat         # Windows (uses jars\signal-processing, recursive classpath)
```

Maven builds the same scope via the `source-code/` module (registered in the root
`pom.xml` aggregator). `source-code/pom.xml` sets `sourceDirectory` to
`source-code/`, excludes `ai/**` and `video/**`, and depends on the bundled jDSP /
commons-math3 jars with `system` scope so the build needs no network:

```bash
mvn -pl source-code -am package   # -> source-code/target/Brarner.M.Alete.jar
```

Keep the three build definitions in step: same source scope, same jDSP version,
same main class.

## Quick compile / smoke test (engine only)

```bash
CP=$(find jars/signal-processing -name '*.jar' | tr '\n' ':')
javac -d build/classes -cp "$CP" \
  source-code/bma/SignalProcessing.java source-code/BaseServer.java source-code/Main.java
```

Running `BaseServer` binds one port per active instance; verify startup prints
"listening on port" lines with no "already in use" / exception output.

## Dependency jar hazards

- **One version per library.** Never commit two versions of the same jar (e.g.
  `jdsp-3.1.0.jar` *and* `jdsp-3.1.1.jar`) — duplicate classes make resolution
  nondeterministic. Keep exactly one.
- **No fake jars.** A failed download can leave a tiny file with a `.jar` name
  whose contents are actually an HTML "404 Not Found" page; `javac` then fails
  with `zip END header not found`. Two such files were removed from `jars/ai/`.
  If a jar is only a few hundred bytes or begins with `<html`, it is broken —
  re-fetch it, do not commit it. Quick check:
  `for j in $(find jars -name '*.jar'); do head -c6 "$j" | grep -q '<html' && echo "BROKEN $j"; done`
