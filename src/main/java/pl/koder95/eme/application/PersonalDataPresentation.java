package pl.koder95.eme.application;

import pl.koder95.eme.core.spi.PersonalDataModel;

/**
 * Model prezentacyjny danych osobowych dla UI.
 */
public record PersonalDataPresentation(
        String surname,
        String name,
        String baptismAN,
        String confirmationAN,
        String marriageAN,
        String deceaseAN
) implements PersonalDataModel {

    public String fullName() {
        return name == null || name.isBlank() ? surname.toUpperCase() : surname.toUpperCase() + " " + name;
    }

    @Override
    public String getSurname() {
        return surname;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getBaptismAN() {
        return baptismAN;
    }

    @Override
    public String getConfirmationAN() {
        return confirmationAN;
    }

    @Override
    public String getMarriageAN() {
        return marriageAN;
    }

    @Override
    public String getDeceaseAN() {
        return deceaseAN;
    }
}
