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

import org.kohsuke.github.*;

import java.io.IOException;
import java.net.HttpURLConnection;

import static pl.koder95.eme.Main.BUNDLE;

/**
 * Klasa dostarcza metod kontrolujących połączenie z repozytorium GitHub.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.4.1, 2021-11-07
 * @since 0.4.1
 */
public class GitHubRepositoryController {

    private GitHub createNewConnection() throws IOException {
        return new GitHubBuilder().withRateLimitHandler(new RateLimitHandler() {
            @Override
            public void onError(IOException e, HttpURLConnection uc) {
                System.out.println(uc);
                e.printStackTrace();
            }
        }).build();
    }

    private GitHub connection = null;

    GitHub getConnection() {
        while(connection == null) {
            try {
                connection = createNewConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }

    final String username;
    final String repositoryName;

    /**
     * Tworzy kontroler dla repozytorium o nazwie {@code repositoryName}, którego właścicielem jest użytkownik
     * o nazwie {@code username}.
     *
     * @param username nazwa właściciela repozytorium
     * @param repositoryName nazwa repozytorium
     */
    public GitHubRepositoryController(String username, String repositoryName) {
        this.username = username;
        this.repositoryName = repositoryName;
    }

    private GHUser user = null;

    private void selectUser() throws IOException {
        GitHub connection = getConnection();
        if (connection.isOffline()) {
            this.connection = null;
            selectUser();
        }
        else {
            user = connection.getUser(username);
        }
    }

    private GHRepository repo = null;

    private void selectRepository() throws IOException {
        if (user == null) throw new IllegalStateException(BUNDLE.getString("THR_USER_NOT_SELECTED"));
        else repo = user.getRepository(repositoryName);
    }

    /**
     * Wprowadza podstawowe ustawienia do połączenia z repozytorium: wybiera użytkownika i repozytorium.
     */
    public void init() {
        try {
            selectUser();
            selectRepository();
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * @return repozytorium GitHub
     */
    public GHRepository getRepository() {
        return repo;
    }
}
