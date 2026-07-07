package pl.koder95.eme.bootstrap;

import java.text.Collator;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Konfiguracja aplikacji przekazywana przez IoC.
 */
public record AppConfig(
        ResourceBundle bundle,
        Locale locale,
        Collator collator
) {
}
