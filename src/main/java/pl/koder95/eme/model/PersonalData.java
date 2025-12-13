package pl.koder95.eme.model;

import pl.koder95.eme.core.spi.PersonalDataModel;

public record PersonalData(String surname, String name,
                           String ban, String can, String man, String dan)
        implements PersonalDataModel {
    private static final String NOMEN = "N.";
    private static final String EAN = "–";

    @Override
    public String getSurname() {
        return surname == null || surname.isBlank() ? NOMEN : surname;
    }

    @Override
    public String getName() {
        return name == null || name.isBlank() ? NOMEN : name;
    }

    @Override
    public String getBaptismAN() {
        return ban == null || ban.isBlank() ? EAN : ban;
    }

    @Override
    public String getConfirmationAN() {
        return can == null || can.isBlank() ? EAN : can;
    }

    @Override
    public String getMarriageAN() {
        return man == null || man.isBlank() ? EAN : man;
    }

    @Override
    public String getDeceaseAN() {
        return dan == null || dan.isBlank() ? EAN : dan;
    }
}
