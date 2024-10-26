package at.fhtw.mctg.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.ToString;

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
