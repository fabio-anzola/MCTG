package at.fhtw.mctg.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
public class Trade {

    @JsonAlias({"Id"})
    private String tradeId;

    @JsonAlias({"initiatorId"})
    @Setter
    private int initiatorId;

    @JsonAlias({"partnerId"})
    private int partnerId;

    @JsonAlias({"CardToTrade"})
    @Getter
    private String senderCardId;

    @JsonAlias({"receiverCardId"})
    private String receiverCardId;

    @JsonAlias({"status"})
    @Setter
    private TradeStatus status;

    @JsonAlias({"timeCreated"})
    private Timestamp timeCreated;

    @JsonAlias({"timeCompleted"})
    private Timestamp timeCompleted;

    @JsonAlias({"Type"})
    private CardType requestedType;

    @JsonAlias({"MinimumDamage"})
    private int requestedDamage;

    public Trade(String tradeId, int initiatorId, int partnerId, String senderCardId, String receiverCardId, TradeStatus status, Timestamp timeCreated, Timestamp timeCompleted, CardType requestedType, int requestedDamage) {
        this.tradeId = tradeId;
        this.initiatorId = initiatorId;
        this.partnerId = partnerId;
        this.senderCardId = senderCardId;
        this.receiverCardId = receiverCardId;
        this.status = status;
        this.timeCreated = timeCreated;
        this.timeCompleted = timeCompleted;
        this.requestedType = requestedType;
        this.requestedDamage = requestedDamage;
    }

    public Trade() {
    }
}
