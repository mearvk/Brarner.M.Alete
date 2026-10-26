---
inclusion: always
---

# Git workflow

## Default push target

Work is delivered to the default branch **`master`**, and **`main` is kept in
sync with `master`**. Unless explicitly asked otherwise:

- Commit the change set on `master` (via a short-lived branch + PR, or directly
  when that is the requested flow), then bring `main` up to the same content.
- `main` carries two files that exist only there — `LEGAL.READ.ME.md` and
  `LICENSE.legal` — plus otherwise-identical content to `master`. Preserve those
  two files whenever you update `main`.
- Do not open PRs against, or diverge, `main` from `master` by default.

## Hygiene rules (enforced by `.gitignore`)

Never commit, and keep ignored:

- build output (`out/`, `build/classes/`), IDE config (`.idea/`),
  JVM crash dumps (`hs_err_pid*.log`);
- large/duplicate binaries: the nested JavaFX SDK copy, `javafx-jdk.zip`,
  `jfxwebkit.dll`, the vendored `openjfx-*-javadoc/`, and any stale jDSP artifact.

Keep exactly one copy of each third-party library. If a vendored binary is large
and reproducible (SDK zip, generated javadoc), prefer not committing it at all.

## Commits & PRs

- Write a descriptive commit body explaining the *why*, not just the *what*.
- Stage specific files; avoid `git add -A` when unrelated changes are present.
- Keep documentation (`README.md`, `STRUCTURE.txt`) in step with structural
  changes in the same change set.
