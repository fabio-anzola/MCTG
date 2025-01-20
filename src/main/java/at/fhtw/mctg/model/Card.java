package at.fhtw.mctg.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a card which can be used in a game.
 * The card contains attributes such as unique identifier,
 * name, damage points, card type, element type, active state,
 * user ownership, and associated package details.
 */
@Getter
@Setter
public class Card {

    @JsonAlias({"Id"})
    private String cardId;

    @JsonAlias({"Name"})
    private String name;

    @JsonAlias({"Damage"})
    @Setter
    private int damage;

    @JsonAlias({"card_type"})
    @Setter
    private CardType type;

    @JsonAlias({"element_type"})
    @Setter
    private Elements element;

    @JsonAlias({"is_active"})
    private boolean active;

    @JsonAlias({"fk_pk_user_id"})
    private int userId;

    @JsonAlias({"fk_pk_package_id"})
    @Setter
    private int packageId;

    /**
     * Constructor for every Object Attribute
     * @param cardId unique card Id
     * @param name card nice name
     * @param damage damage
     * @param type enum card type
     * @param element enum element type
     * @param active if card is active in deck
     * @param userId fk for the owner
     * @param packageId fk for the package
     */
    public Card(String cardId, String name, int damage, CardType type, Elements element, boolean active, int userId, int packageId) {
        this.cardId = cardId;
        this.name = name;
        this.damage = damage;
        this.type = type;
        this.element = element;
        this.active = active;
        this.userId = userId;
        this.packageId = packageId;
    }

    /**
     * Default Constructor needed by Lombok
     */
    public Card() {
    }
}
