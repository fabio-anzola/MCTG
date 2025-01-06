package at.fhtw.mctg.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
public class Trade {

    @JsonAlias({"tradeId"})
    private int tradeId;

    @JsonAlias({"initiatorId"})
    private int initiatorId;

    @JsonAlias({"partnerId"})
    private int partnerId;

    @JsonAlias({"senderCardId"})
    private String senderCardId;

    @JsonAlias({"receiverCardId"})
    private String receiverCardId;

    @JsonAlias({"status"})
    private TradeStatus status;

    @JsonAlias({"timeCreated"})
    private Timestamp timeCreated;

    @JsonAlias({"timeCompleted"})
    private Timestamp timeCompleted;

    @JsonAlias({"requestedType"})
    private CardType requestedType;

    @JsonAlias({"requestedDamage"})
    private int requestedDamage;

    public Trade(int tradeId, int initiatorId, int partnerId, String senderCardId, String receiverCardId, TradeStatus status, Timestamp timeCreated, Timestamp timeCompleted, CardType requestedType, int requestedDamage) {
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
