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
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

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
    
    static List<String> createNotExistDirs(List<String> args) {
        if (!DATA_DIR.exists()) DATA_DIR.mkdirs();
        if (!CSV_DIR.exists()) CSV_DIR.mkdirs();
        if (!XML_DIR.exists()) XML_DIR.mkdirs();
        return args;
    }
    
    static List<String> createNotExistFiles(List<String> args) throws IOException {
        if (!TEMPLATE_XML.exists()) TEMPLATE_XML.createNewFile();
        if (CSV_DIR.list().length == 0) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Wybierz pliki CSV...");
            fileChooser.setMultiSelectionEnabled(true);
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("CSV", "csv"));
            List<File> csv = null;
            int result = fileChooser.showOpenDialog(null);
            csv = Arrays.asList(result == JFileChooser.APPROVE_OPTION? fileChooser.getSelectedFiles() : null);
            for (File f: csv) {
                java.nio.file.Files.copy(f.toPath(),
                        CSV_DIR.toPath().resolve(f.getName()),
                        StandardCopyOption.REPLACE_EXISTING);
            }
            return new LinkedList<>(Arrays.asList(new String[] { "-c" }));
        } else return args;
    }

    private Files() {}
}
