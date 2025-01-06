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
                        resultSet.getInt("pk_trade_id"),
                        resultSet.getInt("fk_pk_initiator_id"),
                        resultSet.getInt("fk_pk_tradepartner_id"),
                        resultSet.getString("fk_pk_sendercard_id"),
                        resultSet.getString("fk_pk_receivercard_id"),
                        TradeStatus.valueOf(resultSet.getString("status")), // Enum mapping
                        resultSet.getTimestamp("time_start"),
                        resultSet.getTimestamp("time_completed"),
                        CardType.valueOf(resultSet.getString("requested_type")), // Enum mapping
                        resultSet.getInt("requested_damage")
                );
                rows.add(trade);
            }

            return rows;
        } catch (SQLException e) {
            throw new DataAccessException("Select not successful", e);
        }
    }
}
