package at.fhtw.mctg.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;

@Getter
public class Trade {

    @JsonAlias({"tradeId"})
    private int tradeId;

    @JsonAlias({"initiatorId"})
    private int initiatorId;

    @JsonAlias({"partnerId"})
    private int partnerId;

    @JsonAlias({"senderCardId"})
    private int senderCardId;

    @JsonAlias({"receiverCardId"})
    private int receiverCardId;

    @JsonAlias({"status"})
    private TradeStatus status;

    @JsonAlias({"timeCreated"})
    private String timeCreated;

    @JsonAlias({"timeCompleted"})
    private String timeCompleted;

    public Trade(int tradeId, int initiatorId, int partnerId, int senderCardId, int receiverCardId, TradeStatus status, String timeCreated, String timeCompleted) {
        this.tradeId = tradeId;
        this.initiatorId = initiatorId;
        this.partnerId = partnerId;
        this.senderCardId = senderCardId;
        this.receiverCardId = receiverCardId;
        this.status = status;
        this.timeCreated = timeCreated;
        this.timeCompleted = timeCompleted;
    }

    public Trade() {
    }
}
