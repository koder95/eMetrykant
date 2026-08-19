package pl.koder95.eme.domain.index;

import lombok.Builder;

import java.util.List;

/**
 * Niemutowalny szablon księgi: typ, nazwy pól oraz sekcje widoku.
 */
@Builder(builderClassName = "Builder")
public record BookTemplate(BookType bookType, List<String> fieldNames, List<Section> sections) {

    public BookTemplate {
        if (bookType == null) {
            throw new IllegalArgumentException("bookType cannot be null");
        }
        if (fieldNames == null) {
            fieldNames = List.of();
        }
        if (sections == null) {
            sections = List.of();
        }
        fieldNames = List.copyOf(fieldNames);
        sections = List.copyOf(sections);
    }

    @lombok.Builder(builderClassName = "Builder")
    public record Section(String id, int startIndex, int endIndex, String title) {
        public Section {
            if (id == null || id.isBlank()) {
                throw new IllegalArgumentException("id cannot be null or blank");
            }
            if (startIndex < 0 || endIndex < 0) {
                throw new IllegalArgumentException("startIndex and endIndex must non-negative");
            }
            if (startIndex > endIndex) {
                throw new IllegalArgumentException("startIndex cannot be greater than endIndex");
            }
            title = title == null ? "" : title;
        }
    }
}
