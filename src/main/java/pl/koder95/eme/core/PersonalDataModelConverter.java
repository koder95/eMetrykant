package pl.koder95.eme.core;

import javafx.util.StringConverter;
import pl.koder95.eme.core.spi.Briefcase;
import pl.koder95.eme.core.spi.FilingCabinet;
import pl.koder95.eme.core.spi.PersonalDataModel;
import pl.koder95.eme.dfs.ActNumber;

import java.util.Arrays;
import java.util.function.Function;

/**
 * Konwerter, który zamienia {@link String ciągi znaków} w {@link PersonalDataModel modele informacji osobistych}
 * i {@link PersonalDataModel modele informacji osobistych} w {@link String ciągi znaków} wykorzystując
 * {@link FilingCabinet szafę aktową}.
 *
 * @author Kamil Jan Mularski [@Koder95]
 * @version 0.4.0, 2020-08-26
 * @since 0.4.0
 */
public class PersonalDataModelConverter extends StringConverter<PersonalDataModel> {

    private final FilingCabinet cabinet;

    /**
     * Tworzy nowy konwerter korzystający z instancji {@link FilingCabinet szafy aktowej}.
     * @param cabinet szafa aktowa, z której konwerter będzie pobierał informacje
     */
    public PersonalDataModelConverter(FilingCabinet cabinet) {
        this.cabinet = cabinet;
    }

    @Override
    public String toString(PersonalDataModel object) {
        return object.getSurname() + " " + object.getName();
    }

    @Override
    public PersonalDataModel fromString(String string) {
        if (string != null && !string.isEmpty()) {
            if (string.contains(" ")) {
                string = capitalize(string);
                String beforeSpace = string.substring(0, string.indexOf(' '));
                String afterSpace = string.substring(string.indexOf(' ') + 1);
                return fromBriefcase(beforeSpace, afterSpace);
            }
        }
        return null;
    }

    private String capitalize(String string) {
        String[] words = string.split(" ");
        StringBuilder builder = Arrays.stream(words).map(word -> {
            String transformed = word.toLowerCase();
            char first = transformed.charAt(0);
            return Character.toUpperCase(first) + transformed.substring(1);
        }).reduce(new StringBuilder(), (b, s) -> b.append(s).append(" "), StringBuilder::append);
        builder.setLength(string.length());
        string = builder.toString();
        if (string.contains("_")) {
            words = string.split("_");
            builder = Arrays.stream(words).map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1))
                    .reduce(new StringBuilder(), (b, s) -> b.append(s).append("_"), StringBuilder::append);
            builder.setLength(string.length());
            string = builder.toString();
        }
        return string;
    }

    private PersonalDataModel fromBriefcase(String surname, String name) {
        Briefcase b = cabinet.get(surname, name);
        if (b == null) b = cabinet.get(name, surname);
        Briefcase briefcase = b;
        return new Model(surname, name, briefcase);
    }

    private static class Model implements PersonalDataModel {

        private final String surname, name, ban, can, man, dan;

        private Model(String surname, String name, ActNumber[] ban, ActNumber[] can,
                      ActNumber[] man, ActNumber[] dan) {
            this.surname = surname;
            this.name = name;
            this.ban = format(ban);
            this.can = format(can);
            this.man = format(man);
            this.dan = format(dan);
        }

        private Model(String surname, String name, Briefcase briefcase) {
            this(surname, name,
                    normalize(briefcase, Briefcase::getBaptism),
                    normalize(briefcase, Briefcase::getConfirmation),
                    normalize(briefcase, Briefcase::getMarriage),
                    normalize(briefcase, Briefcase::getDecease)
            );
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
            return ban;
        }

        @Override
        public String getConfirmationAN() {
            return can;
        }

        @Override
        public String getMarriageAN() {
            return man;
        }

        @Override
        public String getDeceaseAN() {
            return dan;
        }

        private static ActNumber[] normalize(Briefcase briefcase, Function<Briefcase, ActNumber[]> select) {
            return briefcase == null? new ActNumber[0] : select.apply(briefcase);
        }

        private static String format(ActNumber[] an) {
            return an.length == 0 ? "-" : an.length == 1 ? an[0].toString() : Arrays.deepToString(an);
        }
    }
}
