package com.codecool.database;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RadioCharts {
    private final String url;
    private final String user;
    private final String password;

    private final List<Artist> artists = new ArrayList<>();
    private final List<Song> songs = new ArrayList<>();

    public RadioCharts(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    private void getDataFromDatabase() {
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            String sql = "SELECT artist, song, times_aired FROM music_broadcast";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            Artist artist;
            Song song;

            while (resultSet.next()) {
                String artistName = resultSet.getString("artist");
                String songTitle = resultSet.getString("song");
                int timesAired = resultSet.getInt("times_aired");

                artist = artists.stream().filter(a -> a.getName().equals(artistName)).findFirst().orElse(new Artist(artistName));
                artist.addSongTitle(songTitle);
                artists.add(artist);

                song = songs.stream().filter(s -> s.getTitle().equals(songTitle)).findFirst().orElse(new Song(songTitle, 0));
                song.addAirTime(timesAired);
                songs.add(song);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getMostPlayedSong() {
        getDataFromDatabase();

        String mostPlayedSong = "";
        int mostPlayed = 0;

        for (Song song : songs) {
            if (song.getTimesAired() > mostPlayed) {
                mostPlayedSong = song.getTitle();
                mostPlayed = song.getTimesAired();
            }
        }

        return mostPlayedSong;
    }

    public String getMostActiveArtist() {
        getDataFromDatabase();

        String mostActiveArtist = "";
        int mostAmount = 0;

        for (Artist artist : artists) {
            if (artist.getAmountOfSongs() > mostAmount) {
                mostActiveArtist = artist.getName();
                mostAmount = artist.getAmountOfSongs();
            }
        }

        return mostActiveArtist;
    }
}
