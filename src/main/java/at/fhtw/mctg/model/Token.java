package at.fhtw.mctg.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.ToString;

/**
 * The Token class represents an authentication token associated with a user.
 * It contains information about the token's ID, value, expiration date,
 * creation date, and the owner (user ID) of the token.
 *
 * This class is primarily used for managing authentication tokens within
 * the context of session handling and user authentication mechanisms.
 *
 * Fields:
 * - pk_token_id: The unique identifier for the token.
 * - token: The value of the token.
 * - expires: The expiration date and time of the token.
 * - created: The creation date and time of the token.
 * - owner: The ID of the user who owns the token.
 *
 * This class supports JSON serialization/deserialization for the following field aliases:
 * - "tokenId" for pk_token_id
 * - "token" for token
 * - "expires" for expires
 * - "created" for created
 * - "user" for owner
 *
 * It provides constructors for initializing the token object with specific values
 * or using the default no-argument constructor.
 */
@Getter
@ToString
public class Token {

    @JsonAlias({"tokenId"})
    private int pk_token_id;

    @JsonAlias({"token"})
    private String token;

    @JsonAlias({"expires"})
    private String expires;

    @JsonAlias({"created"})
    private String created;

    @JsonAlias({"user"})
    private int owner;

    /**
     * Constructs a new Token instance with the specified values.
     *
     * @param pk_token_id the unique identifier for the token
     * @param token the value of the token
     * @param expires the expiration date and time of the token
     * @param created the creation date and time of the token
     * @param owner the ID of the user who owns the token
     */
    public Token(int pk_token_id, String token, String expires, String created, int owner) {
        this.pk_token_id = pk_token_id;
        this.token = token;
        this.expires = expires;
        this.created = created;
        this.owner = owner;
    }

    public Token() {
    }
}
