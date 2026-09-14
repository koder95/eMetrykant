package pl.koder95.eme.io.csv;

import pl.koder95.eme.domain.index.ActNumber;
import pl.koder95.eme.domain.index.Book;
import pl.koder95.eme.domain.index.BookTemplate;
import pl.koder95.eme.domain.index.Index;
import pl.koder95.eme.model.RepositoryException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Kodek wiersza CSV dla indeksów ksiąg metrykalnych.
 *
 * <p>Format (zgodny z historycznymi danymi programu, v0.1.x):</p>
 * <ul>
 *     <li>separator {@code ;}, bez nagłówka i bez escapingu,</li>
 *     <li>kolumny danych odpowiadają polom szablonu księgi w kolejności
 *     atrybutu {@code index} z {@code templates.xml},</li>
 *     <li>dwie ostatnie kolumny to numer aktu: sygnatura i rok.</li>
 * </ul>
 *
 * <p>Ponieważ format nie przewiduje escapingu, wartości zawierające separator
 * albo znaki nowej linii są odrzucane przy zapisie.</p>
 */
public class CsvIndexCodec {

    private static final String SEPARATOR = ";";
    private static final String UTF8_BOM = "\uFEFF";

    /**
     * Odczytuje indeks z wiersza CSV.
     *
     * @param line     wiersz pliku
     * @param owner    księga, do której indeks należy
     * @param template szablon wyznaczający kolejność pól
     * @return indeks albo {@code null}, gdy wiersz jest pusty lub niepoprawny
     */
    public Index decode(String line, Book owner, BookTemplate template) {
        if (line == null) {
            return null;
        }
        String normalized = stripBom(line).trim();
        if (normalized.isEmpty()) {
            return null;
        }
        String[] columns = normalized.split(SEPARATOR, -1);
        if (columns.length < 2) {
            return null;
        }
        String yearValue = columns[columns.length - 1].trim();
        String sign = columns[columns.length - 2].trim();
        int year;
        try {
            year = Integer.parseInt(yearValue);
        } catch (NumberFormatException ex) {
            return null;
        }
        if (sign.isBlank()) {
            return null;
        }
        List<String> fieldNames = template.fieldNames();
        Map<String, String> data = new HashMap<>();
        int dataColumns = Math.min(columns.length - 2, fieldNames.size());
        for (int i = 0; i < dataColumns; i++) {
            String value = columns[i].trim();
            if (!value.isEmpty()) {
                data.put(fieldNames.get(i), value);
            }
        }
        data.put("an", sign + "/" + year);
        return Index.create(owner, data);
    }

    /**
     * Zapisuje indeks jako wiersz CSV (końcowe puste kolumny są pomijane).
     *
     * @throws RepositoryException gdy indeks nie ma numeru aktu albo wartość
     * zawiera separator lub znak nowej linii
     */
    public String encode(Index index, BookTemplate template) {
        ActNumber an = index.getActNumber();
        if (an == null) {
            throw new RepositoryException("Cannot encode index without act number: " + index);
        }
        List<String> values = new ArrayList<>();
        for (String fieldName : template.fieldNames()) {
            values.add(validated(index.getData(fieldName)));
        }
        while (!values.isEmpty() && values.get(values.size() - 1).isBlank()) {
            values.remove(values.size() - 1);
        }
        values.add(validated(an.getSign()));
        values.add(String.valueOf(an.getYear()));
        return String.join(SEPARATOR, values);
    }

    private static String validated(String value) {
        if (value.contains(SEPARATOR) || value.contains("\n") || value.contains("\r")) {
            throw new RepositoryException(
                    "CSV format does not support separators or line breaks in values: " + value);
        }
        return value;
    }

    private static String stripBom(String line) {
        return line.startsWith(UTF8_BOM) ? line.substring(1) : line;
    }
}
