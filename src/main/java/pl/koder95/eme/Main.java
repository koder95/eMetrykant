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

import javafx.application.Application;
import pl.koder95.eme.au.SelfUpdate;
import pl.koder95.eme.core.*;

import java.text.Collator;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

/**
 * Klasa uruchamiająca i inicjująca podstawowe elementy aplikacji.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.4.4, 2024-12-02
 * @since 0.0.201
 */
public class Main {
    /**
     * Domyślny pakiet językowy.
     */
    public static final ResourceBundle BUNDLE
            = ResourceBundle.getBundle("pl/koder95/eme/strings");
    /**
     * Używana stała dla Polski.
     */
    public static final Locale POLISH = Locale.forLanguageTag("PL-pl"); //NOI18N
    /**
     * Domyślny sposób porównywania stringów (polski).
     */
    public static final Collator DEFAULT_COLLATOR = Collator.getInstance(POLISH); //NOI18N

    private static final String FAV_PATH_START = "pl/koder95/eme/favicon";
    /**
     * Ścieżka ikony dla okienek.
     */
    public static final String FAVICON_PATH = FAV_PATH_START + ".png";
    /**
     * Wzór identyfikujący liczby w stringu.
     */
    public static final Pattern DIGITS_STRING_PATTERN = Pattern.compile("([0-9]*)");
    /**
     * Sprawdzenie, czy system operacyjny należy do rodziny "Windows".
     */
    public static final boolean IS_WINDOWS_OS = System.getProperty("os.name").toLowerCase().contains("windows");

    public static void main(String[] args) {
        if (args.length == 1) {
            if (args[0].equals("-v")) {
                System.out.println(Version.get());
                System.exit(0);
                return;
            }
            else if (args[0].equals("-u")) {
                SelfUpdate su = new SelfUpdate();
                new Thread(su).start();
                return;
            }
        }
        Application.launch(App.class, args);
    }

    /**
     * @since 0.0.203
     * @see MemoryUtils#releaseMemory()
     * @deprecated use {@link MemoryUtils#releaseMemory()}
     */
    @Deprecated
    public static void releaseMemory() {
        MemoryUtils.releaseMemory();
    }
}
