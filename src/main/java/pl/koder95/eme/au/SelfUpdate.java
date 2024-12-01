package pl.koder95.eme.au;

import javafx.application.Platform;
import pl.koder95.eme.git.RepositoryInfo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.NumberFormat;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static pl.koder95.eme.Files.*;
import static pl.koder95.eme.Files.TEMP_DIR;

/**
 * Klasa odpowiedzialna za aktualizowanie własnej wersji do najnowszej.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.4.3, 2024-12-01
 * @since 0.4.3
 */
public class SelfUpdate implements Runnable {

    private static final UpdateScriptGenerator US_GENERATOR = UpdateScriptGenerator.create(UPDATE_SCRIPT);

    private final BiConsumer<Number, Number> updateProgress;
    private final Consumer<String> updateMessage;
    private final Consumer<String> updateTitle;
    private final UpdateScriptGenerator updateScriptGenerator;

    SelfUpdate(BiConsumer<Number, Number> updateProgress, Consumer<String> updateMessage,
                      Consumer<String> updateTitle, UpdateScriptGenerator updateScriptGenerator) {
        this.updateProgress = updateProgress;
        this.updateMessage = updateMessage;
        this.updateTitle = updateTitle;
        this.updateScriptGenerator = updateScriptGenerator;
    }

    SelfUpdate(BiConsumer<Number, Number> updateProgress, Consumer<String> updateMessage, Consumer<String> updateTitle) {
        this(updateProgress, updateMessage, updateTitle, US_GENERATOR);
    }

    /**
     * Tworzy domyślną instancję klasy.
     */
    public SelfUpdate() {
        this((workDone, max) -> {
            double percent = 100 * workDone.doubleValue()/max.doubleValue();
            System.out.println("Processing... " + (int) percent);
        }, msg -> System.out.println("INFO: " + msg), title -> System.out.println('\n' + title + '\n'));
    }

    @Override
    public void run() {
        try {
            // usuwanie plików z katalogu tymczasowego:
            clear();
            // pobieranie zip'a i rozpakowywanie do folderu tymczasowego:
            prepare();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        restart(); // restartowanie z wywołaniem skryptu
    }

    private void clear() throws IOException {
        List<Exception> exceptions = new LinkedList<>();
        try (Stream<Path> paths = Files.walk(TEMP_DIR).sorted(Comparator.reverseOrder())) {
            paths.forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException e) {
                    exceptions.add(e);
                }
            });
        }
        if (!exceptions.isEmpty()) {
            IOException ex = new IOException();
            exceptions.forEach(ex::addSuppressed);
            exceptions.clear();
            throw ex;
        }
    }

    private void restart() {
        Platform.exit();
        try {
            new ProcessBuilder(updateScriptGenerator.getPath().toString()).start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.exit(0);
    }

    private void prepare() throws IOException {
        if (extractZip(downloadZip())) {
            Map<Path, Path> updateMap = new HashMap<>();
            try (Stream<Path> paths = Files.list(TEMP_DIR)) {
                paths.forEach(path -> updateMap.put(path, WORKDIR.resolve(path.getFileName().toString())));
            }
            updateScriptGenerator.generateUpdateScript(updateMap);
        }
    }


    private Path downloadZip() throws IOException {
        updateTitle.accept("Pobieranie " + RepositoryInfo.get().getLatestReleaseName());
        return tryTransfer(RepositoryInfo.get().getLatestReleaseBrowserURL());
    }

    private Path tryTransfer(String urlSpec) throws IOException {
        Path forDownload = TEMP_DIR.resolve(RepositoryInfo.get().getLatestReleaseName());
        Files.deleteIfExists(forDownload);
        Files.createDirectories(forDownload.getParent());
        Files.createFile(forDownload);
        URL url = new URL(urlSpec);
        System.out.println("Downloading: " + forDownload);
        updateProgress.accept(0, 1L);
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
        updateProgress.accept(workDone, size);
        while (workDone < size) {
            long transferred = channel.transferFrom(rbc, workDone, count);
            workDone += transferred;
            updateMessage.accept(NumberFormat.getPercentInstance().format((double) workDone / size));
            updateProgress.accept(workDone, size);
        }
        channel.force(true);
    }

    private boolean extractZip(Path forExtract) throws IOException {
        updateTitle.accept("Rozpakowywanie " + forExtract.getFileName());
        updateProgress.accept(0, 1);
        updateMessage.accept("");
        try (ZipFile zip = new ZipFile(forExtract.toFile())) {
            updateProgress.accept(0, Files.size(forExtract));

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
            updateProgress.accept(1d, 1d);
            updateTitle.accept("Zakończono wypakowywanie archiwum");
        }
        updateProgress.accept(Double.NaN, 0);
        return Files.deleteIfExists(forExtract);
    }

    private static long getTotal(ZipFile zip) throws IOException {
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
            updateProgress.accept(++workDone, total);
            updateMessage.accept(NumberFormat.getPercentInstance().format((double) workDone / total));
        }
    }
}
