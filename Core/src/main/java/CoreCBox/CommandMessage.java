package CoreCBox;

/*
Коды операций:
0 - вход/лог_ин
1 - выход/лог_аут
3 - удаление директории/delete
4 - создание директории/create
5 - переименование/rename

*/


public class CommandMessage implements Message {
    private int codeOperation;
    private String stringPath;
    private String stringPath2;

    public CommandMessage(int codeOperation, String stringPath) {
        this.codeOperation = codeOperation;
        this.stringPath = stringPath;
    }

    public CommandMessage(int codeOperation, String stringPath, String stringPath2) {
        this.codeOperation = codeOperation;
        this.stringPath = stringPath;
        this.stringPath2 = stringPath2;
    }


    public int getCodeOperation() {
        return codeOperation;
    }


    public String getStringPath() {
        return stringPath;
    }

    public String getStringPath2() {
        return stringPath2;
    }
}
