/*
 * Copyright (C) 2021 Kamil Jan Mularski [@koder95]
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
package pl.koder95.eme.au;

import javafx.concurrent.Task;

/**
 * Klasa definiuje metody samoaktualizacji, które współpracują z JavaFX. Jest to szczególna implementacja
 * klasy abstrakcyjnej {@link Task}, której metoda {@link #call()} zwraca zawsze {@code null}.
 * Generator skryptu aktualizującego powinien zadbać o uruchomienie programu po skończeniu aktualizacji.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.4.4, 2024-12-02
 * @since 0.4.1
 * @see SelfUpdate
 */
public class SelfUpdateTask extends Task<Void> {

    private final SelfUpdate su = new SelfUpdate(this::updateProgress, this::updateMessage, this::updateTitle);

    /**
     * @return zawsze {@code null}
     */
    @Override
    protected Void call() throws Exception {
        su.run();
        return null;
    }

    private void updateProgress(Number workDone, Number max) {
        if (workDone instanceof Long && max instanceof Long) updateProgress(workDone.longValue(), max.longValue());
        else updateProgress(workDone.doubleValue(), max.doubleValue());
    }
}
