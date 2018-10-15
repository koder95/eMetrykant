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
import pl.koder95.eme.data.app.IActNumber;
import pl.koder95.eme.data.app.IData;

/**
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.2.1, 2018-10-15
 * @since 0.2.1
 */
public class ActNumber extends Data implements IActNumber {

    public ActNumber(IAct owner, String value) {
        super(owner, DATA_NAME, value.contains("/")? value : "?/?");
    }
    
    public ActNumber(IAct owner, String sign, int year) {
        this(owner, sign + "/" + year);
    }
    
    public static class Builder extends Data.Builder {
        
        public Builder setValue(String sign, int year) {
            setValue(sign + "/" + year);
            return this;
        }

        @Override
        public Builder setName(String name) {
            return this;
        }

        @Override
        public IActNumber build() {
            IData d = super.build();
            if (d == null || !d.getName().equals(DATA_NAME)) return null;
            return new ActNumber(d.getOwner(), d.getValue());
        }
        
    }

}
