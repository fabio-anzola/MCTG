package at.fhtw.mctg.model;

/**
 * Represents the type of a card in the system.
 * A card can be classified as either a MONSTER or a SPELL type.
 * This classification is used across different entities such as trades and cards
 * to specify or filter the type of card.
 */
public enum CardType {
    MONSTER,
    SPELL
}
