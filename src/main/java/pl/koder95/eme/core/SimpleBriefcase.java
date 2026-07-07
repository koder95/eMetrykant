package pl.koder95.eme.core;

import pl.koder95.eme.core.spi.Briefcase;
import pl.koder95.eme.domain.index.ActNumber;

/**
 * Prosta implementacja interfejsu <i>{@link Briefcase aktówki}</i>,
 * która definiuje cztery tablice do przechowywania czterech typów
 * numerów aktów.
 *
 * @author Kamil Jan Mularski [@Koder95]
 * @version 0.4.0, 2020-08-26
 * @since 0.4.0
 */
public class SimpleBriefcase implements Briefcase {

    private final ActNumber[] baptism, confirmation, marriage, decease;

    /**
     * Tworzy prostą aktówkę zawierającą podane tablice numerów aktów.
     *
     * @param baptism numery aktów chrztu
     * @param confirmation numery aktów bierzmowania
     * @param marriage numery aktów małżeństwa
     * @param decease numery aktów zgonu
     */
    public SimpleBriefcase(ActNumber[] baptism, ActNumber[] confirmation,
                            ActNumber[] marriage, ActNumber[] decease) {
        this.baptism = baptism == null? new ActNumber[0] : baptism;
        this.confirmation = confirmation == null? new ActNumber[0] : confirmation;
        this.marriage = marriage == null? new ActNumber[0] : marriage;
        this.decease = decease == null? new ActNumber[0] : decease;
    }

    @Override
    public ActNumber[] getBaptism() {
        return baptism;
    }

    @Override
    public ActNumber[] getConfirmation() {
        return confirmation;
    }

    @Override
    public ActNumber[] getMarriage() {
        return marriage;
    }

    @Override
    public ActNumber[] getDecease() {
        return decease;
    }

    @Override
    public String toString() {
        return "[" + baptism + ";" + confirmation + ";" + marriage + ";" + decease + "]";
    }
}
