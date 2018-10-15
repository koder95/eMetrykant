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

package pl.koder95.eme.data.app;

import java.util.Collection;
import java.util.Map;
import pl.koder95.eme.data.IHeadered;
import pl.koder95.eme.data.INamed;
import pl.koder95.eme.data.IReadOnlyLabel;

/**
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.2.1, 2018-10-15
 * @since 0.2.1
 */
public interface IBookTemplate {

    public Map<INamed, IHeadered> getHeaderToDataNameMap();

    public Collection<INamed> getDataNameCollection(IHeadered headered);
    
    public IReadOnlyLabel getLabel(INamed dataName);

}
