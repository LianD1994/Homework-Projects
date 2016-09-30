package view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.stage.Stage;
import java.io.*;
import java.util.*;

public class Controller implements Serializable {

    @FXML ListView<String> listView;// ListView for song library display
    ObservableList<String> songList;
    ObservableList<Song> displayList;

    @FXML TableView<Song> tableView;// tableView for song detail display
    ArrayList<Song> detailList = new ArrayList<>();

    @FXML TableColumn<Song, String> nameColumn;
    @FXML TableColumn<Song, String> artistColumn;
    @FXML TableColumn<Song, String> albumColumn;
    @FXML TableColumn<Song, Integer> yearColumn;

    @FXML TextField nameInput;
    @FXML TextField artistInput;
    @FXML TextField albumInput;
    @FXML TextField yearInput;

    public void start(Stage mainStage) throws IOException, ClassNotFoundException {

        // Scan the song list file
        File songListFile = new File("src/view/songListFile.txt");
        Scanner s = new Scanner(songListFile);
        String line;

        // create an ObservableList from an ArrayList
        songList = FXCollections.observableArrayList();
        displayList = FXCollections.observableArrayList();

        // set Column cell value
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        artistColumn.setCellValueFactory(new PropertyValueFactory<>("artist"));
        albumColumn.setCellValueFactory(new PropertyValueFactory<>("album"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));

        // set items for the song list
        listView.setItems(songList);
        tableView.setItems(displayList);

        // Read the song from the txt file, add them to songList
        while(s.hasNextLine()){
            line = s.nextLine();
            songList.add(line);
        }
        s.close();

        // Sort the list after reading it from file
        Collections.sort(songList);

        // Read song Object list from file, add it to detailList
        FileInputStream fis = new FileInputStream("src/view/detailListFile.tmp");
        ObjectInputStream ois = new ObjectInputStream(fis);
        detailList = (ArrayList<Song>) ois.readObject();
        ois.close();

        // TODO==============================================
        for(int j=0;j<detailList.size();j++){
            System.out.println("detailList: " + detailList.get(j).name);
        }

        // set listener for the items
        listView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> displaySongDetail());

        // Set the first song to be selected by default
        listView.getSelectionModel().select(0);


        // TODO==============================
    }

    // Example from ppt
    /*private void showItem(Stage mainStage) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.initOwner(mainStage);
        alert.setTitle("List Item");
        alert.setHeaderText("Selected list item properties");

        String content = "Index: " +
                listView.getSelectionModel().getSelectedIndex() +
                "\nValue: " + listView.getSelectionModel().getSelectedItem();

        alert.setContentText(content);
        alert.showAndWait();
    }*/

    // Display info when song is selected
    private void displaySongDetail(){
        // Selected song name
        String songName = listView.getSelectionModel().selectedItemProperty().getValue();

        // Get the number of appearance for the song in the listView
        int index = listView.getSelectionModel().getSelectedIndex();
        int appearance = 0;
        int tmp = 0;
        while(tmp <= index){
            if(listView.getItems().get(tmp).equals(songName)){
                appearance++;
            }
            tmp++;
        }

        System.out.println(appearance);

        displayList = FXCollections.observableArrayList();//todo==========================
        int checkAppearance = 0;
        int i = 0;
        while( i< detailList.size() ){
            String name = detailList.get(i).name;
            if(name.equals(songName)){// TODO: SAME SONG NAME DIFFERENT ARTIST
                checkAppearance++;
                if(checkAppearance == appearance) {
                    displayList.add(detailList.get(i));
                    tableView.setItems(displayList);
                }
            }
            i++;
        }
    }


    // ON ADD BUTTON CLICKED
    // ---------------------
    @FXML
    private void onAddButtonClicked() throws IOException {

        Song addSong = new Song();

        // Set Song detail according to user input
        addSong.setName(nameInput.getText());
        addSong.setArtist(artistInput.getText());
        addSong.setAlbum(albumInput.getText());
        addSong.setYear(yearInput.getText());

        // add the song name to the song list, add the song to the detailed list
        songList.add(addSong.name);
        detailList.add(addSong);

        // Clear the input fields after song is added
        nameInput.clear();
        artistInput.clear();
        albumInput.clear();
        yearInput.clear();

        // Sort the song list
        Collections.sort(songList);

        // Write songList to a file
        PrintWriter w = new PrintWriter("src/view/songListFile.txt");
        int i = 0;
        while(i < songList.size()){
            w.println(songList.get(i));
            i++;
        }
        w.close();

        // Write detailList to a file
        FileOutputStream fos = new FileOutputStream("src/view/detailListFile.tmp");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(detailList);
        oos.close();

        // Select the newly added song
        for(i=0;i<songList.size();i++){
            if(songList.get(i).equals(addSong.name)){
                listView.getSelectionModel().select(i);
                break;
            }
        }
    }


    // ON DELETE BUTTON CLICKED
    // ------------------------
    @FXML
    private void onDeleteButtonClicked() throws IOException {
        String removeSong = listView.getSelectionModel().selectedItemProperty().getValue();

        // Remove the song name from songList
        songList.remove(removeSong);

        // Remove the song object from detailList
        int j = 0;
        while(j < detailList.size()){
            if(detailList.get(j).name.equals(removeSong)){
                detailList.remove(detailList.get(j));
                break;
            }
            j++;
        }

        // Sort the song list
        Collections.sort(songList);

        // Write the songList to the file after deletion
        PrintWriter w = new PrintWriter("src/view/songListFile.txt");
        int i = 0;
        while(i < songList.size()){
            w.println(songList.get(i));
            i++;
        }
        w.close();

        // Write the detailList to the file after deletion
        FileOutputStream fos = new FileOutputStream("src/view/detailListFile.tmp");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(detailList);
        oos.close();

        // Clear the tableView if songList is empty
        if(songList.size() == 0){
            ObservableList<Song> emptyList = FXCollections.emptyObservableList();
            tableView.setItems(emptyList);
        }
    }


    // ON EDIT BUTTON CLICKED
    // ----------------------
    @FXML
    private void onEditButtonClicked() throws IOException {
        String editSong = listView.getSelectionModel().selectedItemProperty().getValue();

        // If name entered by user is not empty, then replace with new name
        // else leave name as it is
        if(! nameInput.getText().equals("")) {
            int i = 0;
            while(i<songList.size()) {
                if(songList.get(i).equals(editSong)) {
                    songList.set(i, nameInput.getText());
                }
                i++;
            }
        }

        // Set the property to new value if not empty
        int i = 0;
        while(i < detailList.size()){
            if(editSong.equals(detailList.get(i).name)){
                if( ! nameInput.getText().equals("")) {
                    detailList.get(i).setName(nameInput.getText());
                }
                if( ! albumInput.getText().equals("")) {
                    detailList.get(i).setAlbum(albumInput.getText());
                }
                if( ! artistInput.getText().equals("")) {
                    detailList.get(i).setArtist(artistInput.getText());
                }
                if( ! yearInput.getText().equals("")) {
                    detailList.get(i).setYear(yearInput.getText());
                }
                break;
            }
            i++;
        }

        // TODO========================================
        ObservableList<Song> displayList1 = FXCollections.observableArrayList();
        displayList1.add(detailList.get(i));
        tableView.setItems(displayList1);

        // Sort the list
        Collections.sort(songList);

        // Write the list to the file after deletion
        PrintWriter w = new PrintWriter("src/view/songListFile.txt");
        i = 0;
        while(i < songList.size()){
            w.println(songList.get(i));
            i++;
        }
        w.close();

        // Write the detailList to the file after edition
        FileOutputStream fos = new FileOutputStream("src/view/detailListFile.tmp");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(detailList);
        oos.close();

        // Clear the input field
        nameInput.clear();
        artistInput.clear();
        albumInput.clear();
        yearInput.clear();
    }
}

