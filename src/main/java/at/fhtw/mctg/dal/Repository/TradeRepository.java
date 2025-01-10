package at.fhtw.mctg.dal.Repository;

import at.fhtw.mctg.dal.DataAccessException;
import at.fhtw.mctg.dal.UnitOfWork;
import at.fhtw.mctg.model.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * The TradeRepository class provides methods to interact with the underlying database layer
 * for operations related to trades. It serves as a repository for managing trade entities
 * within the system and leverages a UnitOfWork for managing database transactions.
 * <p>
 * This class supports various CRUD operations, including retrieving pending trades,
 * retrieving trades by ID or card ID, creating new trades, updating existing trades,
 * and deleting trades by ID.
 */
public class TradeRepository {
    private final UnitOfWork unitOfWork;

    /**
     * Constructs a new instance of TradeRepository with the specified UnitOfWork.
     *
     * @param unitOfWork the UnitOfWork to manage database transactions and connections
     */
    public TradeRepository(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    /**
     * Retrieves all trades that are currently in a pending state from the database.
     * <p>
     * The method executes a SQL query to select trades with a status of 'PENDING'
     * from the "trade" table. These trades are then mapped to Trade objects and
     * returned as a collection.
     *
     * @return a collection of all Trade objects with a status of 'PENDING'
     * @throws DataAccessException if there is an issue accessing or querying the database
     */
    public Collection<Trade> getPendingTrades() {
        try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                """
                            SELECT
                               pk_trade_id,
                               fk_pk_initiator_id,
                               fk_pk_tradepartner_id,
                               fk_pk_sendercard_id,
                               fk_pk_receivercard_id,
                               status,
                               time_start,
                               time_completed,
                               requested_type,
                               requested_damage
                           FROM "trade"
                           WHERE status = 'PENDING'
                        """)) {


            ResultSet resultSet = preparedStatement.executeQuery();

            Collection<Trade> rows = new ArrayList<>();

            while (resultSet.next()) {
                Trade trade = new Trade(
                        resultSet.getString("pk_trade_id"),
                        resultSet.getInt("fk_pk_initiator_id"),
                        resultSet.getInt("fk_pk_tradepartner_id"),
                        resultSet.getString("fk_pk_sendercard_id"),
                        resultSet.getString("fk_pk_receivercard_id"),
                        TradeStatus.valueOf(resultSet.getString("status")),
                        resultSet.getTimestamp("time_start"),
                        resultSet.getTimestamp("time_completed"),
                        CardType.valueOf(resultSet.getString("requested_type")),
                        resultSet.getInt("requested_damage")
                );
                rows.add(trade);
            }

            return rows;
        } catch (SQLException e) {
            throw new DataAccessException("Select not successful", e);
        }
    }

    /**
     * Retrieves all trades with a 'PENDING' status associated with a specific sender card ID.
     * <p>
     * The method executes a query to the database to find trades that are in a pending state
     * and match the given sender card ID. These trades are then converted into Trade objects
     * and returned as a collection.
     *
     * @param cardId the ID of the sender card whose pending trades are to be retrieved
     * @return a collection of Trade objects that are in a 'PENDING' status and associated with the given sender card ID
     * @throws DataAccessException if there is an issue accessing or querying the database
     */
    public Collection<Trade> getPendingTradeByCard(String cardId) {
        try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                """
                            SELECT
                               pk_trade_id,
                               fk_pk_initiator_id,
                               fk_pk_tradepartner_id,
                               fk_pk_sendercard_id,
                               fk_pk_receivercard_id,
                               status,
                               time_start,
                               time_completed,
                               requested_type,
                               requested_damage
                           FROM "trade"
                           WHERE status = 'PENDING' AND fk_pk_sendercard_id = ?
                        """)) {

            preparedStatement.setString(1, cardId);

            ResultSet resultSet = preparedStatement.executeQuery();

            Collection<Trade> pendingTrades = new ArrayList<>();

            while (resultSet.next()) {
                Trade trade = new Trade(
                        resultSet.getString("pk_trade_id"),
                        resultSet.getInt("fk_pk_initiator_id"),
                        resultSet.getInt("fk_pk_tradepartner_id"),
                        resultSet.getString("fk_pk_sendercard_id"),
                        resultSet.getString("fk_pk_receivercard_id"),
                        TradeStatus.valueOf(resultSet.getString("status")),
                        resultSet.getTimestamp("time_start"),
                        resultSet.getTimestamp("time_completed"),
                        CardType.valueOf(resultSet.getString("requested_type")),
                        resultSet.getInt("requested_damage")
                );
                pendingTrades.add(trade);
            }
            return pendingTrades;
        } catch (SQLException e) {
            throw new DataAccessException("Select not successful", e);
        }
    }

    /**
     * Retrieves a collection of Trade objects from the database by the specified trade ID.
     * <p>
     * The method executes a SQL query to fetch all trade details where the primary key
     * matches the provided trade ID. The resulting rows are mapped to Trade objects
     * and returned as a collection.
     *
     * @param id the unique identifier of the trade to be retrieved
     * @return a collection of Trade objects that match the specified trade ID;
     * the collection will be empty if no trades are found
     * @throws DataAccessException if there is an issue accessing or querying the database
     */
    public Collection<Trade> getTradeById(String id) {
        try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                """
                        SELECT
                           pk_trade_id,
                           fk_pk_initiator_id,
                           fk_pk_tradepartner_id,
                           fk_pk_sendercard_id,
                           fk_pk_receivercard_id,
                           status,
                           time_start,
                           time_completed,
                           requested_type,
                           requested_damage
                        FROM "trade"
                        WHERE pk_trade_id = ?
                        """
        )) {
            preparedStatement.setString(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            Collection<Trade> rows = new ArrayList<>();

            while (resultSet.next()) {
                Trade trade = new Trade(
                        resultSet.getString("pk_trade_id"),
                        resultSet.getInt("fk_pk_initiator_id"),
                        resultSet.getInt("fk_pk_tradepartner_id"),
                        resultSet.getString("fk_pk_sendercard_id"),
                        resultSet.getString("fk_pk_receivercard_id"),
                        TradeStatus.valueOf(resultSet.getString("status")),
                        resultSet.getTimestamp("time_start"),
                        resultSet.getTimestamp("time_completed"),
                        CardType.valueOf(resultSet.getString("requested_type")),
                        resultSet.getInt("requested_damage")
                );
                rows.add(trade);
            }

            return rows;
        } catch (SQLException e) {
            throw new DataAccessException("Select not successful for trade ID: " + id, e);
        }
    }

    /**
     * Creates a new trade record in the database using the provided trade information.
     * <p>
     * The method executes an SQL insert statement to save the trade details into the "trade" table.
     * Upon successful execution, it retrieves the generated trade details from the database
     * and constructs a new {@link Trade} object from the retrieved data.
     * If the operation fails at any point, a {@link DataAccessException} is thrown.
     *
     * @param trade the {@link Trade} object containing the trade information to be persisted
     * @return a {@link Trade} object populated with the details of the newly created trade
     * @throws DataAccessException if there is an issue accessing or interacting with the database
     */
    public Trade createTrade(Trade trade) {
        try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                """
                        INSERT INTO "trade" (
                            pk_trade_id,
                            fk_pk_initiator_id,
                            fk_pk_sendercard_id,
                            status,
                            requested_type,
                            requested_damage
                        ) VALUES (?, ?, ?, ?::tradestatus, ?::cardtypes, ?)
                        RETURNING 
                            pk_trade_id,
                            fk_pk_initiator_id,
                            fk_pk_tradepartner_id,
                            fk_pk_sendercard_id,
                            fk_pk_receivercard_id,
                            status,
                            time_start,
                            time_completed,
                            requested_type,
                            requested_damage
                        """
        )) {
            preparedStatement.setString(1, trade.getTradeId());
            preparedStatement.setInt(2, trade.getInitiatorId());
            preparedStatement.setString(3, trade.getSenderCardId());
            preparedStatement.setString(4, trade.getStatus().name());
            preparedStatement.setString(5, trade.getRequestedType().name());
            preparedStatement.setInt(6, trade.getRequestedDamage());

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return new Trade(
                        resultSet.getString("pk_trade_id"),
                        resultSet.getInt("fk_pk_initiator_id"),
                        resultSet.getInt("fk_pk_tradepartner_id"),
                        resultSet.getString("fk_pk_sendercard_id"),
                        resultSet.getString("fk_pk_receivercard_id"),
                        TradeStatus.valueOf(resultSet.getString("status")),
                        resultSet.getTimestamp("time_start"),
                        resultSet.getTimestamp("time_completed"),
                        CardType.valueOf(resultSet.getString("requested_type")),
                        resultSet.getInt("requested_damage")
                );
            } else {
                throw new DataAccessException("Failed to create trade");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to insert trade into the database", e);
        }
    }

    /**
     * Deletes a trade record from the database based on the provided trade ID.
     * <p>
     * The method uses a SQL DELETE statement to remove the trade corresponding
     * to the specified ID from the "trade" table. If no trade with the specified
     * ID exists, a {@link DataAccessException} is thrown. In case of any SQL
     * error, the method handles the exception and throws a custom
     * {@link DataAccessException}.
     *
     * @param tradeId the unique identifier of the trade to be deleted
     * @throws DataAccessException if no trade is found with the specified ID,
     *                             or if there is an error executing the SQL statement
     */
    public void deleteTradeById(String tradeId) {
        try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                """
                        DELETE FROM "trade"
                        WHERE pk_trade_id = ?
                        """
        )) {
            preparedStatement.setString(1, tradeId);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected == 0) {
                throw new DataAccessException("No trade found with ID: " + tradeId);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to delete trade with ID: " + tradeId, e);
        }
    }

    /**
     * Updates an existing trade record in the database with the new information provided in the {@link Trade} object.
     * <p>
     * The method executes an SQL update statement to modify the fields of the specified trade.
     * The updated trade data is then retrieved and returned as a {@link Trade} object.
     * If the trade to be updated does not exist, or if an SQL error occurs, a {@link DataAccessException} is thrown.
     *
     * @param trade the {@link Trade} object containing updated trade information
     * @return a {@link Trade} object with the updated information from the database
     * @throws DataAccessException if the specified trade is not found or a database error occurs
     */
    public Trade updateTrade(Trade trade) {
        try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                """
                        UPDATE "trade"
                        SET 
                            fk_pk_tradepartner_id = ?,
                            fk_pk_receivercard_id = ?,
                            status = ?::tradestatus,
                            time_completed = ? 
                        WHERE pk_trade_id = ?
                        RETURNING 
                            pk_trade_id,
                            fk_pk_initiator_id,
                            fk_pk_tradepartner_id,
                            fk_pk_sendercard_id,
                            fk_pk_receivercard_id,
                            status,
                            time_start,
                            time_completed,
                            requested_type,
                            requested_damage
                        """
        )) {
            preparedStatement.setInt(1, trade.getPartnerId());
            preparedStatement.setString(2, trade.getReceiverCardId());
            preparedStatement.setString(3, trade.getStatus().name());
            preparedStatement.setTimestamp(4, trade.getTimeCompleted());
            preparedStatement.setString(5, trade.getTradeId());

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return new Trade(
                        resultSet.getString("pk_trade_id"),
                        resultSet.getInt("fk_pk_initiator_id"),
                        resultSet.getInt("fk_pk_tradepartner_id"),
                        resultSet.getString("fk_pk_sendercard_id"),
                        resultSet.getString("fk_pk_receivercard_id"),
                        TradeStatus.valueOf(resultSet.getString("status")),
                        resultSet.getTimestamp("time_start"),
                        resultSet.getTimestamp("time_completed"),
                        CardType.valueOf(resultSet.getString("requested_type")),
                        resultSet.getInt("requested_damage")
                );
            } else {
                throw new DataAccessException("Trade with ID " + trade.getTradeId() + " not found for update.");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update trade with ID: " + trade.getTradeId(), e);
        }
    }
}
