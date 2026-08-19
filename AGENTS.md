# AGENTS.md

## Cursor Cloud specific instructions

eMetrykant is a **JavaFX 21 desktop application** (Java 21 + Maven) for browsing and
searching a Polish parish records database (baptism / confirmation / marriage / death
books). Entry point is `pl.koder95.eme.Main`; the UI is defined in FXML under
`src/main/resources/pl/koder95/eme/fx/`. There is no server/backend — it is a single
GUI app.

### Build / test / lint

- Build (also serves as the compile check): `mvn -DskipTests package`. Artifacts land in
  `target/` (`eMetrykant-<version>.jar` plus dependencies copied into `target/lib/`).
- The `maven-javadoc-plugin` runs during `package` and requires `JAVA_HOME` to be set.
  `JAVA_HOME` is exported in `~/.bashrc` (points at `/usr/lib/jvm/java-21-openjdk-amd64`);
  if you invoke Maven from a context that does not source it, prefix the command with
  `JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64`.
- There are **no automated tests** and **no linter** configured in this repo, so `package`
  is the primary correctness gate.

### Running the app (GUI)

- A desktop is available on `DISPLAY=:1` (TigerVNC). Export `DISPLAY=:1` before launching.
- Run with: `java -jar target/eMetrykant-<version>.jar`. The startup warning
  `Unsupported JavaFX configuration: classes were loaded from 'unnamed module ...'` is
  harmless (the jar runs from the classpath, not the module path).
- The app reads `indices.xml` from the **current working directory** (`user.dir`). If the
  file is absent it silently creates an empty one, so the window will show 0 acts. To have
  data to browse, launch from a directory that contains a populated `indices.xml`.
- On startup the app checks GitHub for a newer release (`RepositoryInfo`); if one is found
  (or CLI args are passed) it shows a self-update window instead of the main window.

### Data model / sample data

- Sample records ship as CSV inside `data - przykładowe dane.zip` (Polish: "sample data"),
  but the app consumes **XML** (`indices.xml`), not CSV.
- `indices.xml` shape: `<indices><book name="..."><index .../></book>...</indices>`.
  Book names come from `BookType` (e.g. `Księga ochrzczonych`). Each `<index>` needs an
  `an` attribute in `sign/year` form (e.g. `1000/1998`); baptism/confirmation/death indices
  use `name`/`surname`, marriage indices use `husband-*`/`wife-*`.
- To turn the sample CSVs into a usable `indices.xml`, map columns
  `surname;name;number;year` (marriage: `husband-surname;husband-name;wife-surname;wife-name;number;year`)
  into index attributes with `an="number/year"`.
