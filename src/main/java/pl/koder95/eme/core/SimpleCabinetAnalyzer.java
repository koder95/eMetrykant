package pl.koder95.eme.core;

import pl.koder95.eme.core.spi.Briefcase;
import pl.koder95.eme.core.spi.DataSource;
import pl.koder95.eme.core.spi.DataTarget;
import pl.koder95.eme.core.spi.FilingCabinet;
import pl.koder95.eme.dfs.ActNumber;

/**
 * Rozszerza {@link AbstractCabinetAnalyzer ogólnego analizatora szafy}
 * określając tworzenie {@link Briefcase aktówek} za pomocą
 * {@link SimpleBriefcase#SimpleBriefcase(ActNumber[], ActNumber[], ActNumber[], ActNumber[])
 * konstruktora prostej aktówki}. Klasa również przy tworzeniu nowych instancji
 * domaga się podania {@link SuggestionProvider dostawcy sugestii} w konstruktorze.
 * Korzysta przez to ze zdefiniowanego w nim konwertera oraz samego interfejsu dostawcy
 * sugestii.
 *
 * @author Kamil Jan Mularski [@Koder95]
 * @version 0.4.0, 2020-08-26
 * @since 0.4.0
 */
public class SimpleCabinetAnalyzer extends AbstractCabinetAnalyzer {

    /**
     * Tworzy obiekt określając wszystkie jego elementy składowe.
     *
     * @param cabinet szafa aktowa
     * @param source źródło danych
     * @param target cel danych
     * @param provider dostawca sugestii
     */
    public SimpleCabinetAnalyzer(FilingCabinet cabinet, DataSource source,
                                 DataTarget target, SuggestionProvider provider) {
        super(cabinet, source, target, provider, provider.getConverter());
    }

    /**
     * Tworzy obiekt określając konieczne jego elementy składowe.
     *
     * @param cabinet szafa aktowa
     * @param provider dostawca sugestii
     */
    public SimpleCabinetAnalyzer(FilingCabinet cabinet, SuggestionProvider provider) {
        super(cabinet, provider, provider.getConverter());
    }

    @Override
    public Briefcase createBriefcase(ActNumber[] baptism, ActNumber[] confirmation,
                                     ActNumber[] marriage, ActNumber[] decease) {
        return new SimpleBriefcase(baptism, confirmation, marriage, decease);
    }
}
