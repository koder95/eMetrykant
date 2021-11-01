package pl.koder95.eme.au;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import org.kohsuke.github.GHAsset;
import org.kohsuke.github.GHRelease;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.NumberFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static pl.koder95.eme.Files.*;

public class AutoUpdateTask extends Task<Object> {

    @Override
    protected Object call() throws Exception {
        GHAsset zip;
        try {
            GitHubRepositoryController controller = new GitHubRepositoryController("koder95", "eMetrykant");
            controller.init();
            GHRelease latest = controller.getRepository().getLatestRelease();
            zip = getGithubAsset(latest);
        } catch (IOException e) {
            e.printStackTrace();
            showException(e);
            return null;
        }

        // pobieranie zip'a
        Path downloaded = downloadZip(zip.getBrowserDownloadUrl(), TEMP_DIR, zip.getName(), zip.getSize());
        // rozpakowywanie do folderu tymczasowego
        if (extractZip(downloaded, TEMP_DIR, true)) {
            Map<Path, Path> updateMap = new HashMap<>();
            Files.list(TEMP_DIR).forEach(path -> {
                updateMap.put(path, WORKDIR.resolve(path.getFileName()));
            });
            generateUpdateScript(UPDATE_WIN, updateMap);
        }
        restart();
        return null;
    }

    private void showException(Exception e) {
        showException(e, "Nie można pobrać eMetrykanta.");
    }

    private void showException(Exception e, String title) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(title == null? "Wyjątek" : title);
        a.setHeaderText(e.getClass().getName());
        a.setContentText(e.getLocalizedMessage());
        a.show();
    }

    private GHAsset getGithubAsset(GHRelease latest) throws IOException {
        return latest.listAssets().toList().stream().reduce(null, (r, c) -> {
            System.out.println(c);
            return isMainZip(c) ? c : r;
        });
    }

    private boolean isMainZip(GHAsset c) {
        return c.getName().contains("eMetrykant") && c.getName().endsWith(".zip");
    }

    private Path downloadZip(String url, Path dir, String name, long size) throws IOException {
        NumberFormat pF = NumberFormat.getPercentInstance();
        updateTitle("Pobieranie " + name);
        updateProgress(0, 1);

        URLConnection connection = new URL(url).openConnection();
        Path forDownload = dir.resolve(name);
        if (Files.notExists(forDownload.getParent()))
            Files.createDirectories(forDownload.getParent());
        if (Files.notExists(forDownload))
            Files.createFile(forDownload);

        try (ReadableByteChannel rbc = Channels.newChannel(connection.getInputStream());
             FileChannel channel = FileChannel.open(forDownload, StandardOpenOption.WRITE)) {
                channel.truncate(size);
                long workDone = 0;
                long count = size > 100? size / 100: 1;
                while (workDone < size) {
                    long transferred = channel.transferFrom(rbc, workDone, count);
                    workDone += transferred;
                    updateMessage(pF.format((double) workDone/ size));
                    updateProgress(workDone, size);
            }
            channel.force(true);
        } catch (Exception ex) { ex.printStackTrace(); }

        return forDownload;
    }

    private boolean extractZip(Path forExtract, Path dir, boolean deleteZip) throws IOException {
        NumberFormat pF = NumberFormat.getPercentInstance();
        updateTitle("Rozpakowywanie " + forExtract.getFileName());
        updateProgress(0, 1);
        updateMessage("");
        try (ZipFile zip = new ZipFile(forExtract.toFile())) {
            updateProgress(0, Files.size(forExtract));
            double total = 0;
            if (dir == null) dir = forExtract.getParent();
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.isDirectory()) {
                    if (Files.isDirectory(Files.createDirectories(dir.resolve(entry.getName()))))
                        System.out.println("Directory created!");
                }

                InputStream input = zip.getInputStream(entry);
                int available = input.available();
                total += available;
            }
            entries = zip.entries();
            double work = 0;
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                Path outFile = dir.resolve(entry.getName());

                if (!entry.isDirectory()) {
                    if (Files.isRegularFile(Files.createFile(outFile))) {
                        System.out.println("New file created: " + outFile);
                    }
                }
                else continue;

                System.out.println("Entry: " + entry);
                InputStream input = zip.getInputStream(entry);
                try (OutputStream output = Files.newOutputStream(outFile)) {
                    while (input.available() > 0) {
                        int b = input.read();
                        output.write(b);
                        output.flush();
                        updateProgress(++work, total);
                        updateMessage(pF.format(work/total));
                    }
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        }
        updateProgress(Double.NaN, 0);
        if (deleteZip) return Files.deleteIfExists(forExtract);
        return false;
    }

    private static void restart() {
        Platform.exit();
        try {
            new ProcessBuilder(UPDATE_WIN.toString()).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    private static void generateUpdateScript(Path update, Map<Path, Path> updateMap) {
        try (BufferedWriter writer = Files.newBufferedWriter(update, StandardOpenOption.WRITE)) {
            generateWinUpdateScript(writer, updateMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void generateWinUpdateScript(BufferedWriter writer, Map<Path, Path> updateMap)
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
            } else throw new FileNotFoundException("Nie znaleziono odpowiednich plików do aktualizacji.");
        }
        final String javaBin = '"' + System.getProperty("java.home") + File.separator + "bin" + File.separator;
        writer.write(javaBin + "java.exe\" -jar " + '"' + SELF + '"');
        writer.newLine();
    }
}
