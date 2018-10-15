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

import pl.koder95.eme.data.app.IAct;
import pl.koder95.eme.data.app.IData;
import pl.koder95.eme.data.impl.ReadOnlyField;

/**
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.2.1, 2018-10-15
 * @since 0.2.1
 */
public class Data extends ReadOnlyField implements IData {

    private final IAct owner;

    public Data(IAct owner, String name, String value) {
        super(name, value);
        this.owner = owner;
    }

    @Override
    public IAct getOwner() {
        return owner;
    }
    
    public static class Builder
            implements pl.koder95.eme.data.app.IBuilder<IData> {
        
        protected IAct owner = null;
        protected String name = null;
        protected String label = "";
        protected String value = null;

        public Builder() {}
        
        public Builder setOwner(IAct owner) {
            this.owner = owner;
            return this;
        }
        
        public Builder setName(String name) {
            this.name = name;
            return this;
        }
        
        public Builder setLabel(String label) {
            this.label = label;
            return this;
        }
        
        public Builder setValue(String value) {
            this.value = value;
            return this;
        }

        @Override
        public IData build() {
            if (owner == null || name == null) return null;
            if (label == null) label = "";
            IAct owner = this.owner;
            this.owner = null;
            String name = this.name;
            this.name = null;
            String value = this.value;
            this.value = null;
            String label = this.label;
            this.label = "";
            return name.equals(ActNumber.DATA_NAME)? new ActNumber(owner, value)
                    : new LabeledData(owner, name, label, value);
        }
    }
}
