# Propozycja refaktoryzacji z naciskiem na IoC i MVC

## 1. Diagnoza obecnej architektury

Największe miejsca sprzężenia i utrudnionego testowania:

- `App` tworzy i łączy zależności ręcznie (`TreeFilingCabinet`, `IndexListDataSource`, `SuggestionProvider`, `SimpleCabinetAnalyzer`) oraz rejestruje je globalnie przez `CabinetWorkers`. To jest forma *Service Locator*, która utrudnia kontrolę cyklu życia obiektów.
- `PersonalDataView` pobiera serwis przez statyczne `CabinetWorkers.get(...)`, a jednocześnie zawiera sporo logiki prezentacji i akcji (wyszukiwanie, reload danych, zamknięcie aplikacji).
- `Main` trzyma stałe globalne (bundle, locale, collator), przez co kontekst konfiguracji jest rozproszony i trudniejszy do podmiany w testach.

## 2. Cel refaktoryzacji

- Wprowadzić **IoC** (Inversion of Control) przez jawne wstrzykiwanie zależności.
- Uporządkować **MVC**:
  - **Model**: domena + usługi (`core`, `dfs`, `git`) bez zależności od JavaFX.
  - **View**: FXML + elementy UI.
  - **Controller**: cienka warstwa UI delegująca do serwisów aplikacyjnych.
- Zmniejszyć globalny stan i zależności statyczne.

## 3. Docelowa struktura modułów/pakietów

Przykładowy podział (bez zmiany funkcjonalnej):

- `pl.koder95.eme.bootstrap`
  - `AppLauncher` (start aplikacji i złożenie grafu zależności)
  - `AppConfig` (bundle, locale, strategia porównań)
- `pl.koder95.eme.application`
  - `PersonalDataQueryService`
  - `IndexReloadService`
  - `UpdateService`
- `pl.koder95.eme.ui.fx`
  - `PersonalDataController` (kontroler FXML)
  - `FxDialogs` (fabryka dialogów)
- `pl.koder95.eme.domain` / `core`
  - istniejące interfejsy i implementacje (`CabinetAnalyzer`, `FilingCabinet`, `DataSource`)

## 4. Plan wdrożenia IoC (bez ciężkiego frameworka)

### Krok A: Kompozycja zależności w jednym miejscu

- Dodać klasę `ApplicationContext` (prosty kontener ręczny), która:
  - tworzy `FilingCabinet`, `DataSource`, `SuggestionProvider`, `CabinetAnalyzer`;
  - udostępnia je przez metody instancyjne (bez statycznych pól).
- `App` korzysta wyłącznie z `ApplicationContext`.

### Krok B: Usunąć `CabinetWorkers` z UI

- Zmienić kontroler FXML tak, by zależności trafiały do niego przez:
  - konstruktor (`FXMLLoader#setControllerFactory`) albo
  - jawny setter wywołany po załadowaniu FXML.

### Krok C: Wyodrębnić serwisy aplikacyjne

- Z `PersonalDataView` wynieść logikę do:
  - `PersonalDataQueryService` (mapowanie danych osobowych na view-model),
  - `IndexReloadService` (reload indeksów),
  - `AppCloseService` (obsługa scenariusza zamknięcia).

Efekt: kontroler tylko pobiera dane z pól i deleguje akcje.

## 5. Plan wdrożenia MVC

### Controller (UI)

`PersonalDataController` odpowiada za:
- bindowanie zdarzeń JavaFX;
- aktualizację widoku (labelki, dialogi);
- wywołania metod serwisów.

### Model

Model domenowy pozostaje w `core/spi` + `dfs`. Dla UI warto dodać:
- `PersonalDataViewModel` (readonly properties / DTO),
- mapper z `PersonalDataModel` -> `PersonalDataViewModel`.

### View

FXML bez logiki biznesowej. Widok wyłącznie deklaratywny.

## 6. Migracja krok po kroku (bezpieczna)

1. Dodać `ApplicationContext` i przepiąć `App` na nowy sposób tworzenia obiektów.
2. Umożliwić wstrzykiwanie zależności do kontrolera FXML.
3. Przenieść logikę `reload` do `IndexReloadService`.
4. Przenieść logikę wyszukiwania/autocomplete do `PersonalDataQueryService`.
5. Ograniczyć `Main` do samego bootstrappingu (usuwać globalne stałe krokowo).
6. Usunąć/wycofać `CabinetWorkers` po pełnej migracji.

## 7. Korzyści po refaktoryzacji

- Łatwiejsze testy jednostkowe kontrolerów i serwisów.
- Mniejsze sprzężenie między JavaFX i logiką domenową.
- Jasny punkt kompozycji zależności (IoC) i czytelny przepływ MVC.
- Mniej ryzyka efektów ubocznych przez globalny stan statyczny.

## 8. Minimalny przykład docelowego przepływu

- `AppLauncher` tworzy `ApplicationContext`.
- `FXMLLoader` dostaje `controllerFactory`, która tworzy `PersonalDataController(analyzer, reloadService, dialogs)`.
- Kontroler odbiera akcję użytkownika i wywołuje serwis.
- Serwis zwraca model/DTO, kontroler aktualizuje widok.

To podejście daje IoC bez zależności od Spring/Guice i jest kompatybilne z obecną strukturą projektu.

## 9. Co zostało do zrobienia (status po wdrożonych zmianach)

### Zrobione

- [x] Dodany `ApplicationContext` jako punkt kompozycji zależności.
- [x] Wstrzykiwanie zależności do kontrolera FXML przez konstruktor i `controllerFactory`.
- [x] Wyodrębnienie serwisów aplikacyjnych z `PersonalDataView`:
  - [x] `PersonalDataQueryService`
  - [x] `IndexReloadService`
  - [x] `AppCloseService`
  - [x] model prezentacyjny `PersonalDataPresentation`
- [x] Usunięty wyciek `CabinetAnalyzer` z API `PersonalDataQueryService`.
- [x] Wydzielona fabryka dialogów (`FxDialogs`) dla `Alert`/`Dialog`.
- [x] Dodany rekord konfiguracji aplikacji (`AppConfig`: `ResourceBundle`, `Locale`, `Collator`).
- [x] Wydzielone `IndexLoader` oraz `IndexDataSource` (I/O poza obiektami modelu).
- [x] Dodany punkt rozszerzeń `IndexFilter` dla reguł filtrowania rekordów.

### Do zrobienia

- [ ] Zaktualizować dokumentację pakietów (`package-info`) tak, by odzwierciedlała nową ścieżkę IoC/MVC.
- [ ] Dodać testy jednostkowe:
  - [ ] `PersonalDataQueryService` (mapowanie null/pełnych danych)
  - [ ] `IndexReloadService` (iteracja po `IndexList`)
  - [ ] zachowanie kontrolera z mockowanymi serwisami
- [ ] Rozważyć zmianę nazwy kontrolera z `PersonalDataView` na `PersonalDataController`
      (dla spójności z MVC) i aktualizację FXML.
- [ ] Po pełnym domknięciu migracji usunąć legacy artefakty i komentarze opisujące dawny `Service Locator`.

## 10. Propozycja refaktoryzacji pakietu `dfs` zgodnie z SOLID

### S — Single Responsibility Principle

- Rozdzielić odpowiedzialności klas `IndexList` i `IndexContainer`:
  - loader danych (`IndexLoader`),
  - cache (`IndexCache`),
  - parser rekordów (`IndexRecordParser`).
- Usunąć logikę I/O z obiektów modelu (`Book`, `Index`, `ActNumber`) — modele powinny pozostać czystymi strukturami danych.

### O — Open/Closed Principle

- Wprowadzić interfejs strategii ładowania, np. `IndexDataSource` (XML, ZIP, pamięć, test fixture),
  aby dodawać nowe źródła bez modyfikowania istniejących klas domenowych.
- Dodać `IndexFilter`/`IndexPredicate` jako punkt rozszerzeń dla reguł filtrowania rekordów.

### L — Liskov Substitution Principle

- Jeśli `IndexList` pozostanie `enum`, ograniczyć dziedziczenie i różnice zachowania między wartościami enum.
  Lepsza opcja: zastąpić enum klasami implementującymi wspólny kontrakt `IndexType`.

### I — Interface Segregation Principle

- Podzielić szerokie API na mniejsze kontrakty:
  - `ReadableIndexRepository`,
  - `WritableIndexRepository`,
  - `ReloadableIndexRepository`.
- UI i serwisy aplikacyjne powinny zależeć tylko od interfejsów, których realnie używają.

### D — Dependency Inversion Principle

- `IndexReloadService` powinien zależeć od abstrakcji repozytorium (np. `ReloadableIndexRepository`),
  a nie bezpośrednio od `IndexList.values()`.
- Konkrety (`XMLLoader`, format plików, źródła ZIP) podłączać wyłącznie w `ApplicationContext`.

### Sugerowana migracja techniczna `dfs`

1. [x] Dodać abstrakcję źródła danych `IndexDataSource` oraz `IndexLoader` i przenieść tam I/O XML.
2. [x] Dodać `IndexFilter` jako rozszerzalny punkt filtrowania rekordów.
3. [ ] Dodać interfejs `ReloadableIndexRepository` i adapter na aktualne `IndexList`.
4. [ ] Przepiąć `IndexReloadService` na nowy interfejs.
5. [ ] Dopiero na końcu upraszczać/wycofywać stare API oparte o enum/statyki.
