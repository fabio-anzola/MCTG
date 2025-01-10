package at.fhtw.mctg.dal.Repository;

import at.fhtw.mctg.dal.DataAccessException;
import at.fhtw.mctg.dal.UnitOfWork;
import at.fhtw.mctg.model.Card;
import at.fhtw.mctg.model.CardType;
import at.fhtw.mctg.model.Elements;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * The CardRepository class provides methods for CRUD operations on Card objects
 * persisting data in a relational database. It provides functionality to create,
 * retrieve, and update card data for users and card packages.
 * <p>
 * This repository utilizes a UnitOfWork instance to manage database transactions
 * and prepared statements for secure and efficient database operations.
 */
public class CardRepository {
    private final UnitOfWork unitOfWork;

    /**
     * Constructs a new CardRepository instance.
     *
     * @param unitOfWork the UnitOfWork instance used for managing database transactions
     */
    public CardRepository(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    /**
     * Creates a new card entry in the database.
     *
     * @param card the card object containing the information to be persisted.
     *             This includes attributes such as card ID, name, card type,
     *             damage value, element type, and the associated package ID.
     */
    public void createCard(Card card) {
        try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                """
                        INSERT INTO "card" (pk_card_id, name, card_type, damage ,element_type, fk_pk_package_id) VALUES (?, ?, ?::cardTypes, ?, ?::elementTypes, ?)
                        """)) {
            preparedStatement.setString(1, card.getCardId());
            preparedStatement.setString(2, card.getName());
            preparedStatement.setString(3, card.getType().name());
            preparedStatement.setInt(4, card.getDamage());
            preparedStatement.setString(5, card.getElement().name());
            preparedStatement.setInt(6, card.getPackageId());


            preparedStatement.execute();

        } catch (SQLException e) {
            throw new DataAccessException("Insert not successful", e);
        }
    }

    /**
     * Retrieves a collection of cards from the database that match the specified card ID.
     *
     * @param id the unique identifier of the card to be retrieved.
     * @return a collection of {@code Card} objects that match the provided card ID.
     * Returns an empty collection if no cards match the specified ID.
     * @throws DataAccessException if a database access error occurs or the query execution fails.
     */
    public Collection<Card> getCardById(String id) {
        try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                """
                            select * from "card" where pk_card_id = ?
                        """)) {

            preparedStatement.setString(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            Collection<Card> rows = new ArrayList<>();

            while (resultSet.next()) {
                Card card = new Card(
                        resultSet.getString(1), // id
                        resultSet.getString(2), // name
                        resultSet.getInt(3), // damage
                        CardType.valueOf(resultSet.getString(4)), // Card Type as Class CardType
                        Elements.valueOf(resultSet.getString(5)), // Element Type as class Elements
                        resultSet.getBoolean(6), // active
                        resultSet.getInt(7), // owner
                        resultSet.getInt(8) // package
                );

                rows.add(card);
            }

            return rows;
        } catch (SQLException e) {
            throw new DataAccessException("Select not successful", e);
        }
    }

    /**
     * Updates the ownership of all cards within a specific package,
     * assigning them to a given user.
     *
     * @param packageId the unique identifier for the card package whose cards are to be acquired.
     * @param userId    the unique identifier of the user to whom the cards will be assigned.
     * @throws DataAccessException if a database access error occurs during the update operation.
     */
    public void acquireMultipleCards(int packageId, int userId) {
        try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                """
                        UPDATE "card" SET fk_pk_user_id = ? WHERE fk_pk_package_id = ?;
                        """)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, packageId);

            preparedStatement.execute();

        } catch (SQLException e) {
            throw new DataAccessException("Update not successful", e);
        }
    }

    /**
     * Retrieves a collection of cards associated with a specific user ID from the database.
     *
     * @param userId the unique identifier of the user whose cards are to be retrieved
     * @return a collection of {@code Card} objects associated with the specified user ID.
     * Returns an empty collection if the user has no associated cards.
     * @throws DataAccessException if a database access error occurs or the query execution fails
     */
    public Collection<Card> getCardsByUserId(int userId) {
        try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                """
                            SELECT pk_card_id, "name", damage, card_type, element_type, is_active, fk_pk_user_id, fk_pk_package_id
                                                             FROM card
                                                             WHERE fk_pk_user_id = ?;
                        """)) {

            preparedStatement.setInt(1, userId);

            ResultSet resultSet = preparedStatement.executeQuery();

            Collection<Card> cardRows = new ArrayList<>();

            while (resultSet.next()) {
                Card card = new Card(
                        resultSet.getString(1), // ID
                        resultSet.getString(2), // NAME
                        resultSet.getInt(3),    // DAMAGE
                        CardType.valueOf(resultSet.getString(4)), // TYPE
                        Elements.valueOf(resultSet.getString(5)), // ELEMENT
                        resultSet.getBoolean(6),// ACTIVE
                        resultSet.getInt(7),    // UID
                        resultSet.getInt(8)     // PACKID
                );

                cardRows.add(card);
            }

            return cardRows;
        } catch (SQLException e) {
            throw new DataAccessException("Select not successful", e);
        }
    }

    /**
     * Retrieves a collection of active cards associated with a specific user ID.
     *
     * @param userId the unique identifier of the user whose active cards are to be retrieved
     * @return a collection of {@code Card} objects that are active and associated with the specified user ID.
     * Returns an empty collection if the user has no active cards.
     * @throws DataAccessException if a database access error occurs or the query execution fails
     */
    public Collection<Card> getActiveCardsByUserId(int userId) {
        try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                """
                            SELECT pk_card_id, "name", damage, card_type, element_type, is_active, fk_pk_user_id, fk_pk_package_id
                                                             FROM card
                                                             WHERE is_active = TRUE AND fk_pk_user_id = ?;
                        """)) {

            preparedStatement.setInt(1, userId);

            ResultSet resultSet = preparedStatement.executeQuery();

            Collection<Card> cardRows = new ArrayList<>();

            while (resultSet.next()) {
                Card card = new Card(
                        resultSet.getString(1), // ID
                        resultSet.getString(2), // NAME
                        resultSet.getInt(3),    // DAMAGE
                        CardType.valueOf(resultSet.getString(4)), // TYPE
                        Elements.valueOf(resultSet.getString(5)), // ELEMENT
                        resultSet.getBoolean(6),// ACTIVE
                        resultSet.getInt(7),    // UID
                        resultSet.getInt(8)     // PACKID
                );

                cardRows.add(card);
            }

            return cardRows;
        } catch (SQLException e) {
            throw new DataAccessException("Select not successful", e);
        }
    }

    /**
     * Marks a card as active in the database by updating its "is_active" status to TRUE.
     *
     * @param cardId the unique identifier of the card to be set as active
     * @throws DataAccessException if a database access error occurs or the update operation fails
     */
    public void setCardAsActive(String cardId) {
        try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                """
                        UPDATE "card" SET is_active = TRUE WHERE pk_card_id = ?;
                        """)) {
            preparedStatement.setString(1, cardId);

            preparedStatement.execute();

        } catch (SQLException e) {
            throw new DataAccessException("Update not successful", e);
        }
    }

    /**
     * Updates the owner of a specific card in the database by changing its associated user ID.
     *
     * @param cardId the unique identifier of the card whose ownership is being updated
     * @param uid    the unique identifier of the new owner of the card
     * @throws DataAccessException if a database access error occurs or the update operation fails
     */
    public void updateOwner(String cardId, int uid) {
        try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                """
                        UPDATE "card" SET fk_pk_user_id = ? WHERE pk_card_id = ?;
                        """)) {
            preparedStatement.setInt(1, uid);
            preparedStatement.setString(2, cardId);

            preparedStatement.execute();

        } catch (SQLException e) {
            throw new DataAccessException("Update not successful", e);
        }
    }
}
