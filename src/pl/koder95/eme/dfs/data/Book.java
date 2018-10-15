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
import java.util.LinkedList;
import java.util.List;
import pl.koder95.eme.data.app.IAct;
import pl.koder95.eme.data.app.IBook;
import pl.koder95.eme.data.app.IBookRepository;
import pl.koder95.eme.data.app.IBuilder;
import pl.koder95.eme.data.impl.UnmodifiableList;

/**
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.2.1, 2018-10-15
 * @since 0.2.1
 */
public class Book extends UnmodifiableList<IAct> implements IBook {

    private final IBookRepository owner;
    private final String name;
    
    public Book(IBookRepository owner, String name, List<IAct> list) {
        super(list);
        this.owner = owner;
        this.name = name;
    }

    @Override
    public IBookRepository getOwner() {
        return owner;
    }

    @Override
    public String getName() {
        return name;
    }
    
    public static class Builder implements IBuilder<IBook> {
        
        protected IBookRepository owner = null;
        protected String name = "";
        protected final LinkedList<IAct> acts = new LinkedList<>();

        public Builder() {}
        
        public Builder setOwner(IBookRepository owner) {
            this.owner = owner;
            return this;
        }
        
        public Builder setName(String name) {
            this.name = name;
            return this;
        }
        
        public Builder addAct(IAct act) {
            this.acts.add(act);
            return this;
        }
        
        @Override
        public IBook build() {
            if (owner == null) return null;
            if (name == null) name = "";
            ArrayList<IAct> acts = new ArrayList<>(this.acts);
            this.acts.clear();
            String name = this.name;
            this.name = "";
            IBookRepository owner = this.owner;
            this.owner = null;
            return new Book(owner, name, acts);
        }
        
    }

}
