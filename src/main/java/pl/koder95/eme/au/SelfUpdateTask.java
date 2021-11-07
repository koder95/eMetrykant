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

import javafx.application.Platform;
import javafx.concurrent.Task;
import pl.koder95.eme.git.RepositoryInfo;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.NumberFormat;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static pl.koder95.eme.Files.*;

/**
 * Klasa definiuje metody samoaktualizacji, które współpracują z JavaFX. Jest to szczególna implementacja
 * klasy abstrakcyjnej {@link Task}, której metoda {@link #call()} zwraca zawsze {@code null} i działa
 * w następujący sposób:
 * <ol>
 *     <li>Usuwanie zawartości folderu tymczasowego, gdzie pobrane zostaną pliki ostatniego wydania.</li>
 *     <li>Pobieranie najnowszego wydania programu w postaci archiwum ZIP.</li>
 *     <li>Wypakowanie zawartości archiwum do folderu tymczasowego.</li>
 *     <li>Usunięcie pobranego archiwum ZIP.</li>
 *     <li>Wygenerowanie skryptu samoaktualizacji.</li>
 *     <li>Uruchomienie skryptu i zamknięcie programu.</li>
 * </ol>
 * Generator skryptu aktualizującego powinien zadbać o uruchomienie programu po skończeniu aktualizacji.
 */
public class SelfUpdateTask extends Task<Void> {

    private static final UpdateScriptGenerator US_GENERATOR = UpdateScriptGenerator.create(UPDATE_SCRIPT);

    /**
     * @return zawsze {@code null}
     */
    @Override
    protected Void call() throws Exception {
        // usuwanie plików z katalogu tymczasowego:
        Files.walk(TEMP_DIR).sorted(Comparator.reverseOrder()).forEach(path -> {
            try {
                Files.deleteIfExists(path);
            } catch (IOException e) {
                // ignore
            }
        });
        // pobieranie zip'a i rozpakowywanie do folderu tymczasowego:
        if (extractZip(downloadZip())) {
            Map<Path, Path> updateMap = new HashMap<>();
            Files.list(TEMP_DIR).forEach(path -> {
                updateMap.put(path, WORKDIR.resolve(path.getFileName().toString()));
            });
            US_GENERATOR.generateUpdateScript(updateMap);
        }
        restart(); // restartowanie z wywołaniem skryptu
        return null;
    }

    private Path downloadZip() throws IOException {
        updateTitle("Pobieranie " + RepositoryInfo.get().getLatestReleaseName());
        return tryTransfer(RepositoryInfo.get().getLatestReleaseBrowserURL());
    }

    private Path tryTransfer(String urlSpec) throws IOException {
        Path forDownload = TEMP_DIR.resolve(RepositoryInfo.get().getLatestReleaseName());
        Files.deleteIfExists(forDownload);
        Files.createDirectories(forDownload.getParent());
        Files.createFile(forDownload);
        URL url = new URL(urlSpec);
        System.out.println("Downloading: " + forDownload);
        updateProgress(0, 1L);
        try (ReadableByteChannel rbc = Channels.newChannel(url.openStream());
             FileChannel channel = FileChannel.open(forDownload, StandardOpenOption.WRITE)) {
            transfer(rbc, channel);
        }
        if (Files.isRegularFile(forDownload)) return forDownload;
        Files.deleteIfExists(forDownload);
        return null;
    }

    private void transfer(ReadableByteChannel rbc, FileChannel channel) throws IOException {
        System.out.println("TRANSFERRING TO: " + channel);
        long size = RepositoryInfo.get().getLatestReleaseSize();
        channel.truncate(size);
        long count = size > 100? size / 100: 1;
        long workDone = 0;
        updateProgress(workDone, size);
        while (workDone < size) {
            long transferred = channel.transferFrom(rbc, workDone, count);
            workDone += transferred;
            updateMessage(NumberFormat.getPercentInstance().format((double) workDone / size));
            updateProgress(workDone, size);
            //System.out.println(getWorkDone() + "/" + getTotalWork());
        }
        channel.force(true);
    }

    private boolean extractZip(Path forExtract) throws IOException {
        updateTitle("Rozpakowywanie " + forExtract.getFileName());
        updateProgress(0, 1);
        updateMessage("");
        try (ZipFile zip = new ZipFile(forExtract.toFile())) {
            updateProgress(0, Files.size(forExtract));

            Enumeration<? extends ZipEntry> entries = zip.entries();
            long total = getTotal(zip);
            long workDone = 0;
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                Path outFile = TEMP_DIR.resolve(entry.getName());
                Files.deleteIfExists(outFile);

                if (!entry.isDirectory()) {
                    Files.createFile(outFile);

                    if (Files.isRegularFile(outFile)) {
                        tryTransfer(zip, entry, outFile, workDone, total);
                    }
                } else if (Files.notExists(outFile)) {
                    Files.createDirectories(outFile);
                }
            }
            updateProgress(1d, 1d);
            updateTitle("Zakończono wypakowywanie archiwum");
        }
        updateProgress(Double.NaN, 0);
        return Files.deleteIfExists(forExtract);
    }

    private long getTotal(ZipFile zip) throws IOException {
        long total = 0;
        Enumeration<? extends ZipEntry> entries = zip.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if (!entry.isDirectory()) {
                InputStream input = zip.getInputStream(entry);
                int available = input.available();
                total += available;
            }
        }
        return total;
    }

    private void tryTransfer(ZipFile zip, ZipEntry entry, Path outFile, long workDone, long total) throws IOException {
        try (InputStream input = zip.getInputStream(entry);
             OutputStream output = Files.newOutputStream(outFile)) {
            transfer(input, output, workDone, total);
        }
    }

    private void transfer(InputStream input, OutputStream output, long workDone, long total) throws IOException {
        while (input.available() > 0) {
            int b = input.read();
            output.write(b);
            output.flush();
            updateProgress(++workDone, total);
            updateMessage(NumberFormat.getPercentInstance().format((double) workDone / total));
        }
    }

    private static void restart() {
        Platform.exit();
        try {
            new ProcessBuilder(US_GENERATOR.getPath().toString()).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}
