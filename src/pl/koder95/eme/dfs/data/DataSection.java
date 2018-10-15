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
import pl.koder95.eme.data.app.IBuilder;
import pl.koder95.eme.data.app.IData;
import pl.koder95.eme.data.app.IDataSection;
import pl.koder95.eme.data.impl.UnmodifiableCollection;

/**
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.2.1, 2018-10-15
 * @since 0.2.1
 */
public class DataSection extends UnmodifiableCollection<IData>
        implements IDataSection {
    
    private final IAct owner;
    private final String header;

    public DataSection(IAct owner, String header, Collection<IData> collection) {
        super(collection);
        this.owner = owner;
        this.header = header;
    }

    @Override
    public IAct getOwner() {
        return owner;
    }

    @Override
    public String getHeader() {
        return header;
    }
    
    public static class Builder implements IBuilder<DataSection> {

        protected IAct owner = null;
        protected String header = "";
        protected final LinkedList<IData> data = new LinkedList<>();

        public Builder setOwner(IAct owner) {
            this.owner = owner;
            return this;
        }

        public Builder setHeader(String header) {
            this.header = header;
            return this;
        }
        
        public Builder addData(IData d) {
            data.add(d);
            return this;
        }

        @Override
        public DataSection build() {
            if (owner == null) return null;
            if (header == null) header = "";
            IAct owner = this.owner;
            this.owner = null;
            String header = this.header;
            this.header = "";
            ArrayList<IData> data = new ArrayList<>(this.data);
            this.data.clear();
            return new DataSection(owner, header, data);
        }
    }

}
