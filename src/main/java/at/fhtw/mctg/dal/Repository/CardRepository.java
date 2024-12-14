package at.fhtw.mctg.dal.Repository;

import at.fhtw.mctg.dal.DataAccessException;
import at.fhtw.mctg.dal.UnitOfWork;
import at.fhtw.mctg.model.Card;
import at.fhtw.mctg.model.CardType;
import at.fhtw.mctg.model.Elements;
import at.fhtw.mctg.model.User;

import java.lang.annotation.ElementType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class CardRepository {
    private UnitOfWork unitOfWork;

    public CardRepository(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

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
}
