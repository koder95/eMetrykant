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

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;

import static pl.koder95.eme.Files.SELF;
import static pl.koder95.eme.Main.BUNDLE;
import static pl.koder95.eme.Main.IS_WINDOWS_OS;

/**
 * Klasa dostarcza metody do generowania skryptów aktualizacyjnych.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.4.3, 2024-12-01
 * @since 0.4.1
 */
public class UpdateScriptGenerator {

    private static final String WIN_EXT = ".bat";
    private final Path path;

    private UpdateScriptGenerator(Path out) {
        this.path = out;
    }

    /**
     * @return ścieżka do skryptu
     */
    public Path getPath() {
        return path;
    }

    /**
     * Tworzy nowy generator. Ścieżka zostanie odpowiednio przystosowana do systemu operacyjnego
     * @param out ścieżka do pliku zawierającego skrypt aktualizujący
     * @return nowa instancja generatora skryptu
     */
    public static UpdateScriptGenerator create(Path out) {
        return new UpdateScriptGenerator(IS_WINDOWS_OS?
                out.endsWith(WIN_EXT)? out : Paths.get(out + WIN_EXT)
                : out);
    }

    /**
     * Zapisuje w {@link #getPath() pliku} skrypt dostosowując go do systemu.
     * @param updateMap mapa źródło-cel, ze źródła kopiowane są pliki do celu
     * @throws IOException w przypadku problemów z zapisem
     */
    public void generateUpdateScript(Map<Path, Path> updateMap) throws IOException {
        if (Files.notExists(path)) Files.createFile(path);
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.WRITE)) {
            if (path.endsWith(WIN_EXT)) {
                generateWinUpdateScript(writer, updateMap);
            } else {
                generateUnixScript(writer, updateMap);
            }
        }
    }

    private void generateUnixScript(BufferedWriter writer, Map<Path, Path> updateMap) throws IOException {
        writer.write("$ set echo off");
        writer.newLine();
        writer.write("sleep 5");
        writer.newLine();
        for (Map.Entry<Path, Path> entry : updateMap.entrySet()) {
            Path oldFile = entry.getValue();
            Path newFile = entry.getKey();
            if (Files.exists(newFile)) {
                writer.write("cp -r " + '"');
                writer.write(newFile.toString());
                writer.write('"' + " " + '"');
                writer.write(oldFile.toString());
                writer.write('"' + " /y");
                writer.newLine();
                writer.write("rmdir -r " + '"');
                writer.write(oldFile.getParent().toString());
                writer.write('"' + " /y");
                writer.newLine();
            } else throw new FileNotFoundException(BUNDLE.getString("THR_UPDATE_FILES_NOT_FOUND"));
        }
        writer.write("java -jar \"" + SELF + '"');
        writer.newLine();
    }

    private void generateWinUpdateScript(BufferedWriter writer, Map<Path, Path> updateMap)
            throws IOException {
        writer.write("@echo off");
        writer.newLine();
        writer.write("timeout /T 5 /nobreak > nul");
        writer.newLine();
        for (Map.Entry<Path, Path> entry : updateMap.entrySet()) {
            Path oldFile = entry.getValue();
            Path newFile = entry.getKey();
            if (Files.exists(newFile)) {
                writer.write("copy " + '"');
                writer.write(newFile.toString());
                writer.write('"' + " " + '"');
                writer.write(oldFile.toString());
                writer.write('"' + " /y");
                writer.newLine();
                writer.write("rmdir -r " + '"');
                writer.write(oldFile.getParent().toString());
                writer.write('"' + " /y");
                writer.newLine();
            } else throw new FileNotFoundException(BUNDLE.getString("THR_UPDATE_FILES_NOT_FOUND"));
        }
        writer.write("java -jar \"" + SELF + '"');
        writer.newLine();
    }
}
