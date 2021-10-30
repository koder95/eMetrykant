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

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Przechowuje dane na temat plików.
 * 
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.2.0, 2018-10-07
 * @since 0.1.5
 */
public final class Files {
    
    /**
     * Folder, gdzie znajdują się pliki programu.
     */
    public static final File WORKDIR
            = new File(System.getProperty("user.dir")); //NOI18N
    /**
     * Folder, gdzie znajdują się pliki zawierające dane do wczytania
     * przez program.
     */
    public static final File DATA_DIR = WORKDIR;
    /**
     * Folder, gdzie znajdują się pliki zawierające dane do wczytania
     * przez program o rozszerzeniu XML.
     */
    public static final File XML_DIR = DATA_DIR; //NOI18N
    /**
     * Plik, który przechowuje dane na temat indeksów.
     */
    public static final File INDICES_XML = new File(Files.XML_DIR, "indices.xml");

    public static final Path TEMP_DIR = Paths.get(System.getProperty("java.io.tmpdir"), "eMetrykant");

    public static final File UPDATE_WIN = new File(Files.WORKDIR, "update.bat");
    public static final File UPDATE_UNIX = new File(Files.WORKDIR, "update");
    public static final Path SELF = WORKDIR.toPath().resolve("eMetrykant.jar");

    private Files() {}
}
