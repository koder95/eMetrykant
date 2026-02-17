package pl.koder95.eme.bootstrap;

import pl.koder95.eme.application.AppCloseService;
import pl.koder95.eme.application.IndexReloadService;
import pl.koder95.eme.application.PersonalDataQueryService;
import pl.koder95.eme.Main;
import pl.koder95.eme.fx.FxDialogs;
import pl.koder95.eme.core.IndexListDataSource;
import pl.koder95.eme.core.NoOpDataTarget;
import pl.koder95.eme.core.SimpleCabinetAnalyzer;
import pl.koder95.eme.core.SuggestionProvider;
import pl.koder95.eme.core.TreeFilingCabinet;
import pl.koder95.eme.core.spi.CabinetAnalyzer;
import pl.koder95.eme.core.spi.FilingCabinet;

/**
 * Prosty kontener IoC aplikacji.
 *
 * <p>Skupia tworzenie i utrzymywanie zależności w jednym miejscu,
 * aby ograniczyć tworzenie obiektów w warstwie UI.</p>
 */
public class ApplicationContext {

    private final FilingCabinet cabinet;
    private final IndexListDataSource indexListDataSource;
    private final SuggestionProvider suggestionProvider;
    private final CabinetAnalyzer cabinetAnalyzer;
    private final PersonalDataQueryService personalDataQueryService;
    private final IndexReloadService indexReloadService;
    private final AppConfig appConfig;
    private final FxDialogs dialogs;
    private final AppCloseService appCloseService;
    private boolean initialized;

    public ApplicationContext() {
        this.cabinet = new TreeFilingCabinet();
        this.indexListDataSource = new IndexListDataSource();
        this.suggestionProvider = new SuggestionProvider(cabinet);
        this.cabinetAnalyzer = new SimpleCabinetAnalyzer(
                cabinet,
                indexListDataSource,
                new NoOpDataTarget(),
                suggestionProvider
        );
        this.personalDataQueryService = new PersonalDataQueryService(cabinetAnalyzer);
        this.indexReloadService = new IndexReloadService();
        this.appConfig = new AppConfig(Main.BUNDLE, Main.POLISH, Main.DEFAULT_COLLATOR);
        this.dialogs = new FxDialogs();
        this.appCloseService = new AppCloseService(appConfig, dialogs);
        this.initialized = false;
    }

    public CabinetAnalyzer getCabinetAnalyzer() {
        ensureInitialized();
        return cabinetAnalyzer;
    }

    public PersonalDataQueryService getPersonalDataQueryService() {
        ensureInitialized();
        return personalDataQueryService;
    }

    public IndexReloadService getIndexReloadService() {
        return indexReloadService;
    }

    public AppConfig getAppConfig() {
        return appConfig;
    }

    public FxDialogs getDialogs() {
        return dialogs;
    }

    public AppCloseService getAppCloseService() {
        return appCloseService;
    }

    /**
     * Inicjalizuje kontekst po konstrukcji (wywoływane np. z {@code App.init()}).
     *
     * @throws IllegalStateException gdy kontekst został już zainicjalizowany
     */
    public void initialize() {
        if (initialized) {
            throw new IllegalStateException("ApplicationContext został już zainicjalizowany.");
        }
        cabinetAnalyzer.load();
        initialized = true;
    }

    private void ensureInitialized() {
        if (!initialized) {
            throw new IllegalStateException("ApplicationContext musi zostać zainicjalizowany przez initialize().");
        }
    }
}
