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

public class Controller {

    @FXML ListView<String> listView;// ListView for song library display
    private ObservableList<String> songList;

    @FXML TableView<Song> tableView;// tableView for song detail display
    private ObservableList<Song> detailList;

    @FXML TableColumn<Song, String> nameColumn;
    @FXML TableColumn<Song, String> artistColumn;
    @FXML TableColumn<Song, String> albumColumn;
    @FXML TableColumn<Song, Integer> yearColumn;

    @FXML TextField nameInput;
    @FXML TextField artistInput;
    @FXML TextField albumInput;
    @FXML TextField yearInput;

    public void start(Stage mainStage) throws FileNotFoundException {

        // Scan the song list file
        File songListFile = new File("src/view/songListFile.txt");
        Scanner s = new Scanner(songListFile);
        String line;

        // create an ObservableList from an ArrayList
        songList = FXCollections.observableArrayList();
        detailList = FXCollections.observableArrayList();

        // set Column cell value
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        artistColumn.setCellValueFactory(new PropertyValueFactory<>("artist"));
        albumColumn.setCellValueFactory(new PropertyValueFactory<>("album"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));

        // set items for the song list and detail list
        listView.setItems(songList);

        // Add the song in the txt file to the lists
        while(s.hasNextLine()){
            line = s.nextLine();
            Song song = new Song(line, "adc", "asd", 1);
            songList.add(song.name);
            detailList.add(song);
        }

        s.close();

        // TODO: output the song list to a txt file

        // set listener for the items
        listView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> displaySongDetail());

        // Set the first song to be selected by default
        listView.getSelectionModel().select(0);
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

        // ObservableList for displaying info
        ObservableList<Song> displayList = FXCollections.observableArrayList();

        int i = 0;
        while( i< detailList.size() ){
            String name = detailList.get(i).name;
            if(name.equals(songName)){
                displayList.add(detailList.get(i));
                tableView.setItems(displayList);
            }
            i++;
        }
    }

    @FXML
    private void onAddButtonClicked(){// TODO: Add the song user entered to the list

        Song addSong = new Song();
        addSong.setName(nameInput.getText());
        addSong.setArtist(artistInput.getText());
        addSong.setAlbum(albumInput.getText());
        addSong.setYear(Integer.parseInt(yearInput.getText()));

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

        // TODO: ALERT IF SONG ALREADY EXIST
    }

    @FXML
    private void onDeleteButtonClicked(){
        String removeSong = listView.getSelectionModel().selectedItemProperty().getValue();
        songList.remove(removeSong);
        detailList.remove(removeSong);
    }

    @FXML
    private void onEditButtonClicked(){
        String editSong = listView.getSelectionModel().selectedItemProperty().getValue();
        songList.remove(editSong);
        songList.add(nameInput.getText());

        int i = 0;
        while(i < detailList.size()){
            if(editSong.equals(detailList.get(i).name)){
                detailList.get(i).setName(nameInput.getText());
                detailList.get(i).setAlbum(albumInput.getText());
                detailList.get(i).setArtist(artistInput.getText());
                detailList.get(i).setYear(Integer.parseInt(yearInput.getText()));
                break;
            }

            i++;
        }

        // Clear the input field
        nameInput.clear();
        artistInput.clear();
        albumInput.clear();
        yearInput.clear();

        // Sort the list
        Collections.sort(songList);
    }
}

