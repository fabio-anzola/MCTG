package at.fhtw.mctg.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;

@Getter
public class CardPack {

    @JsonAlias({"packageId"})
    private int packageId;

    @JsonAlias({"name"})
    private String name;

    @JsonAlias({"price"})
    private int price;

    public CardPack(int packageId, String name, int price) {
        this.packageId = packageId;
        this.name = name;
        this.price = price;
    }

    public CardPack() {
    }
}