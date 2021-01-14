package pl.koder95.eme.au;

import javafx.application.Platform;
import javafx.concurrent.Task;
import pl.koder95.eme.Main;

import java.io.*;
import java.net.URISyntaxException;

public class AutoUpdateTask extends Task<Object> {
    @Override
    protected Object call() throws Exception {
        return null;
    }

    private static void restart() {
        Platform.exit();
        try {
            final String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
            File self = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(javaBin, "-jar", self.getName());
            builder.start();
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    private static void generateUpdateScript() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("update.bat"))) {
            writer.write("@echo off");
            writer.newLine();
            writer.write("timeout /T 5 /nobreak > nil");
            writer.newLine();
            writer.write("copy ");
        } catch (IOException ex) {

        }
    }
}
