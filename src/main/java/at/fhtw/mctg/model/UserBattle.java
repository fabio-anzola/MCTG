package at.fhtw.mctg.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;

public class UserBattle {
    @JsonAlias({"userId"})
    @Getter
    private int userId;

    @JsonAlias({"battleId"})
    private int battleId;

    @JsonAlias({"status"})
    @Getter
    private BattleStatus status;

    /**
     * Default Constructor needed by Lombok
     */
    public UserBattle() {
    }

    /**
     * @param userId the int of single user id
     * @param battleId the int of the accosciated battle
     * @param status status - can be null when pending
     */
    public UserBattle(int userId, int battleId, BattleStatus status) {
        this.userId = userId;
        this.battleId = battleId;
        this.status = status;
    }
}
