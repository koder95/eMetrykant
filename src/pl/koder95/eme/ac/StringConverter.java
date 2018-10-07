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

package pl.koder95.eme.ac;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import javafx.util.converter.FormatStringConverter;
import pl.koder95.eme.dfs.Index;
import pl.koder95.eme.fx.SuggestCreatingMethod;

/**
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.2.0, 2018-10-07
 * @since 0.2.0
 */
public class StringConverter extends FormatStringConverter<Index> {

    public StringConverter(SuggestCreatingMethod scm) {
        super(new Format() {
            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo,
                    FieldPosition pos) {
                if (obj instanceof Index) {
                    toAppendTo.append(scm.createSuggestion((Index) obj));
                }
                return toAppendTo;
            }

            @Override
            public Object parseObject(String source, ParsePosition pos) {
                return new Object();
            }
        });
    }

}
