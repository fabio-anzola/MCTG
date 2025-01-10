package at.fhtw.mctg.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;

/**
 * Represents a user's statistical data including their username, name, elo ranking,
 * number of wins, losses, and ties.
 *
 * This class is designed to be used to encapsulate data retrieved from a database
 * or other source of user statistics.
 */
@Getter
public class Stats {
    @JsonAlias({"Username"})
    private String username;

    @JsonAlias({"Name"})
    private String name;

    @JsonAlias({"Elo"})
    private int elo;

    @JsonAlias({"Wins"})
    private int wins;

    @JsonAlias({"Losses"})
    private int losses;

    @JsonAlias({"Ties"})
    private int ties;

    /**
     * Default Constructor needed by Lombok
     */
    public Stats() {
    }

    /**
     * Constructor to set all args
     *
     * @param username username
     * @param name user set name
     * @param elo elo int
     * @param wins wins int
     * @param losses loss int
     * @param ties ties int
     */
    public Stats(String username, String name, int elo, int wins, int losses, int ties) {
        this.username = username;
        this.name = name;
        this.elo = elo;
        this.wins = wins;
        this.losses = losses;
        this.ties = ties;
    }
}
