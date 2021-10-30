package pl.koder95.eme.au;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import org.kohsuke.github.GHAsset;
import org.kohsuke.github.GHRelease;
import pl.koder95.eme.Files;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static pl.koder95.eme.Files.SELF;
import static pl.koder95.eme.Files.TEMP_DIR;

public class AutoUpdateTask extends Task<Object> {
    @Override
    protected Object call() throws Exception {
        GHAsset zip;
        try {
            GitHubRepositoryController controller = new GitHubRepositoryController("koder95", "eMetrykant");
            controller.init();
            GHRelease latest = controller.getRepository().getLatestRelease();
            zip = latest.listAssets().toList().stream().reduce(null, (r, c) -> {
                System.out.println(c);
                return c.getName().contains("eMetrykant") && c.getName().endsWith(".zip")? c : r;
            });
        } catch (IOException e) {
            e.printStackTrace();
            Alert a = new Alert(Alert.AlertType.ERROR, "Nie można pobrać eMetrykanta.");
            a.show();
            return null;
        }

        // pobieranie zip'a
        Path downloaded = downloadZip(zip.getBrowserDownloadUrl(), TEMP_DIR, zip.getName(), zip.getSize());
        // rozpakowywanie do folderu tymczasowego
        if (extractZip(downloaded, TEMP_DIR, true)) {
            // TODO: tworzenie mapy aktualizacji
            Map<Path, Path> updateMap = new HashMap<>();
            updateMap.put(TEMP_DIR.resolve("eMetrykant.jar"), SELF);
			
            restart(updateMap);
        }
        return null;
    }

    private Path downloadZip(String url, Path dir, String name, long size) throws IOException {
        NumberFormat pF = NumberFormat.getPercentInstance();
        String title = "Pobieranie " + name;
        updateTitle(title);
        updateProgress(0, 1);

        URLConnection connection = new URL(url).openConnection();
        Path forDownload = dir.resolve(name);
        java.nio.file.Files.createFile(forDownload);

        try (InputStream in = connection.getInputStream();
             ReadableByteChannel rbc = Channels.newChannel(in);
             FileChannel channel = FileChannel.open(forDownload)) {
                channel.truncate(size);
                in.mark((int) size);
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
            updateProgress(0, java.nio.file.Files.size(forExtract));
            double total = 0;
            if (dir == null) dir = forExtract.getParent();
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                System.out.println(entry);
                if (entry.isDirectory()) {
                    if (java.nio.file.Files.isDirectory(java.nio.file.Files.createDirectories(dir.resolve(entry.getName()))))
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
                    if (java.nio.file.Files.isRegularFile(java.nio.file.Files.createFile(outFile))) {
                        System.out.println("New file created: " + outFile);
                    }
                }
                else continue;

                System.out.println("Entry: " + entry);
                InputStream input = zip.getInputStream(entry);
                try (OutputStream output = java.nio.file.Files.newOutputStream(outFile)) {
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
        if (deleteZip) return java.nio.file.Files.deleteIfExists(forExtract);
        return false;
    }

    private static void restart(Map<Path, Path> updateMap) {
        Platform.exit();
        try {
            File update = Files.UPDATE_WIN;
            generateUpdateScript(update, updateMap);
            new ProcessBuilder(update.getPath()).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    private static void generateUpdateScript(File update, Map<Path, Path> updateMap) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(update))) {
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
            if (java.nio.file.Files.exists(newFile)) {
                writer.write("copy " + '"');
                writer.write(newFile.toString());
                writer.write('"' + " " + '"');
                writer.write(oldFile.toString());
                writer.write('"' + " /y");
                writer.newLine();
            } else throw new FileNotFoundException("Nie znaleziono odpowiednich plików do aktualizacji.");
        }
        final String javaBin = '"' + System.getProperty("java.home") + File.separator + "bin" + File.separator;
        writer.write(javaBin + "java.exe\" -jar " + '"' + Files.SELF + '"');
        writer.newLine();
    }
}
