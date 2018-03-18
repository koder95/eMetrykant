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

package pl.koder95.eme;

/**
 * Interfejs reprezentuje metodę uruchamiania służącą do uruchomienia aplikacji.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.1.10, 2018-03-18
 * @since 0.1.10
 */
public interface LaunchMethod {

    /**
     * Uruchamia program używając podanych argumentów, albo przekazuje
     * uruchomienie do następnej metody uruchamiania.
     * 
     * @param args lista argumentów
     */
    public void launch(String[] args);
    
    /**
     * Ustawia metodę, której zostaną przekazane argumenty, gdyby ta metoda nie
     * mogła obsłużyć uruchomienia programu.
     * 
     * @param next następna metoda uruchamiania
     */
    public void setNextLaunchMethod(LaunchMethod next);
    
    /**
     * Zwraca metodę, która w razie niemożliwości obsłużenia żądania
     * uruchomienia programu, wykorzystywana będzie do obsługi tego żądania.
     * 
     * @return kolejna metoda uruchamiania
     */
    public LaunchMethod nextMethod();
}
