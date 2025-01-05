package at.fhtw.mctg.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

public class Battle {
    @JsonAlias({"Id"})
    @Getter
    private int battleId;

    @JsonAlias({"TimeStart"})
    private Timestamp startTime;

    @JsonAlias({"TimeEnd"})
    private Timestamp endTime;

    @JsonAlias({"Rounds"})
    private int rounds;

    @JsonAlias({"BattleLogId"})
    private int logId;

    /**
     * Default Constructor needed by Lombok
     */
    public Battle() {
    }

    /**
     * @param battleId battle id
     * @param startTime start time as timestamp
     * @param endTime end time as timestamp
     * @param rounds no of rounds
     * @param logId battle log id
     */
    public Battle(int battleId, Timestamp startTime, Timestamp endTime, int rounds, int logId) {
        this.battleId = battleId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.rounds = rounds;
        this.logId = logId;
    }
}