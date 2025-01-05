package at.fhtw.mctg.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;

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
