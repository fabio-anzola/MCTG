package at.fhtw.mctg.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;

@Getter
public class CardModel {

    @JsonAlias({"pk_card_id"})
    private int cardId;

    @JsonAlias({"name"})
    private String name;

    @JsonAlias({"damage"})
    private int damage;

    @JsonAlias({"card_type"})
    private CardType type;

    @JsonAlias({"element_type"})
    private Elements element;

    @JsonAlias({"is_active"})
    private boolean active;

    @JsonAlias({"fk_pk_user_id"})
    private int userId;

    @JsonAlias({"fk_pk_user_id"})
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
    public CardModel(int cardId, String name, int damage, CardType type, Elements element, boolean active, int userId, int packageId) {
        this.cardId = cardId;
        this.name = name;
        this.damage = damage;
        this.type = type;
        this.element = element;
        this.active = active;
        this.userId = userId;
        this.packageId = packageId;
    }
}
