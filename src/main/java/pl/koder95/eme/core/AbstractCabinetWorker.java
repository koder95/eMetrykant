package pl.koder95.eme.core;

import pl.koder95.eme.core.spi.*;
import pl.koder95.eme.dfs.ActNumber;

/**
 * Pracownik szafy ze zdefiniowaną szafą aktową. Umożliwia także ustawienie odpowiedniego źródła danych
 * oraz ich celu. Nie implementuje metody {@link #createBriefcase(ActNumber[], ActNumber[], ActNumber[], ActNumber[])},
 * która ma wytwarzać obiekty przechowywane w szafie.
 *
 * @author Kamil Jan Mularski [@Koder95]
 * @version 0.4.0, 2020-08-26
 * @since 0.4.0
 */
public abstract class AbstractCabinetWorker implements CabinetWorker {

    private final FilingCabinet cabinet;

    private DataSource source;
    private DataTarget target;

    /**
     * Tworzy obiekt określając wszystkie jego elementy.
     *
     * @param cabinet szafa aktowa
     * @param source źródło danych
     * @param target cel danych
     */
    public AbstractCabinetWorker(FilingCabinet cabinet, DataSource source, DataTarget target) {
        this.cabinet = cabinet;
        this.source = source;
        this.target = target;
    }

    /**
     * Tworzy obiekt określając jedynie z jakiej szafy ma korzystać. Resztę elementów można
     * określić za pomocą metod {@link #setDataSource(DataSource)} i {@link #setDataTarget(DataTarget)}.
     *
     * @param cabinet szafa aktowa
     */
    public AbstractCabinetWorker(FilingCabinet cabinet) {
        this(cabinet, null, null);
    }

    @Override
    public FilingCabinet getCabinet() {
        return cabinet;
    }

    @Override
    public DataSource getDataSource() {
        return source;
    }

    /**
     * Ustawia źródło danych.
     * @param source źródło wykorzystywane do pobierania danych
     */
    public void setDataSource(DataSource source) {
        this.source = source;
    }

    @Override
    public DataTarget getDataTarget() {
        return target;
    }

    /**
     * Ustawia cel danych.
     * @param target cel danych wykorzystywane do zapisywania danych
     */
    public void setDataTarget(DataTarget target) {
        this.target = target;
    }
}
