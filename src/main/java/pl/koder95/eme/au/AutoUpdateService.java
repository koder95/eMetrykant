package pl.koder95.eme.au;

import javafx.concurrent.Service;

public class AutoUpdateService extends Service<Object> {
    @Override
    protected AutoUpdateTask createTask() {
        return null;
    }
}
