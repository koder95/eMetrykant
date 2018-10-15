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
import java.util.LinkedList;
import pl.koder95.eme.data.app.IAct;
import pl.koder95.eme.data.app.IBook;
import pl.koder95.eme.data.app.IBuilder;
import pl.koder95.eme.data.app.IDataSection;
import pl.koder95.eme.data.impl.UnmodifiableCollection;

/**
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.2.1, 2018-10-15
 * @since 0.2.1
 */
public class Act extends UnmodifiableCollection<IDataSection> implements IAct{
    
    private final IBook owner;

    public Act(IBook owner, Collection<IDataSection> collection) {
        super(collection);
        this.owner = owner;
    }

    @Override
    public IBook getOwner() {
        return owner;
    }
    
    public static class Builder implements IBuilder<IAct> {
        
        protected final LinkedList<IDataSection> sections = new LinkedList<>();
        protected IBook owner = null;

        public Builder() {}
        
        public Builder addDataSection(IDataSection section) {
            sections.add(section);
            return this;
        }
        
        public Builder setOwner(IBook owner) {
            this.owner = owner;
            return this;
        }
        
        @Override
        public IAct build() {
            if (owner == null) return null;
            ArrayList<IDataSection> sections = new ArrayList<>(this.sections);
            this.sections.clear();
            IBook owner = this.owner;
            this.owner = null;
            return new Act(owner, sections);
        }
    }

}
