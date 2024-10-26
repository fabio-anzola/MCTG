package at.fhtw.mctg.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Session {

    @JsonAlias({"Username"})
    private String username;

    @JsonAlias({"Password"})
    private String password;

    public Session(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Session() {
    }
}
