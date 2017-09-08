/*
 * Copyright (C) 2017 Kamil Jan Mularski [@koder95]
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
import java.io.IOException;

/**
 * Przechowuje dane na temat plików.
 * 
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.1.5, 2017-09-08
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
    public static final File DATA_DIR = new File(WORKDIR, "data"); //NOI18N
    /**
     * Folder, gdzie znajdują się pliki zawierające dane do wczytania
     * przez program o rozszerzeniu CSV.
     */
    public static final File CSV_DIR = new File(DATA_DIR, "csv"); //NOI18N
    /**
     * Folder, gdzie znajdują się pliki zawierające dane do wczytania
     * przez program o rozszerzeniu XML.
     */
    public static final File XML_DIR = new File(DATA_DIR, "xml"); //NOI18N
    /**
     * Plik XML, gdzie zapisane są dane o szablonach.
     */
    public static final File TEMPLATE_XML
            = new File(WORKDIR, "templates.xml"); //NOI18N
    
    static void createNotExistDirs() {
        if (!DATA_DIR.exists()) DATA_DIR.mkdirs();
        if (!CSV_DIR.exists()) CSV_DIR.mkdirs();
        if (!XML_DIR.exists()) XML_DIR.mkdirs();
    }
    
    static void createNotExistFiles() throws IOException {
        if (!TEMPLATE_XML.exists()) TEMPLATE_XML.createNewFile();
    }

    private Files() {}
}
