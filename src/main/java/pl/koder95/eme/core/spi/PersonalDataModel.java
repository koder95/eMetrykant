package pl.koder95.eme.core.spi;

/**
 * Interfejs zwraca dane osobowe przeznaczone do wyświetlenia.
 *
 * @author Kamil Jan Mularski [@Koder95]
 * @version 0.4.0, 2020-08-26
 * @since 0.4.0
 */
public interface PersonalDataModel {

    /**
     * @return nazwisko osoby, której dotyczą dane, gdy {@code null} wtedy {@code '?'}
     */
    String getSurname();
    /**
     * @return imię / imiona osoby, której dotyczą dane, gdy {@code null} wtedy {@code '?'}
     */
    String getName();

    /**
     * @return numery aktów chrztu, albo {@code '-'} jeśli nie ma żadnych
     */
    String getBaptismAN();
    /**
     * @return numery aktów bierzmowania, albo {@code '-'} jeśli nie ma żadnych
     */
    String getConfirmationAN();
    /**
     * @return numery aktów małżeństwa, albo {@code '-'} jeśli nie ma żadnych
     */
    String getMarriageAN();
    /**
     * @return numery aktów pogrzebu, albo {@code '-'} jeśli nie ma żadnych
     */
    String getDeceaseAN();
}
