# Propozycja refaktoryzacji: usunięcie paczki `dfs`

## Cel

Usunąć historyczną paczkę `pl.koder95.eme.dfs` i przenieść jej odpowiedzialności do bardziej semantycznych warstw:

- **domain/model** – czyste modele domenowe,
- **infrastructure/index** – I/O i parsery,
- **application/index** – przypadki użycia/reload,
- **core/spi** – kontrakty.

## Problem obecnego układu

Nazwa `dfs` nie komunikuje odpowiedzialności. W jednej paczce mieszają się:

- modele (`Book`, `Index`, `ActNumber`),
- infrastruktura (`IndexLoader`, `FileXmlIndexDataSource`),
- kontrakty (`IndexDataSource`, `IndexFilter`),
- enum pełniący rolę repozytorium i cache (`IndexList`).

To utrudnia testowanie, wymianę źródeł danych i rozwój architektury IoC/MVC.

## Docelowy podział pakietów

### 1) `pl.koder95.eme.domain.index`

Przenieść modele:

- `ActNumber`
- `Index`
- `Book`

Zasada: brak I/O i brak zależności od JavaFX.

### 2) `pl.koder95.eme.infrastructure.index`

Przenieść implementacje infrastrukturalne:

- `FileXmlIndexDataSource`
- `IndexLoader`

Dodać tu parsery/adaptory XML (jeśli będą potrzebne osobne klasy parsera).

### 3) `pl.koder95.eme.application.index`

Przenieść użycie przypadków użycia i repozytoriów:

- `IndexReloadService` (już istnieje w `application`, pozostaje i rozszerza się),
- nowy `ReloadableIndexRepository` + implementacja adapterowa.

### 4) `pl.koder95.eme.core.spi` (lub `domain.index.spi`)

Trzymać kontrakty:

- `IndexDataSource`
- `IndexFilter`
- nowy `IndexRepository` / `ReloadableIndexRepository`.

## Mapa przeniesień (klasa -> nowe miejsce)

- `dfs.ActNumber` -> `domain.index.ActNumber`
- `dfs.Index` -> `domain.index.Index`
- `dfs.Book` -> `domain.index.Book`
- `dfs.IndexDataSource` -> `core.spi.IndexDataSource` (lub `domain.index.spi`)
- `dfs.IndexFilter` -> `core.spi.IndexFilter` (lub `domain.index.spi`)
- `dfs.FileXmlIndexDataSource` -> `infrastructure.index.FileXmlIndexDataSource`
- `dfs.IndexLoader` -> `infrastructure.index.IndexLoader`
- `dfs.IndexList` -> zastąpić przez:
  - `application.index.ReloadableIndexRepository`
  - `infrastructure.index.IndexRepositoryImpl` (cache + reload)

## Strategia usunięcia `IndexList` (klucz do usunięcia całego `dfs`)

`IndexList` obecnie robi jednocześnie:

- identyfikację typów ksiąg,
- cache w pamięci,
- wywołanie loadera,
- częściowo logikę mapowania pól.

Docelowo rozbić to na:

1. `IndexType` (enum tylko typu księgi: baptism/confirmation/marriage/decease),
2. `IndexRepository` (odczyt),
3. `ReloadableIndexRepository` (przeładowanie),
4. `IndexFieldSchema` (opis pól dla typu księgi).

## Plan migracji iteracyjnej

1. **Wprowadzić nowe pakiety i klasy docelowe** (kopie klas z `dfs`, bez zmiany zachowania).
2. **Dodać adapter kompatybilności**: stary kod korzysta z nowych klas przez cienką warstwę.
3. **Przepiąć `IndexReloadService` i `IndexListDataSource`** na `ReloadableIndexRepository`/`IndexRepository`.
4. **Usunąć bezpośrednie użycia `dfs.*`** w `application`, `core`, `fx`.
5. **Zdeprecjonować paczkę `dfs`** i zostawić mostki tylko na 1 wersję.
6. **Usunąć `dfs` całkowicie** po zamknięciu migracji i aktualizacji importów.

## Minimalne kryteria zakończenia

- brak importów `pl.koder95.eme.dfs.*` w kodzie produkcyjnym,
- `IndexReloadService` działa przez interfejs repozytorium,
- XML loader działa z poziomu `infrastructure.index`,
- modele domenowe są niezależne od I/O i JavaFX.
