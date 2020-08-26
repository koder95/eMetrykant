/**
 * Paczka zawiera interfejsy najważniejsze dla programu, ponieważ tworzą one system
 * zarządzania informacjami.
 *
 * <br/>
 * <h3>Przykład użycia:</h3>
 * Wczytywanie danych:
 * <pre>
 *     CabinetAnalyzer worker = ...;
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
package pl.koder95.eme.core.spi;