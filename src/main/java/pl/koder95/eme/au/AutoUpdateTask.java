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
        File downloaded = downloadZip(zip.getBrowserDownloadUrl(), TEMP_DIR, zip.getName(), zip.getSize());
        // rozpakowywanie do folderu tymczasowego
        if (extractZip(downloaded, TEMP_DIR, true)) {
            // TODO: tworzenie mapy aktualizacji
            Map<File, File> updateMap = new HashMap<>();
            updateMap.put(new File(TEMP_DIR, "eMetrykant.jar"), SELF);
            restart(updateMap);
        }
        return null;
    }

    private File downloadZip(String url, File dir, String name, long size) throws IOException {
        NumberFormat pF = NumberFormat.getPercentInstance();
        String title = "Pobieranie " + name;
        updateTitle(title);
        updateProgress(0, 1);

        URLConnection connection = new URL(url).openConnection();
        File forDownload = new File(dir, name);

        try (InputStream in = connection.getInputStream();
             ReadableByteChannel rbc = Channels.newChannel(in);
             FileOutputStream out = new FileOutputStream(forDownload);
             FileChannel channel = out.getChannel()) {
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

    private boolean extractZip(File forExtract, File dir, boolean deleteZip) throws IOException {
        NumberFormat pF = NumberFormat.getPercentInstance();
        updateTitle("Rozpakowywanie " + forExtract.getName());
        updateProgress(0, 1);
        updateMessage("");
        try (ZipFile zip = new ZipFile(forExtract)) {
            updateProgress(0, forExtract.length());
            double total = 0;
            if (dir == null) dir = forExtract.getParentFile();
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                System.out.println(entry);
                if (entry.isDirectory()) {
                    if (new File(dir, entry.getName()).mkdirs()) System.out.println("Directory created!");
                }

                InputStream input = zip.getInputStream(entry);
                int available = input.available();
                total += available;
            }
            entries = zip.entries();
            double work = 0;
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                File outFile = new File(dir, entry.getName());

                if (!entry.isDirectory()) {
                    if (outFile.createNewFile()) {
                        System.out.println("New file created: " + outFile);
                    }
                }
                else continue;

                System.out.println("Entry: " + entry);
                InputStream input = zip.getInputStream(entry);
                try (FileOutputStream output = new FileOutputStream(outFile)) {
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
        if (deleteZip) return forExtract.delete();
        return false;
    }

    private static void restart(Map<File, File> updateMap) {
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

    private static void generateUpdateScript(File update, Map<File, File> updateMap) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(update))) {
            generateWinUpdateScript(writer, updateMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void generateWinUpdateScript(BufferedWriter writer, Map<File, File> updateMap)
            throws IOException {
        writer.write("@echo off");
        writer.newLine();
        writer.write("timeout /T 5 /nobreak > nul");
        writer.newLine();
        for (Map.Entry<File, File> entry : updateMap.entrySet()) {
            File oldFile = entry.getValue();
            File newFile = entry.getKey();
            if (newFile.exists()) {
                writer.write("copy " + '"');
                writer.write(newFile.getPath());
                writer.write('"' + " " + '"');
                writer.write(oldFile.getPath());
                writer.write('"' + " /y");
                writer.newLine();
            } else throw new FileNotFoundException("Nie znaleziono odpowiednich plików do aktualizacji.");
        }
        final String javaBin = '"' + System.getProperty("java.home") + File.separator + "bin" + File.separator;
        writer.write(javaBin + "java.exe\" -jar " + '"' + Files.SELF + '"');
        writer.newLine();
    }
}
