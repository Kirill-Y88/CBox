package CoreCBox;

/*
Коды операций:
0 - вход/лог_ин
1 - выход/лог_аут
3 - удаление директории/delete
4 - создание директории/create

*/


public class CommandMessage implements Message {
    private int codeOperation;
    private String stringPath;

    public CommandMessage(int codeOperation, String stringPath) {
        this.codeOperation = codeOperation;
        this.stringPath = stringPath;
    }


    public int getCodeOperation() {
        return codeOperation;
    }


    public String getStringPath() {
        return stringPath;
    }
}
