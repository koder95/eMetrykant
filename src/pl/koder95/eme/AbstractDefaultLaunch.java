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

import java.util.List;

/**
 * Klasa reprezentuje uruchamianie domyślne, które nie posiada odniesień do
 * jakiegoś innego sposobu uruchamiania.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.1.12-alt, 2018-08-04
 * @since 0.1.10
 */
public abstract class AbstractDefaultLaunch implements LaunchMethod {

    @Override
    public LaunchMethod nextMethod() {
        return null;
    }

    @Override
    public void setNextLaunchMethod(LaunchMethod next) {
        // do nothing
    }

    @Override
    public abstract void launch(List<String> args);

}
