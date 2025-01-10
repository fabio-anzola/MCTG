package at.fhtw.mctg.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;

/**
 * Represents the relationship between a user and a battle.
 * This class captures the user participating in a battle, the associated battle,
 * and the user's status in the battle (e.g., win, loss, tie, or pending).
 *
 * Instances of this class can be created either with default values or with specific
 * details such as user ID, battle ID, and battle status.
 *
 * This entity is often used in operations like querying the participants of a battle
 * and determining the current state or outcome of a battle for each user.
 */
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
