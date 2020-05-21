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

package pl.koder95.eme.searching;

import org.controlsfx.control.textfield.AutoCompletionBinding;

/**
 * Rozszerza interfejs AutoCompletionBinding.ISearchingRequest ustanawiając
 * domyślną implementację dla metody {@link #isCancelled()} zawsze zwracającej
 * wartość {@code false}.
 * 
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.3.1, 2020-05-21
 * @since 0.3.0
 */
public interface ISearchingRequest extends AutoCompletionBinding
        .ISuggestionRequest {

    @Override
    default boolean isCancelled() {
        return false;
    }
}
