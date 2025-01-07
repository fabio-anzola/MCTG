package at.fhtw.mctg.dal.Repository;

import at.fhtw.mctg.dal.DataAccessException;
import at.fhtw.mctg.dal.UnitOfWork;
import at.fhtw.mctg.model.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class TradeRepository {
    private UnitOfWork unitOfWork;

    public TradeRepository(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

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
