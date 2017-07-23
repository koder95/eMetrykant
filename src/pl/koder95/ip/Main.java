/*
 * Ten utwór jest dostępny na licencji
 * Creative Commons BY-NC-SA 4.0 Międzynarodowe.
 * Aby zapoznać się z tekstem licencji wejdź na stronę
 * http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */
package pl.koder95.ip;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.nio.charset.Charset;
import java.text.Collator;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version %I%, %G%
 */
public class Main {
    public static final ResourceBundle BUNDLE
            = ResourceBundle.getBundle("pl/koder95/ip/strings");
    public static final Locale POLISH = Locale.forLanguageTag("PL-pl"); //NOI18N
    public static final File WORKDIR
            = new File(System.getProperty("user.dir")); //NOI18N
    public static final File DATA_DIR = new File(WORKDIR, "data"); //NOI18N
    public static final Charset CSV_OLD_CHARSET
            = Charset.forName("CP852"); //NOI18N
    public static final Charset CSV_NEW_CHARSET
            = Charset.forName("UTF-8"); //NOI18N
    public static final Charset CSV_DEFAULT_CHARSET = CSV_NEW_CHARSET;
    public static final Collator DEFAULT_COLLATOR
            = Collator.getInstance(POLISH); //NOI18N
    public static final Image FAVICON = Toolkit.getDefaultToolkit().createImage(
            ClassLoader.getSystemResource("pl/koder95/ip/favicon.png"));
    public static final Object READ_CSV_ERR_MESSAGE
            = BUNDLE.getString("ERR_IMPORTANT_FILE_NOT_FOUND");
    public static final String READ_CSV_ERR_TITLE
            = BUNDLE.getString("ERR_IMPORTANT_FILE_NOT_FOUND_TITLE");
    public static final int OPTION_BI = 1, OPTION_OC = 0,
            OPTION_ZM = 2, OPTION_ZA = 3;
    public static final Pattern DIGITS_STRING_PATTERN = Pattern.compile( "([0-9]*)");
    
    static {
        try {
            setSystemLookAndFeel();
        } catch (ClassNotFoundException | InstantiationException 
                | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            showErrorMessage(null, ex.getLocalizedMessage(),
                    BUNDLE.getString("ERR_GUI_TITLE"));
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        MainFrame.main(args);
    }
    
    public static int showErrorMessage(java.awt.Component parentComponent,
            Object message, String title) {
        java.awt.Toolkit.getDefaultToolkit().beep();
        javax.swing.JOptionPane.showMessageDialog(parentComponent, message,
                title, javax.swing.JOptionPane.ERROR_MESSAGE);
        return -1;
    }
    
    public static void showErrorMessage(java.awt.Component parentComponent,
            Object message, String title, boolean exit) {
        int status = showErrorMessage(parentComponent, message, title);
        if (exit) System.exit(status);
    }
    
    public static int showErrorMessage(Object message, String title) {
        java.awt.Toolkit.getDefaultToolkit().beep();
        javax.swing.JOptionPane.showMessageDialog(null, message, title,
                javax.swing.JOptionPane.ERROR_MESSAGE);
        return -1;
    }
    
    public static void showErrorMessage(Object message, String title,
            boolean exit) {
        int status = showErrorMessage(null, message, title);
        if (exit) System.exit(status);
    }
    
    public static void setSystemLookAndFeel() throws ClassNotFoundException,
            InstantiationException, IllegalAccessException,
            UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
}
