/**
 * Paczka zawiera klasy najważniejsze dla programu, ponieważ definiujące jego system
 * zarządzania informacjami.
 *
 * <h3>Przykład użycia:</h3>
 * Wczytywanie danych:
 * <pre>
 *     FilingCabinet cabinet = new TreeFilingCabinet();
 *     IndexListDataSource source = new IndexListDataSource();
 *     SuggestionProvider suggestionProvider = new SuggestionProvider(cabinet);
 *     CabinetAnalyzer worker = new SimpleCabinetAnalyzer(cabinet, source, null, suggestionProvider);
 *     CabinetWorkers.register(CabinetAnalyzer.class, worker);
 *     worker.load();
 * </pre>
 * Odczytywanie danych:
 * <pre>
 *     Briefcase briefcase = worker.getCabinet().get("Kowalski", "Jan");
 *     ActNumber[] deceases = briefcase.getDecease();
 * </pre>
 * Załączanie automatycznych sugestii:
 * <pre>
 *     CabinetAnalyzer analyzer = CabinetWorkers.get(CabinetAnalyzer.class);
 *     AutoCompletionBinding<PersonalDataModel> autoCompletionBinding = TextFields.bindAutoCompletion(
 *          new TextField(),
 *          analyzer.getSuggestionProvider(),
 *          analyzer.getPersonalDataConverter()
 *     );
 * </pre>
 */
package pl.koder95.eme.core;