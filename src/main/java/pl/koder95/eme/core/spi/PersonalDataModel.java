package pl.koder95.eme.core.spi;

/**
 * Interfejs zwraca dane osobowe przeznaczone do wyświetlenia.
 *
 * @author Kamil Jan Mularski [@Koder95]
 * @version 0.4.0, 2020-08-13
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
     * @return numer aktu chrztu, gdy {@code null} wtedy {@code '-'}
     */
    String getBaptismAN();
    /**
     * @return numer aktu bierzmowania, gdy {@code null} wtedy {@code '-'}
     */
    String getConfirmationAN();
    /**
     * @return numer aktu małżeństwa, gdy {@code null} wtedy {@code '-'}
     */
    String getMarriageAN();
    /**
     * @return numer aktu pogrzebu, gdy {@code null} wtedy {@code '-'}
     */
    String getDeceaseAN();
}
