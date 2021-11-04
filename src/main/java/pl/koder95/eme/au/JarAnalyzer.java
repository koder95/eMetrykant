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
package pl.koder95.eme.au;

import pl.koder95.eme.Version;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;

/**
 * Klasa umożliwia w łatwy sposób zarządzać plikiem JAR na potrzeby aplikacji.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.4.1, 2021-11-05
 * @since 0.4.1
 */
public class JarAnalyzer {

    private final Path jar;

    public JarAnalyzer(Path jar) {
        this.jar = jar;
    }

    /**
     * Pobiera wersję z archiwum JAR poprzez uruchomienie paczki z argumentem {@code -v}.
     *
     * @return null — jeśli archiwum JAR nie zwraca wersji, w przeciwnym razie zwraca wersję JAR
     * @throws IOException w przypadku problemów z odczytaniem strumieni wejścia / wyjścia
     */
    public Version getVersion() throws IOException {
        if (jar.endsWith(".jar")) {
            ProcessBuilder builder = new ProcessBuilder("java", "-jar", jar.toString(), "-v");
            Process process = builder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            Version fromLine = Version.parse(reader.readLine());
            if (process.isAlive()) process.destroy();
            reader.close();
            return fromLine;
        }
        return null;
    }
}