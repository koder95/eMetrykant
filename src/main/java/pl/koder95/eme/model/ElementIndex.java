package pl.koder95.eme.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Indeks reprezentuje mapę elementów.
 *
 * @param id identyfikator indeksu
 * @param elementList lista elementów niepowtarzających się;
 *                   powtórzenia zostaną zignorowane
 */
public record ElementIndex(String id, List<String> elementList) {

    public ElementIndex(String id, List<String> elementList) {
        this.id = id;
        this.elementList = new ArrayList<>(elementList.stream().distinct().toList());
    }
}
