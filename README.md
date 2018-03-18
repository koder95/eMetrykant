# eMetrykant ![churchoval32](https://user-images.githubusercontent.com/9617256/29318380-09eff3be-81d0-11e7-91df-2b651c8eba94.png) ![eme32](https://user-images.githubusercontent.com/9617256/29318628-3f54a94a-81d1-11e7-9fdb-92d5d1631a10.png)
Program do odczytywania i przeszukiwania bazy danych dot. metryk parafialnych.
- Księgi zawsze pod ręką:

  ![screen0](https://user-images.githubusercontent.com/9617256/29740482-fef27334-8a57-11e7-87bc-a8d3789f3412.png)

- Nie musisz przeglądać całych ksiąg w poszukiwaniu konkretnego indeksu. Teraz wystarczy, że wpiszesz nazwisko i imię (imiona) a znajdziesz numer do konkretnego aktu.

  ![screen1](https://user-images.githubusercontent.com/9617256/29740483-fef3dff8-8a57-11e7-8639-5396ebcf3e18.png)

- Znajdziesz nawet akt małżeński.

  ![screen2](https://user-images.githubusercontent.com/9617256/29740481-feef37e6-8a57-11e7-919d-5f2f6f804921.png)

## Zestaw przykładowych danych
Aby program działał poprawnie, należy w tym miejscu, gdzie on się znajduje, umieścić również folder _data_ z danymi do wczytania. Dane te są w formie 4 plików o rozszerzeniu CSV. Można pobrać paczkę z przykładowymi danymi, które nie są rzeczywiste.

## Plany
Pierwsza stabilna wersja programu (v1.0.0), będzie programem, który w pełni pozwoliłby na zarządzaniem metrykami parafialnymi. Wersja alfa jest podstawową wersją, do której dodawane będą dodatkowe możliwości:

### Wersja 0.2
- Wczytywanie danych z pliku XML. (_ZROBIONE_)
- Konwerter danych z CSV do formatu XML. (_ZROBIONE_)
- Uruchamianie aplikacji w oparciu o wzorzec _Chain of Responsibility_ (łańcuch odpowiedzialności). (_ZROBIONE_)
- Używanie szablonów przez GUI.

### Wersja 0.3
- Podpowiedzi wyszukiwania.
- Wyszukiwanie w księdze zaślubionych po płci.

### Wersja 0.4
- Modernizacja GUI.
- Dodanie możliwości edycji bazy indeksów.
