package CoreCBox;

import java.io.File;
import java.nio.file.Path;

/*
Коды операций:
0 - вход/лог_ин


*/


public class CommandMessage implements Message {
    private int codeOperation;
    private Path pathDir;
    private Path createNewDirectory;
    private Path pathDelete;
    private Path oldNameDirectory;
    private Path newNameDirectory;
    private String login;

    public CommandMessage(int codeOperation, String login) {
        this.codeOperation = codeOperation;
        this.login = login;
    }

    public int getCodeOperation() {
        return codeOperation;
    }

    public Path getPathDir() {
        return pathDir;
    }

    public Path getCreateNewDirectory() {
        return createNewDirectory;
    }

    public Path getPathDelete() {
        return pathDelete;
    }

    public Path getOldNameDirectory() {
        return oldNameDirectory;
    }

    public Path getNewNameDirectory() {
        return newNameDirectory;
    }

    public String getLogin() {
        return login;
    }
}
