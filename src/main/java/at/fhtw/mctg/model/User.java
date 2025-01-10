package at.fhtw.mctg.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents a User entity with attributes such as user ID, username, password, name, bio, image, wallet, and Elo score.
 * This class is primarily used for managing user-related data and interactions within the system.
 *
 * Fields annotated with @JsonAlias allow for JSON field mapping during serialization and deserialization.
 * Lombok annotations @Getter and @ToString are applied to generate boilerplate code for getter methods and string representation functionality.
 * Individual attributes such as password, name, bio, image, wallet, and Elo score have setter methods for updating their values.
 */
@Getter
@ToString
public class User {
    @JsonAlias({"userId"})
    private int userId;

    @JsonAlias({"Username"})
    private String username;

    @JsonAlias({"Password"})
    @Setter
    private String password;

    @JsonAlias({"Name"})
    @Setter
    private String name;

    @JsonAlias({"Bio"})
    @Setter
    private String bio;

    @JsonAlias({"Image"})
    @Setter
    private String image;

    @JsonAlias({"Wallet"})
    @Setter
    private int wallet;

    @JsonAlias({"Elo"})
    @Setter
    private int elo;

    /**
     * Constructor to set all attributes
     *
     * @param userId the users id
     * @param username the username
     * @param password has of the password
     * @param wallet nr of coins
     * @param elo elo of the user
     */
    public User(int userId, String username, String password, String name, String bio, String image, int wallet, int elo) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.name = name;
        this.bio = bio;
        this.image = image;
        this.wallet = wallet;
        this.elo = elo;
    }

    /**
     * Default Constructor needed by Lombok
     */
    public User() {
    }
}
