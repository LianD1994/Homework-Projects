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

    Song previous = new Song();
    Song edited= new Song();
    int flag=0;

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

        // set listener for the items
        listView.getSelectionModel().selectedItemProperty().addListener(((observable) -> displaySongDetail()));

        // Set the first song to be selected by default
        listView.getSelectionModel().select(0);
    }


    // Display info when song is selected
    // ----------------------------------
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

    @FXML
    private void onAddButtonClicked() throws IOException {

        Song addSong = new Song();
        addSong.setName(nameInput.getText());
        previous.setName(nameInput.getText());//save the song that is being added, used for cancel later//
        addSong.setArtist(artistInput.getText());
        previous.setArtist(artistInput.getText());
        addSong.setAlbum(albumInput.getText());
        previous.setAlbum(albumInput.getText());
        addSong.setYear(yearInput.getText());
        previous.setYear(yearInput.getText());


        for(int a=0; a<yearInput.getText().length();a++){
            if(!Character.isDigit(yearInput.getText().charAt(a))){
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Year has to be a number!");

                alert.showAndWait();
                return;
            }
        }

        if(nameInput.getText().isEmpty()||artistInput.getText().isEmpty()){
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Title and artist of the song cannot be empty!");

            alert.showAndWait();
            return;
        }else{

        }

        boolean a=false; //check if this song already exists//
        int j = 0;
        while(j < detailList.size()){
            if(detailList.get(j).name.equals(nameInput.getText())&&detailList.get(j).getArtist().equals(artistInput.getText())){
                a=true;
            }
            j++;
        }


        if(!a){ //if the song does not exist, process to add, else pop alert screen//

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
            // Find the last appearance of the song name (in the case of adding song with same song name)
            int appearance = 0;
            int tmp = 0;
            while(tmp < songList.size()){
                if(songList.get(tmp).equals(addSong.name)){
                    appearance++;
                }
                tmp++;
            }

            int checkAppearance = 0;
            for(i=0;i<songList.size();i++){
                if(songList.get(i).equals(addSong.name)){
                    checkAppearance++;
                    if(checkAppearance == appearance) {
                        listView.getSelectionModel().select(i);
                        break;
                    }
                }
            }
            flag=1;
        }else{

            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("This song is already in your library!");

            alert.showAndWait();
            return;
        }
    }

    @FXML
    private void onDeleteButtonClicked() throws IOException {

        String removeSong = listView.getSelectionModel().selectedItemProperty().getValue();

        // Remove the song name from songList
        songList.remove(removeSong);

        // Remove the song object from detailList
        int j = 0;
        while(j < detailList.size()){
            if(detailList.get(j).name.equals(removeSong)){
                previous=detailList.get(j);
                detailList.remove(detailList.get(j));
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

        flag=2;
    }

    @FXML
    private void onEditButtonClicked() throws IOException {
        String editSong = listView.getSelectionModel().selectedItemProperty().getValue();

        // Get the number of appearance for the song in the listView
        int index = listView.getSelectionModel().getSelectedIndex();
        int appearance = 0;
        int tmp = 0;
        while(tmp <= index){
            if(listView.getItems().get(tmp).equals(editSong)){
                appearance++;
            }
            tmp++;
        }

        displayList = FXCollections.observableArrayList();
        int checkAppearance = 0;

        int count = 0;
        int detailListIndex=0;
        while(count<appearance){
            if(listView.getSelectionModel().selectedItemProperty().getValue().equals(detailList.get(detailListIndex).getName())){
                count++;
            }
            detailListIndex++;
        }
        detailListIndex--;


        //checking if there is already a song with same name and artist//
        if(!nameInput.getText().isEmpty()&&!artistInput.getText().isEmpty()){//both not empty//

            count=0;
            while(count<detailList.size()){
                if(detailList.get(count).getName().equals(nameInput.getText())&&detailList.get(count).getArtist().equals(artistInput.getText())){
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("The song from the same artist is already in your library!");

                    alert.showAndWait();
                    return;
                }else{
                    count++;
                }
            }

        }else if(nameInput.getText().isEmpty()&&!artistInput.getText().isEmpty()){//name empty, artist not empty//

            count=0;
            while(count<detailList.size()){
                if(detailList.get(count).name.equals(editSong)&&detailList.get(count).getArtist().equals(artistInput.getText())){
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("The song from the same artist is already in your library!");

                    alert.showAndWait();
                    return;
                }else{
                    count++;
                }
            }
        }else if(!nameInput.getText().isEmpty()&&artistInput.getText().isEmpty()){//name not empty, artist empty//

            count=0;
            String artist = detailList.get(detailListIndex).getArtist();
            while(count<detailList.size()){
                if(detailList.get(count).getArtist().equals(artist)&&detailList.get(count).getName().equals(nameInput.getText())){
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("The song from the same artist is already in your library!");

                    alert.showAndWait();
                    return;
                }else{
                    count++;
                }
            }

        }

        for(int a=0; a<yearInput.getText().length();a++){
            if(!Character.isDigit(yearInput.getText().charAt(a))){
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Year has to be a number!");

                alert.showAndWait();
                return;
            }
        }

        previous=detailList.get(detailListIndex);


        // If name entered by user is not empty, then replace with new name
        // else leave name as it is
        if(! nameInput.getText().equals("")) {
            songList.set(index, nameInput.getText());
        }

        // Set the property to new value if not empty
        int i = 0;
        while(i < detailList.size()){
            if(editSong.equals(detailList.get(i).name)){
                checkAppearance++;
                if(checkAppearance == appearance) {
                    if (!nameInput.getText().equals("")) {
                        detailList.get(i).setName(nameInput.getText());
                        edited.setName(nameInput.getText());
                    }
                    if (!albumInput.getText().equals("")) {
                        detailList.get(i).setAlbum(albumInput.getText());
                        edited.setAlbum(albumInput.getText());
                    }
                    if (!artistInput.getText().equals("")) {
                        detailList.get(i).setArtist(artistInput.getText());
                        edited.setArtist(artistInput.getText());
                    }
                    if (!yearInput.getText().equals("")) {
                        detailList.get(i).setYear(yearInput.getText());
                        edited.setYear(yearInput.getText());
                    }
                    break;
                }
            }
            i++;
        }

        // Sort the list
        Collections.sort(songList);

        // Write the songList to the file after edition
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

        flag=3;

        // Select the newly edited item
        /*int appearance = 0;
        int tmp = 0;
        while(tmp < songList.size()){
            if(songList.get(tmp).equals(addSong.name)){
                appearance++;
            }
            tmp++;
        }

        int checkAppearance = 0;
        for(i=0;i<songList.size();i++){
            if(songList.get(i).equals(addSong.name)){
                checkAppearance++;
                if(checkAppearance == appearance) {
                    listView.getSelectionModel().select(i);
                    break;
                }
            }
        }*/

        System.out.println(listView.getSelectionModel().selectedItemProperty().getValue());
        String value = listView.getSelectionModel().selectedItemProperty().getValue();
        listView.getSelectionModel().select(0);
        System.out.println("after selecting index 0, value is " + listView.getSelectionModel().selectedItemProperty().getValue());
        listView.getSelectionModel().select(value);
        System.out.println("now select edited item, value is " + listView.getSelectionModel().selectedItemProperty().getValue());
    }

    @FXML //cancel function, Dennis//
    private void onCancelButtonClicked() throws IOException{

        if(flag==1){ //cancel add//
            int i=0;
            while(i<detailList.size()){
                if(detailList.get(i).getName().equals(previous.getName())&&detailList.get(i).getArtist().equals(previous.getArtist())){
                    detailList.remove(i);
                    songList.remove(previous.getName());
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
                    break;
                }
                i++;
            }
        }else if(flag==2){ //cancel delete//
            songList.add(previous.getName());
            detailList.add(previous);
            Collections.sort(songList);
            // Write the list to the file after deletion
            PrintWriter w = new PrintWriter("src/view/songListFile.txt");
            int i;
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
        }else if(flag==3){ //cancel edit//
            int i=0;
            while(i<detailList.size()){
                if(detailList.get(i).getName().equals(edited.getName())&&detailList.get(i).getArtist().equals(edited.getArtist())){
                    detailList.remove(i);
                    songList.remove(edited.getName());
                    detailList.add(previous);
                    songList.add(previous.getName());
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
                    break;
                }
                i++;
            }

        }else if(flag==4){ //already canceled//
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("You can only cancel once!");

            alert.showAndWait();
            return;
        }

        nameInput.clear();
        artistInput.clear();
        albumInput.clear();
        yearInput.clear();
        flag=4;
    }
}


