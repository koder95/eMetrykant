/*
 * Copyright (C) 2018 Kamil Jan Mularski [@koder95]
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package pl.koder95.eme.dfs;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import pl.koder95.eme.data.IHeadered;
import pl.koder95.eme.data.INamed;
import pl.koder95.eme.data.IReadOnlyLabel;
import pl.koder95.eme.data.app.IBookTemplate;

/**
 * Przechowuje informacje o szablonie księgi.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.2.0, 2018-10-07
 * @since 0.2.0
 */
public class BookTemplate implements IBookTemplate {

    /**
     * Przechowuje informacje dla sekcji. Każda sekcja zawiera listę pól.
     */
    public static class Section implements IHeadered {
        
        final String header;
        final List<Field> fields = new LinkedList<>();

        /**
         * Tworzy sekcję o podanym tytule.
         * 
         * @param header tytuł dla sekcji
         */
        public Section(String header) {
            this.header = header;
        }

        @Override
        public String toString() {
            String fields = "";
            fields = this.fields.stream().map((field) -> field + ";")
                    .reduce(fields, String::concat);
            return "Section \"" + header + "\": " + fields;
        }

        @Override
        public String getHeader() {
            return header;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof IHeadered?
                    getHeader().equals(((IHeadered) obj).getHeader())
                    : super.equals(obj);
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 97 * hash + Objects.hashCode(this.header);
            return hash;
        }
    }

    /**
     * Przechowuje informacje o polach, takie jak nazwa, indeks i etykieta.
     */
    public static class Field implements INamed, IReadOnlyLabel{
        
        final String name;
        final String label;
        final int index;

        /**
         * Tworzy pole z określonymi danymi: nazwą pola, indeksem dla danych
         * i etykietą. Wartość {@code index} odnosi się do wartości {@code data}
         * w indeksie (patrz: {@link Index#getData(int)}).
         * 
         * @param name nazwa zmiennej pola dla plików XML (atrybut)
         * @param index konkretna informacja z {@link Index indeksu}
         * @param label etykieta, wykorzystywana w formularzach
         */
        public Field(String name, int index, String label) {
            this.name = name;
            this.index = index;
            this.label = label;
        }

        @Override
        public String toString() {
            return "Field \"" + name + "\" in index=" + index + " and labeled="
                    + '"' + label + '"';
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getLabel() {
            return label;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof INamed?
                    getName().equals(((INamed) obj).getName())
                    : super.equals(obj);
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 53 * hash + Objects.hashCode(this.name);
            return hash;
        }
    }
    
    final List<Section> sections;
    final String name;

    /**
     * Tworzy szablon dla księgi o podanej nazwie.
     * 
     * @param name nazwa księgi, dla której tworzony jest szablon
     */
    public BookTemplate(String name) {
        this.sections = new LinkedList<>();
        this.name = name;
    }
    
    /**
     * Tworzy nowy szablon dla indeksów na podstawie pól, które są w tym
     * szablonie księgi.
     * 
     * @return nowy szablon indeksu
     */
    public IndexTemplate createIndexTemplate() {
        IndexTemplate tmpl = new IndexTemplate();
        sections.stream().forEach((s) -> {
            s.fields.stream().forEach((f) -> {
                tmpl.createAttr(f.name, f.index, f.label);
            });
        });
        return tmpl;
    }
    
    @Override
    public String toString() {
        return "Template has got " + sections.size() + " sections.";
    }
    
    @Override
    public Map<INamed, IHeadered> getHeaderToDataNameMap() {
        Map<INamed, IHeadered> dataNameMap = new HashMap<>();
        sections.forEach(s -> s.fields.forEach(f -> dataNameMap.put(f, s)));
        return dataNameMap;
    }

    @Override
    public Collection<INamed> getDataNameCollection(IHeadered headered) {
        return sections.stream()
                .reduce(null, (r, c) -> c.equals(headered)? c : r).fields
                .stream().map(f -> (INamed) f).collect(Collectors.toList());
    }

    @Override
    public IReadOnlyLabel getLabel(INamed dataName) {
        return sections.stream().flatMap(s -> s.fields.stream())
                .reduce(null, (r, c) -> c.equals(dataName)? c : r);
    }

}