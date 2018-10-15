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

package pl.koder95.eme.data;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.2.1, 2018-10-15
 * @since 0.2.1
 */
public interface IUnmodifiableList<E> extends List<E> {


    @Override
    default boolean add(E e) {
        return false;
    }

    @Override
    default void add(int index, E element) {}

    @Override
    default boolean addAll(Collection<? extends E> c) {
        return false;
    }

    @Override
    default boolean addAll(int index, Collection<? extends E> c) {
        return false;
    }

    @Override
    default void clear() {}

    @Override
    default boolean remove(Object o) {
        return false;
    }

    @Override
    default E remove(int index) {
        return null;
    }

    @Override
    default boolean removeAll(Collection<?> c) {
        return false;
    }

    @Override
    default boolean removeIf(Predicate<? super E> filter) {
        return false;
    }

    @Override
    default void replaceAll(UnaryOperator<E> operator) {}

    @Override
    default boolean retainAll(Collection<?> c) {
        return false;
    }

    @Override
    default E set(int index, E element) {
        return null;
    }

    @Override
    default void sort(Comparator<? super E> c) {}
}
