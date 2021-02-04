package ClientCBox;

import CoreCBox.CommandMessage;
import CoreCBox.FileMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static javafx.scene.control.ButtonType.OK;

public class Controller implements Initializable {
    @FXML
    TextField host;
    @FXML
    TextField port;
    @FXML
    TextField login;
    @FXML
    TextField password;
    @FXML
     TextField pathClient;
    @FXML
     TableView<FileInfo> tableViewClient;
    @FXML
     TextField pathServer;
    @FXML
    TableView<FileInfo> tableViewServer;

    /*byte [] bArray =  new byte[3072];
    int countPack;
    int indexArray;
    String path;
    SocketChannel sChannel;
    Thread t;*/

    public TextField text;
    private ObjectEncoderOutputStream os;
    private ObjectDecoderInputStream is;
    private Network network;
    private boolean tableClientFocus = false;
    private boolean tableServerFocus = false;
    private String newDirectory;
    private Path pathUserRoot;
    private Path pathUserFull;
    private int countPathRoot;
    private String filePathToServer;
    private String filePathFromClient;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TableColumn<FileInfo, String> fileTypeColumn = new TableColumn<>(); // создание столбца
        fileTypeColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getType().getName()));
        //перевод из типа файл инфо в стринг
        fileTypeColumn.setPrefWidth(24);

        TableColumn<FileInfo, String> filenameColumn = new TableColumn<>("Имя");
        filenameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getFilename()));
        filenameColumn.setPrefWidth(240);

        TableColumn<FileInfo, Long> fileSizeColumn = new TableColumn<>("Размер");
        fileSizeColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getSize()));
        fileSizeColumn.setCellFactory(column -> {
            return new TableCell<FileInfo, Long>() {
                @Override
                protected void updateItem(Long item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        String text = String.format("%,d bytes", item);
                        if (item == -1L) {
                            text = "[DIR]";
                        }
                        setText(text);
                    }
                }
            };
        });
        fileSizeColumn.setPrefWidth(120);


        TableColumn<FileInfo, String> fileTypeColumn2 = new TableColumn<>(); // создание столбца
        fileTypeColumn2.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getType().getName()));
        //перевод из типа файл инфо в стринг
        fileTypeColumn2.setPrefWidth(24);

        TableColumn<FileInfo, String> filenameColumn2 = new TableColumn<>("Имя");
        filenameColumn2.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getFilename()));
        filenameColumn2.setPrefWidth(240);

        TableColumn<FileInfo, Long> fileSizeColumn2 = new TableColumn<>("Размер");
        fileSizeColumn2.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getSize()));
        fileSizeColumn2.setCellFactory(column -> {
            return new TableCell<FileInfo, Long>() {
                @Override
                protected void updateItem(Long item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        String text = String.format("%,d bytes", item);
                        if (item == -1L) {
                            text = "[DIR]";
                        }
                        setText(text);
                    }
                }
            };
        });
        fileSizeColumn2.setPrefWidth(120);

        TableColumn<FileInfo, String> fileSynhColumn2 = new TableColumn<>("Синхронизация"); // создание столбца
        fileSynhColumn2.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().isFileSynhronize()));
        fileSynhColumn2.setPrefWidth(120);




        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        TableColumn<FileInfo, String> fileDateColumn = new TableColumn<>("Дата изменения");
        fileDateColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getLastModified().format(dtf)));
        fileDateColumn.setPrefWidth(120);

        TableColumn<FileInfo, String> fileDateColumn2 = new TableColumn<>("Дата изменения");
        fileDateColumn2.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getLastModified().format(dtf)));
        fileDateColumn2.setPrefWidth(120);


        tableViewClient.getColumns().addAll(fileTypeColumn, filenameColumn, fileSizeColumn, fileDateColumn); //добавление созданных столбцов
        tableViewClient.getSortOrder().add(fileTypeColumn);

        tableViewServer.getColumns().addAll(fileTypeColumn2, filenameColumn2, fileSizeColumn2, fileDateColumn2, fileSynhColumn2); //добавление созданных столбцов
        tableViewServer.getSortOrder().add(fileTypeColumn2);

        tableViewClient.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                tableClientFocus = true;
                tableServerFocus = false;
            }
            if (event.getClickCount() == 2) {
                Path path = Paths.get(pathClient.getText()).resolve(tableViewClient.getSelectionModel().getSelectedItem().getFilename());
                if (Files.isDirectory(path)) {
                    updateListClient(path);
                }
            }
        });


        tableViewServer.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                tableClientFocus = false;
                tableServerFocus = true;
            }
            if (event.getClickCount() == 2) {
                Path path = Paths.get(pathServer.getText()).resolve(tableViewServer.getSelectionModel().getSelectedItem().getFilename());
                if (Files.isDirectory(path)) {
                    updateListServer(path);
                }
                int size = path.getNameCount();
                System.out.println(size);
                System.out.println(path);
                pathUserFull =  path.subpath(countPathRoot + 1 , size);
                System.out.println(pathUserRoot);
                System.out.println(pathUserFull);
            }
        });

        updateListClient(Paths.get("./"));
       // updateListServer(Paths.get("Client/Clients"));
        tableViewServer.setVisible(false);
        countPathRoot = (Paths.get(".").toAbsolutePath()).getNameCount();
        System.out.println(Paths.get("./").toAbsolutePath());
        System.out.println(countPathRoot);
    }

    /*public void updateList(Path path) {
        try {
            pathServer.setText(path.normalize().toAbsolutePath().toString());
            tableViewServer.getItems().clear();
            tableViewServer.getItems().addAll(Files.list(path).map(FileInfo::new).collect(Collectors.toList()));
            //запускаем стрим по указанному пути/преобразуем все полученное в объекты файл инфо/ упаковвываем полученные данные в коллекцию Лист
            tableViewServer.sort();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "По какой-то причине не удалось обновить список файлов", ButtonType.OK);
            alert.showAndWait();
        }
        try {
            pathClient.setText(path.normalize().toAbsolutePath().toString());
            tableViewClient.getItems().clear();
            tableViewClient.getItems().addAll(Files.list(path).map(FileInfo::new).collect(Collectors.toList()));
            //запускаем стрим по указанному пути/преобразуем все полученное в объекты файл инфо/ упаковвываем полученные данные в коллекцию Лист
            tableViewClient.sort();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "По какой-то причине не удалось обновить список файлов", ButtonType.OK);
            alert.showAndWait();
        }
    }*/

    public void updateListClient(Path path) {
        try {
            pathClient.setText(path.normalize().toAbsolutePath().toString());
            tableViewClient.getItems().clear();
            tableViewClient.getItems().addAll(Files.list(path).map(FileInfo::new).collect(Collectors.toList()));
            //запускаем стрим по указанному пути/преобразуем все полученное в объекты файл инфо/ упаковвываем полученные данные в коллекцию Лист
            tableViewClient.sort();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "По какой-то причине не удалось обновить список файлов", OK);
            alert.showAndWait();
        }
    }

    public void updateListServer(Path path) {
        try {
            pathServer.setText(path.normalize().toAbsolutePath().toString());
            tableViewServer.getItems().clear();
            tableViewServer.getItems().addAll(Files.list(path).map(FileInfo::new).collect(Collectors.toList()));
            //запускаем стрим по указанному пути/преобразуем все полученное в объекты файл инфо/ упаковвываем полученные данные в коллекцию Лист
            tableViewServer.sort();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "По какой-то причине не удалось обновить список файлов", OK);
            alert.showAndWait();
        }
    }






    public void connect(ActionEvent actionEvent) {
    network = new Network(host.getText(), port.getText(), this);

    }

    public void disconnect(ActionEvent actionEvent) {
    network.disconnect();

    }

    public void log_in(ActionEvent actionEvent){
        String loginUser = login.getText();
        String passwordUser = password.getText();
        network.sendCommand(new CommandMessage(0, loginUser, passwordUser));

    }

    public void log_in_auth(String loginUser){

        File directoryUser = new File("Client/Clients/" + loginUser);
        pathUserRoot = Paths.get(directoryUser.getName());
        pathUserFull = pathUserRoot;
        if (directoryUser.isDirectory()){
            tableViewServer.setVisible(true);
            updateListServer(Paths.get(directoryUser.getPath()));

        }else {
            Path createNewDirectory = Paths.get(directoryUser.getPath());
            try {
                Files.createDirectory(createNewDirectory);
                tableViewServer.setVisible(true);
                updateListServer(createNewDirectory);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }



    /*public void log_in(ActionEvent actionEvent){
        String loginUser = login.getText();
        String passwordUser = password.getText();
        File directoryUser = new File("Client/Clients/" + loginUser);
        pathUserRoot = Paths.get(directoryUser.getName());
        pathUserFull = pathUserRoot;
        if (directoryUser.isDirectory()){
            tableViewServer.setVisible(true);
            updateListServer(Paths.get(directoryUser.getPath()));
            network.sendCommand(new CommandMessage(0, loginUser, passwordUser));
        }else {
            Path createNewDirectory = Paths.get(directoryUser.getPath());
            try {
                Files.createDirectory(createNewDirectory);
                tableViewServer.setVisible(true);
                updateListServer(createNewDirectory);
                network.sendCommand(new CommandMessage(0, loginUser, passwordUser));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }*/





    public void log_out(ActionEvent actionEvent){
        String loginUser = login.getText();
        tableViewServer.setVisible(false);
        pathServer.clear();
        pathUserRoot = null;
        network.sendCommand(new CommandMessage(1, loginUser));
    }

    public void upClient(ActionEvent actionEvent) {
        Path upperPath = Paths.get(pathClient.getText()).getParent();
        if (upperPath != null) {
            updateListClient(upperPath);
        }
    }

    public void upServer(ActionEvent actionEvent) {
        Path upperPath = Paths.get(pathServer.getText()).getParent();
        Path thisPathName = Paths.get(pathServer.getText()).getFileName();
        if(thisPathName.equals(pathUserRoot)){
            return;
        } else if (upperPath != null ) {
            updateListServer(upperPath);
            int size = upperPath.getNameCount();
            System.out.println(size);
            System.out.println(pathUserFull);
            pathUserFull =  upperPath.subpath(countPathRoot + 1 , size);
            System.out.println(pathUserRoot);
            System.out.println(pathUserFull);
        }
    }

    public void copy(ActionEvent actionEvent) {
        Path srcPath = null;
        Path dstPath = null;

        if(tableClientFocus == true){
            filePathToServer = (pathUserFull.resolve(tableViewClient.getSelectionModel().getSelectedItem().getFilename()).toString());
          //  filePathFromClient = Paths.get(pathClient.getText()).resolve((tableViewClient.getSelectionModel().getSelectedItem().getFilename()));
            srcPath = Paths.get(pathClient.getText(), tableViewClient.getSelectionModel().getSelectedItem().getFilename());
            dstPath = (Paths.get(pathServer.getText()).getParent()).resolve(filePathToServer);




        }
        if(tableServerFocus == true){
            srcPath = Paths.get(pathServer.getText(), tableViewServer.getSelectionModel().getSelectedItem().getFilename());
            dstPath = Paths.get(pathClient.getText()).resolve(tableViewServer.getSelectionModel().getSelectedItem().getFilename());

        }

        try {

            System.out.println("откуда " + srcPath);
            System.out.println("куда " + dstPath);

            Files.copy(srcPath, dstPath);
            updateListServer(Paths.get(pathServer.getText()));
            updateListClient(Paths.get(pathClient.getText()));


            if(tableClientFocus == true){
                /*try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
                System.out.println(" отправка на сервер" + filePathToServer);
                network.sendFile(filePathToServer);
            }

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Не удалось скопировать указанный файл, файл с таким названием уже существует", ButtonType.OK);
            alert.showAndWait();
        }









/*
        //String messageContent = text.getText();
        LocalDateTime sendAt = LocalDateTime.now();
        //text.clear();
        path = "Server/src/main/java/ServerCBox/photo.jpg";
        try (FileInputStream fis = new FileInputStream("Client/src/main/java/ClientCBox/photo.jpg")) {
            indexArray = 0;
            while ( (indexArray = fis.read(bArray)) > 0){
                //os.writeObject
                sChannel.writeAndFlush
                        (
                        new FileMessage(
                                path,
                                "fish",
                                sendAt,
                                bArray,
                                countPack,
                                indexArray,
                                false)
                );
               // os.flush();
                countPack++;
            }
            indexArray = 0;
            //os.writeObject
            sChannel.writeAndFlush(
                    new FileMessage(
                            path,
                            "Fish",
                            sendAt,
                            new byte[]{},
                            countPack,
                            indexArray,
                            true)
            );
           // os.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }*/

    }

    public void delete(ActionEvent actionEvent) {
        Path pathDelete = null;
        if(tableClientFocus == true){
            pathDelete = Paths.get(pathClient.getText(), tableViewClient.getSelectionModel().getSelectedItem().getFilename());
        try{

            Files.delete(pathDelete);
            updateListClient(Paths.get(pathClient.getText()));

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Не удалось удалить указанный файл", ButtonType.OK);
            alert.showAndWait();
        }
        }
        if(tableServerFocus == true){
            pathDelete = Paths.get(pathServer.getText(), tableViewServer.getSelectionModel().getSelectedItem().getFilename());
            try{
                network.sendCommand( new CommandMessage(3, pathUserFull.resolve(tableViewServer.getSelectionModel().getSelectedItem().getFilename()).toString()));
            Files.delete(pathDelete);
            updateListServer(Paths.get(pathServer.getText()));
         //   network.sendCommand(new CommandMessage(3, (pathUserFull.resolve(tableViewServer.getSelectionModel().getSelectedItem().getFilename())).toString()));
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Не удалось удалить указанный файл", ButtonType.OK);
            alert.showAndWait();
        }
        }


    }

    public void rename(ActionEvent actionEvent) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setWidth(550);
        dialog.setHeight(200);
        dialog.setTitle("Переименовать в");
        File newNameDir;
        File oldNameDir;
        if(tableClientFocus == true){
           Path oldNameDirectory = Paths.get(pathClient.getText(), tableViewClient.getSelectionModel().getSelectedItem().getFilename());
            dialog.showAndWait();
            Path newNameDirectory  = oldNameDirectory.getParent().resolve((dialog.getEditor()).getText());
            dialog.showAndWait();
            oldNameDir = new File(oldNameDirectory.toString());
            newNameDir = new File(newNameDirectory.toString());
            oldNameDir.renameTo(newNameDir);
            updateListClient(Paths.get(pathClient.getText()));
        }
        if(tableServerFocus == true){
            String fileName = tableViewServer.getSelectionModel().getSelectedItem().getFilename();

            Path oldNameDirectory = Paths.get(pathServer.getText(), fileName);
            dialog.showAndWait();
            String fileNameNew = (dialog.getEditor()).getText();
            Path newNameDirectory  = oldNameDirectory.getParent().resolve(fileNameNew);
            dialog.showAndWait();
            oldNameDir = new File(oldNameDirectory.toString());
            newNameDir = new File(newNameDirectory.toString());
            oldNameDir.renameTo(newNameDir);
            updateListServer(Paths.get(pathServer.getText()));
            network.sendCommand(new CommandMessage(5,
                    (pathUserFull.resolve(fileName)).toString(),
                    (pathUserFull.resolve(fileNameNew)).toString()));

            System.out.println("Collector old " + (pathUserFull.resolve(fileName)).toString());
            System.out.println("Collector new " + (pathUserFull.resolve(fileNameNew)).toString());

        }




    }

    public void create_directory(ActionEvent actionEvent) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setWidth(550);
        dialog.setHeight(200);
        dialog.setTitle("Наименование новой директории");
        if(tableClientFocus == true||tableServerFocus == true) {

            dialog.showAndWait();
            //dialog.resultConverterProperty();
            // if(((dialog.resultConverterProperty()).getValue()).toString().equals(OK.getText())) {  //не понял как стащить события нажатия какой либо кнопки с окна
            newDirectory = (dialog.getEditor()).getText();
        /*}else {
            newDirectory = "Опять не работает";
        }*/

            Path createNewDirectory = null;
            if (tableServerFocus) {
                createNewDirectory = Paths.get(pathServer.getText(), "/" + newDirectory);
                try {
                    Files.createDirectory(createNewDirectory);
                    updateListServer(createNewDirectory.getParent());
                    System.out.println(pathUserFull + " путь юзера");

                    System.out.println("новая директория на отправку версия 2  " +  pathUserFull.resolve(newDirectory));
                   network.sendCommand( new CommandMessage(4, (pathUserFull.resolve(newDirectory)).toString()));

                 //   network.sendCommand(new CommandMessage(0, loginUser));

                } catch (IOException e) {
                    e.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Директория с таким именем уже существует", ButtonType.OK);
                    alert.showAndWait();
                }
            }
            if (tableClientFocus) {
                createNewDirectory = Paths.get(pathClient.getText(), "/" + newDirectory);
                try {
                    Files.createDirectory(createNewDirectory);
                    updateListClient(createNewDirectory.getParent());
                } catch (IOException e) {
                    e.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Директория с таким именем уже существует", ButtonType.OK);
                    alert.showAndWait();
                }
            }
        }else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Выберите поле для создания директории", ButtonType.OK);
            alert.showAndWait();

        }
    }



    public void exitAction(){

        Platform.exit();
    }
}
