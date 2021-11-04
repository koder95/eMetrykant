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
import org.kohsuke.github.*;
import pl.koder95.eme.Version;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.NoSuchObjectException;
import java.util.Optional;
/**
 * Klasa pobiera informacje o repozytorium GitHub. Dostarcza również wartości
 * jako {@link ReadOnlyObjectProperty}.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.4.1, 2021-11-05
 * @since 0.4.1
 */
public final class RepositoryInfo {

    private static final RepositoryInfo INFO = new RepositoryInfo("eMetrykant", ".zip");
    private static final String DEFAULT_USER = "koder95";

    public static RepositoryInfo get() {
        return INFO;
    }

    private final ReadOnlyObjectWrapper<Version> latestReleaseVersion = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyObjectWrapper<URL> latestReleaseURL = new ReadOnlyObjectWrapper<>();
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

    public URL getLatestReleaseURL() {
        return latestReleaseURLProperty().get();
    }

    public ReadOnlyObjectProperty<URL> latestReleaseURLProperty() {
        return latestReleaseURL.getReadOnlyProperty();
    }

    private synchronized void reload(String tagName, GHAsset latestZip) throws MalformedURLException {
        latestReleaseURL.set(new URL(latestZip.getBrowserDownloadUrl()));
        latestReleaseVersion.set(Version.parse(tagName));
    }

    private void reload(GHRelease latest) throws IOException {
        Optional<GHAsset> asset = latest.listAssets().toSet().stream()
                .filter(a -> a.getName().startsWith(assetPrefix) && a.getName().endsWith(assetSuffix))
                .findAny();
        if (asset.isPresent()) reload(latest.getTagName(), asset.get());
    }

    public void reload(GHRepository repository) throws IOException {
        GHRelease latest = repository.getLatestRelease();
        if (latest != null) reload(latest);
    }

    public void reload(GitHub gitHub, String user, String repository) throws IOException {
        GHUser ghu = gitHub.getUser(user);
        if (ghu == null)
            throw new IOException(new NoSuchObjectException("Nie znaleziono użytkownika: " + user));
        GHRepository ghr = ghu.getRepository(repository);
        if (ghr == null)
            throw new IOException(new NoSuchObjectException("Nie znaleziono repozytorium: " + repository));
        else reload(ghr);
    }

    public void reload(GitHub gitHub, String repository) throws IOException {
        reload(gitHub, DEFAULT_USER, repository);
    }

    public void reload(GitHub gitHub) throws IOException {
        reload(gitHub, assetPrefix);
    }

    public void reload() throws IOException {
        reload(GitHub.connectAnonymously());
    }
}
