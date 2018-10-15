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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import pl.koder95.eme.data.IHeadered;
import pl.koder95.eme.data.INamed;
import pl.koder95.eme.data.IReadOnlyLabel;
import pl.koder95.eme.data.app.IBookTemplate;
import pl.koder95.eme.data.app.IBuilder;

/**
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.2.1, 2018-10-15
 * @since 0.2.1
 */
public class BookTemplate implements IBookTemplate {

    private final Map<INamed, IHeadered> headerMap;

    public BookTemplate(Map<INamed, IHeadered> headerMap) {
        this.headerMap = headerMap;
    }

    @Override
    public Map<INamed, IHeadered> getHeaderToDataNameMap() {
        return headerMap;
    }

    @Override
    public Collection<INamed> getDataNameCollection(IHeadered headered) {
        return headerMap.keySet().stream()
                .filter(h -> headerMap.get(h).equals(headered))
                .collect(Collectors.toList());
    }

    @Override
    public IReadOnlyLabel getLabel(INamed dataName) {
        return (IReadOnlyLabel) headerMap.keySet().stream()
                .reduce(null, (r, rof) -> rof.equals(dataName)? rof : r);
    }
    
    public static class Builder implements IBuilder<IBookTemplate> {
        
        protected final HashMap<INamed, IHeadered> headerMap = new HashMap<>();

        public Builder() {}
        
        public Builder setHeadered(INamed dataNamed, IHeadered headered) {
            headerMap.put(dataNamed, headered);
            return this;
        }

        @Override
        public IBookTemplate build() {
            HashMap<INamed, IHeadered> headerMap = new HashMap<>(this.headerMap);
            this.headerMap.clear();
            return new BookTemplate(headerMap);
        }
    }
}
