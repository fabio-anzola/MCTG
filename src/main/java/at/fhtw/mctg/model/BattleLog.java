package at.fhtw.mctg.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;

/**
 * The BattleLog class represents a single entry in the log of a battle.
 * It contains information about the battle ID, the row number of the log entry,
 * and the textual content of the log entry.
 * This class is used to track individual log entries for a specific battle.
 */
public class BattleLog {
    @JsonAlias({"Id"})
    private int battleId;

    @JsonAlias({"RowNr"})
    @Getter
    private int rowNr;

    @JsonAlias({"row"})
    @Getter
    private String logRow;

    /**
     * Default Constructor needed by Lombok
     */
    public BattleLog() {
    }

    /**
     * @param battleId the battle id
     * @param rowNr the row nr
     * @param logRow the row line
     */
    public BattleLog(int battleId, int rowNr, String logRow) {
        this.battleId = battleId;
        this.rowNr = rowNr;
        this.logRow = logRow;
    }
}
