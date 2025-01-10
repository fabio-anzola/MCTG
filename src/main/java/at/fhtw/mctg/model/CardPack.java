package at.fhtw.mctg.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;

/**
 * Represents a card package in the system.
 *
 * A card package consists of a unique identifier, a name, and a price.
 * This class is used to encapsulate the details of a card package and
 * can be used in various operations like fetching a package from the
 * database or managing package transactions.
 *
 * Fields of this class are annotated with {@code @JsonAlias} to support
 * serialization or deserialization with alternative JSON property names.
 */
@Getter
public class CardPack {

    @JsonAlias({"packageId"})
    private int packageId;

    @JsonAlias({"name"})
    private String name;

    @JsonAlias({"price"})
    private int price;

    /**
     * Constructs a CardPack object with the specified package ID, name, and price.
     *
     * @param packageId the unique identifier of the card package
     * @param name the name of the card package
     * @param price the price of the card package
     */
    public CardPack(int packageId, String name, int price) {
        this.packageId = packageId;
        this.name = name;
        this.price = price;
    }

    /**
     * Default Constructor needed by Lombok
     */
    public CardPack() {
    }
}