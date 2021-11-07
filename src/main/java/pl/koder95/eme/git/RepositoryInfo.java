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
package pl.koder95.eme.git;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import org.kohsuke.github.GHAsset;
import org.kohsuke.github.GHRelease;
import org.kohsuke.github.GHRepository;
import pl.koder95.eme.Version;

import java.io.IOException;
import java.util.Optional;

/**
 * Klasa pobiera informacje o repozytorium GitHub. Dostarcza również wartości
 * jako {@link ReadOnlyObjectProperty}.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.4.1, 2021-11-07
 * @since 0.4.1
 */
public final class RepositoryInfo {

    private static final RepositoryInfo INFO = new RepositoryInfo("eMetrykant", ".zip");
    private static final String DEFAULT_USER = "koder95";

    /**
     * @return instancja singleton {@link RepositoryInfo}
     */
    public static RepositoryInfo get() {
        return INFO;
    }

    private final ReadOnlyObjectWrapper<Version> latestReleaseVersion = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyObjectWrapper<String> latestReleaseBrowserURL = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyObjectWrapper<Long> latestReleaseSize = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyObjectWrapper<String> latestReleaseName = new ReadOnlyObjectWrapper<>();
    private final String assetPrefix;
    private final String assetSuffix;

    private RepositoryInfo(String assetPrefix, String assetSuffix) {
        this.assetPrefix = assetPrefix;
        this.assetSuffix = assetSuffix;
    }

    public Version getLatestReleaseVersion() {
        return latestReleaseVersionProperty().get();
    }

    public ReadOnlyObjectProperty<Version> latestReleaseVersionProperty() {
        return latestReleaseVersion.getReadOnlyProperty();
    }

    public String getLatestReleaseBrowserURL() {
        return latestReleaseBrowserURLProperty().get();
    }

    public ReadOnlyObjectProperty<String> latestReleaseBrowserURLProperty() {
        return latestReleaseBrowserURL.getReadOnlyProperty();
    }

    public Long getLatestReleaseSize() {
        return latestReleaseSizeProperty().get();
    }

    public ReadOnlyObjectProperty<Long> latestReleaseSizeProperty() {
        return latestReleaseSize.getReadOnlyProperty();
    }

    public String getLatestReleaseName() {
        return latestReleaseNameProperty().get();
    }

    public ReadOnlyObjectProperty<String> latestReleaseNameProperty() {
        return latestReleaseName.getReadOnlyProperty();
    }

    private void reload(String tagName, GHAsset latestZip) {
        latestReleaseBrowserURL.set(latestZip.getBrowserDownloadUrl());
        latestReleaseVersion.set(Version.parse(tagName));
        latestReleaseSize.set(latestZip.getSize());
        latestReleaseName.set(latestZip.getName());
    }

    private void reload(GHRelease latest) throws IOException {
        Optional<GHAsset> asset = latest.listAssets().toSet().stream()
                .filter(a -> a.getName().startsWith(assetPrefix) && a.getName().endsWith(assetSuffix))
                .findAny();
        asset.ifPresent(ghAsset -> reload(latest.getTagName(), ghAsset));
    }

    private void reload(GHRepository repository) throws IOException {
        GHRelease latest = repository.getLatestRelease();
        if (latest != null) reload(latest);
    }

    /**
     * Ponownie odczytuje informacje z serwerów GitHub i aktualizuje dane przechowywane w tym obiekcie.
     *
     * @throws IOException problemy z połączeniem lub pobieraniem danych z GitHub
     */
    public void reload() throws IOException {
        GitHubRepositoryController controller = new GitHubRepositoryController(DEFAULT_USER, assetPrefix);
        controller.init();
        reload(controller.getRepository());
    }
}
