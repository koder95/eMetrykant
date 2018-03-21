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

package pl.koder95.eme.gui;

/**
 * Interfejs służy do przesyłania żądań związanych z wyświetlaniem danych.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.1.11, 2018-03-21
 * @since 0.1.11
 */
public interface DataShowing {

    /**
     * Generuje żądanie wyświetlenia pola o podanej nazwie
     * i z określoną wartością.
     * 
     * @param name nazwa pola, która ma być widoczna, opisująca wartość
     * @param value wartość pola
     */
    public void addData(String name, String value);
    
    /**
     * Generuje żądanie zresetowania wartości, która przyporządkowana jest do
     * pola o podanej nazwie.
     * 
     * @param name nazwa pola, która jest wyświetlana
     */
    public void reset(String name);
    
    /**
     * Generuje żądanie usunięcia pola o podanej nazwie wraz z wartością.
     * 
     * @param name nazwa pola, która jest wyświetlana
     */
    public void removeData(String name);
}
