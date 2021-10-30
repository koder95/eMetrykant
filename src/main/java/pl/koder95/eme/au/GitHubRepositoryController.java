package pl.koder95.eme.au;

import org.kohsuke.github.*;

import java.io.IOException;
import java.net.HttpURLConnection;

public class GitHubRepositoryController {

    private GitHub createNewConnection() throws IOException {
        return new GitHubBuilder().withRateLimitHandler(new RateLimitHandler() {
            @Override
            public void onError(IOException e, HttpURLConnection uc) {
                e.printStackTrace();
                System.out.println(uc);
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
        if (user == null) throw new IllegalStateException("User is not selected.");
        else repo = user.getRepository(repositoryName);
    }

    public void init() {
        try {
            selectUser();
            selectRepository();
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public GHRepository getRepository() {
        return repo;
    }
}
