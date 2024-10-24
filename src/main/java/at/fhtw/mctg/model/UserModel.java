package at.fhtw.mctg.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;

@Getter
public class UserModel {
    @JsonAlias({"pk_user_id"})
    private int userId;

    @JsonAlias({"username"})
    private String username;

    @JsonAlias({"password"})
    private String password;

    @JsonAlias({"wallet"})
    private int wallet;

    @JsonAlias({"elo"})
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
    public UserModel(int userId, String username, String password, int wallet, int elo) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.wallet = wallet;
        this.elo = elo;
    }
}
