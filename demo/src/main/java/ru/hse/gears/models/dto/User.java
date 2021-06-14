package ru.hse.gears.models.dto;

public class User {
    public static class UserInformation {
        public String token;
        public Long id;

        public UserInformation(String token, Long id) {
            this.token = token;
            this.id = id;
        }
    }

    private Long id;
    private String username;
    private String password;
    private Long points;
    private Long totalNumberOfGames;
    private Long numberOfGamesWon;
    private Long numberOfGamesLost;


    public User() {
    }

    public User(Long id, String username,
                String password, Long points,
                Long totalNumberOfGames, Long numberOfGamesWon,
                Long numberOfGamesLost) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.points = points;
        this.totalNumberOfGames = totalNumberOfGames;
        this.numberOfGamesWon = numberOfGamesWon;
        this.numberOfGamesLost = numberOfGamesLost;
    }

    public User(User user) {
        this.id = user.id;
        this.username = user.username;
        this.password = user.password;
        this.points = user.points;
        this.totalNumberOfGames = user.totalNumberOfGames;
        this.numberOfGamesWon = user.numberOfGamesWon;
        this.numberOfGamesLost = user.numberOfGamesLost;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public Long getPoints() {
        return points;
    }

    public Long getTotalNumberOfGames() {
        return totalNumberOfGames;
    }

    public Long getNumberOfGamesWon() {
        return numberOfGamesWon;
    }

    public Long getNumberOfGamesLost() {
        return numberOfGamesLost;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPoints(Long points) {
        this.points = points;
    }

    public void setTotalNumberOfGames(Long totalNumberOfGames) {
        this.totalNumberOfGames = totalNumberOfGames;
    }

    public void setNumberOfGamesWon(Long numberOfGamesWon) {
        this.numberOfGamesWon = numberOfGamesWon;
    }

    public void setNumberOfGamesLost(Long numberOfGamesLost) {
        this.numberOfGamesLost = numberOfGamesLost;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}