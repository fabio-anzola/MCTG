package at.fhtw.mctg.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.ToString;

/**
 * Represents a user session containing the username and password for authentication purposes.
 * The Session class is used to encapsulate the credentials to be passed within the application.
 *
 * This class supports deserialization from JSON keys "Username" and "Password" using @JsonAlias annotations.
 *
 * It provides:
 * - A parameterized constructor to initialize username and password.
 * - A no-argument constructor for default instantiation.
 */
@Getter
@ToString
public class Session {

    @JsonAlias({"Username"})
    private String username;

    @JsonAlias({"Password"})
    private String password;

    /**
     * Constructs a new Session with the specified username and password.
     *
     * @param username the username associated with the session
     * @param password the password associated with the session
     */
    public Session(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Default Constructor needed by Lombok
     */
    public Session() {
    }
}
