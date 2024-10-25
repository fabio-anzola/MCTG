package at.fhtw.mctg.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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
    private int wallet;

    @JsonAlias({"Elo"})
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
