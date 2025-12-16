package pl.koder95.eme.model;

import java.util.List;

/**
 * Indeks reprezentuje mapę elementów.
 * @param id identyfikator indeksu
 * @param elementList lista elementów niepowtarzających się,
 *                   powtórzenia zostaną zignorowane
 */
public record Index(String id, List<String> elementList) {

    public Index(String id, List<String> elementList) {
        this.id = id;
        this.elementList = elementList.stream().distinct().toList();
    }
}
