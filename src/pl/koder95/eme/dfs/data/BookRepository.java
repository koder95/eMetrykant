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

package pl.koder95.eme.dfs.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import pl.koder95.eme.data.INamed;
import pl.koder95.eme.data.app.IBook;
import pl.koder95.eme.data.app.IBookRepository;
import pl.koder95.eme.data.app.IBookTemplate;
import pl.koder95.eme.data.app.IBuilder;
import pl.koder95.eme.data.impl.UnmodifiableCollection;

/**
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.2.1, 2018-10-15
 * @since 0.2.1
 */
public class BookRepository extends UnmodifiableCollection<IBook>
        implements IBookRepository {
    
    private final Map<INamed, IBookTemplate> templatesMap;

    public BookRepository(Collection<IBook> collection,
            Map<INamed, IBookTemplate> templatesMap) {
        super(collection);
        this.templatesMap = templatesMap;
    }

    @Override
    public IBookTemplate getBookTemplate(String name) {
        INamed n = templatesMap.keySet().stream().reduce(null,
                (r, c) -> c.getName().equals(name)? c : r);
        if (n == null) return null;
        return templatesMap.get(n);
    }
    
    public static class Builder implements IBuilder<IBookRepository> {
        
        private final LinkedList<IBook> books = new LinkedList<>();
        private final HashMap<INamed, IBookTemplate> templates = new HashMap<>();

        public Builder() {}
        
        public Builder addBook(IBook book) {
            books.add(book);
            return this;
        }
        
        public Builder setBookTemplate(INamed book, IBookTemplate tmpl) {
            templates.put(book, tmpl);
            return this;
        }

        @Override
        public IBookRepository build() {
            ArrayList<IBook> books = new ArrayList<>(this.books);
            this.books.clear();
            HashMap<INamed, IBookTemplate>
                    templates = new HashMap<>(this.templates);
            this.templates.clear();
            return new BookRepository(books, templates);
        }
        
    }

}
