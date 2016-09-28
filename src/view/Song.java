package view;

/**
 * Created by Lian on 9/27/16.
 */
public class Song {

    public String name, artist, album;
    public int year;

    public Song(){

        this.name = "empty";
        this.artist = "empty";
        this.album = " ";
        this.year = 0;
    }

    public Song(String name, String artist, String album, int year){

        this.name = name;
        this.artist = artist;
        this.album = album;
        this.year = year;
    }

    public String getName(){
        return name;
    }

    public String getArtist(){
        return artist;
    }

    public String getAlbum(){
        return album;
    }

    public int getYear(){
        return year;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setArtist(String artist){
        this.artist = artist;
    }

    public void setAlbum(String album){
        this.album = album;
    }

    public void setYear(int year){
        this.year = year;
    }
}
