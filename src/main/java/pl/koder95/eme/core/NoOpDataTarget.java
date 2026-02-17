package pl.koder95.eme.core;

import pl.koder95.eme.core.spi.DataTarget;
import pl.koder95.eme.domain.index.ActNumber;

/**
 * Pusty cel danych używany, gdy zapis nie jest wymagany.
 */
public class NoOpDataTarget implements DataTarget {

    @Override
    public void setBaptism(String surname, String name, ActNumber an) {
        // no-op
    }

    @Override
    public void setConfirmation(String surname, String name, ActNumber an) {
        // no-op
    }

    @Override
    public void setMarriage(String surname, String name, ActNumber an) {
        // no-op
    }

    @Override
    public void setDecease(String surname, String name, ActNumber an) {
        // no-op
    }
}
