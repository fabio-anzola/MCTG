package at.fhtw.mctg.dal.Repository;

import at.fhtw.mctg.dal.DataAccessException;
import at.fhtw.mctg.dal.UnitOfWork;
import at.fhtw.mctg.model.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

public class BattleRepository {
    private UnitOfWork unitOfWork;

    public BattleRepository(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }


    public Collection<Battle> getPendingBattles() {
        try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                """
                            SELECT b.*
                                 FROM "battle" b
                                 JOIN "user_battle" ub ON b.pk_battle_id = ub.fk_pk_battle_id
                                 WHERE ub.status IS NULL
                                 GROUP BY b.pk_battle_id, b.time_start, b.time_end, b.rounds_nr, b.fk_pk_battlelog_id
                                 HAVING COUNT(ub.fk_pk_user_id) = 1;
                        """)) {

            ResultSet resultSet = preparedStatement.executeQuery();

            Collection<Battle> battleRows = new ArrayList<>();

            while (resultSet.next()) {
                Battle battle = new Battle(
                        resultSet.getInt(1), //ID
                        resultSet.getTimestamp(2), // Start Time
                        resultSet.getTimestamp(3), // End Time
                        resultSet.getInt(4), //Round Nr
                        resultSet.getInt(5) //Battle Log id
                );

                battleRows.add(battle);
            }

            return battleRows;
        } catch (SQLException e) {
            throw new DataAccessException("Select not successful", e);
        }
    }

    public Battle createNewBattle(String username) {
        String init_msg = "Battle Initialized by " + username;

        try {
            // Insert into battlelog table
            PreparedStatement battleLogStmt = this.unitOfWork.prepareStatement(
                    """
                            INSERT INTO "battlelog" (log_text, row_nr) 
                            VALUES (?,?) 
                            RETURNING pk_battlelog_id
                            """
            );
            battleLogStmt.setString(1, init_msg);
            battleLogStmt.setInt(2, 1);
            ResultSet battleLogResult = battleLogStmt.executeQuery();

            int battleLogId;
            if (battleLogResult.next()) {
                battleLogId = battleLogResult.getInt(1); // Retrieve generated battle log ID
            } else {
                throw new DataAccessException("Failed to insert battle log");
            }

            // Insert into battle table
            PreparedStatement battleStmt = this.unitOfWork.prepareStatement(
                    """
                            INSERT INTO "battle" (rounds_nr, fk_pk_battlelog_id) 
                            VALUES (?, ?) 
                            RETURNING pk_battle_id, time_start, time_end, rounds_nr, fk_pk_battlelog_id
                            """
            );
            battleStmt.setInt(1, 0); // Initialize rounds to 0
            battleStmt.setInt(2, battleLogId);
            ResultSet battleResult = battleStmt.executeQuery();

            Battle battle;
            if (battleResult.next()) {
                // Create Battle object from result
                battle = new Battle(
                        battleResult.getInt("pk_battle_id"),
                        battleResult.getTimestamp("time_start"),
                        battleResult.getTimestamp("time_end"),
                        battleResult.getInt("rounds_nr"),
                        battleResult.getInt("fk_pk_battlelog_id")
                );
            } else {
                throw new DataAccessException("Failed to insert battle");
            }

            // Insert into user_battle table
            PreparedStatement userBattleStmt = this.unitOfWork.prepareStatement(
                    """
                            INSERT INTO "user_battle" (status, fk_pk_user_id, fk_pk_battle_id) 
                            VALUES (NULL, (SELECT pk_user_id FROM "user" WHERE username = ?), ?)
                            """
            );
            userBattleStmt.setString(1, username); // Set username for user lookup
            userBattleStmt.setInt(2, battle.getBattleId()); // Set the generated battle ID
            userBattleStmt.execute();

            // Return the Battle object
            return battle;

        } catch (SQLException e) {
            throw new DataAccessException("Transaction failed", e);
        }
    }

    public Battle setBattleStart(int battleId, Timestamp startTime) {
        try {
            // Update the battle start time
            PreparedStatement updateStmt = this.unitOfWork.prepareStatement(
                    """
                    UPDATE "battle"
                    SET time_start = ?
                    WHERE pk_battle_id = ?
                    RETURNING pk_battle_id, time_start, time_end, rounds_nr, fk_pk_battlelog_id
                    """
            );
            updateStmt.setTimestamp(1, startTime);
            updateStmt.setInt(2, battleId);
            ResultSet resultSet = updateStmt.executeQuery();

            Battle battle;
            if (resultSet.next()) {
                battle = new Battle(
                        resultSet.getInt("pk_battle_id"),
                        resultSet.getTimestamp("time_start"),
                        resultSet.getTimestamp("time_end"),
                        resultSet.getInt("rounds_nr"),
                        resultSet.getInt("fk_pk_battlelog_id")
                );
            } else {
                throw new DataAccessException("Battle not found for battleId: " + battleId);
            }

            // Return the updated Battle object
            return battle;

        } catch (SQLException e) {
            throw new DataAccessException("Failed to set battle start time for battleId: " + battleId, e);
        }
    }

    public Battle finalizeBattle(int battleId, Timestamp endTime, int roundsNr) {
        try {
            // Update the battle end time and rounds number
            PreparedStatement updateStmt = this.unitOfWork.prepareStatement(
                    """
                    UPDATE "battle"
                    SET time_end = ?, rounds_nr = ?
                    WHERE pk_battle_id = ?
                    RETURNING pk_battle_id, time_start, time_end, rounds_nr, fk_pk_battlelog_id
                    """
            );
            updateStmt.setTimestamp(1, endTime);
            updateStmt.setInt(2, roundsNr);
            updateStmt.setInt(3, battleId);

            ResultSet resultSet = updateStmt.executeQuery();

            Battle battle;
            if (resultSet.next()) {
                // Create a Battle object with the updated data
                battle = new Battle(
                        resultSet.getInt("pk_battle_id"),
                        resultSet.getTimestamp("time_start"),
                        resultSet.getTimestamp("time_end"),
                        resultSet.getInt("rounds_nr"),
                        resultSet.getInt("fk_pk_battlelog_id")
                );
            } else {
                throw new DataAccessException("Battle not found for battleId: " + battleId);
            }

            // Return the updated Battle object
            return battle;

        } catch (SQLException e) {
            throw new DataAccessException("Failed to finalize battle for battleId: " + battleId, e);
        }
    }

    public void addLogLine(int battleId, String line) {
        try {
            // Step 1: Find the associated battlelog ID
            int battlelogId;
            try (PreparedStatement findBattlelogStmt = this.unitOfWork.prepareStatement(
                    """
                            SELECT fk_pk_battlelog_id 
                            FROM "battle" 
                            WHERE pk_battle_id = ?
                            """
            )) {
                findBattlelogStmt.setInt(1, battleId);
                ResultSet resultSet = findBattlelogStmt.executeQuery();
                if (resultSet.next()) {
                    battlelogId = resultSet.getInt("fk_pk_battlelog_id");
                } else {
                    throw new DataAccessException("BattleLog not found for battleId: " + battleId);
                }

            }

            // Step 2: Determine the next available row number
            int nextRowNr;
            try (PreparedStatement findNextRowStmt = this.unitOfWork.prepareStatement(
                    """
                            SELECT COALESCE(MAX(row_nr), 0) + 1 AS next_row_nr 
                            FROM "battlelog" 
                            WHERE pk_battlelog_id = ?
                            """
            )) {
                findNextRowStmt.setInt(1, battlelogId);
                ResultSet resultSet = findNextRowStmt.executeQuery();
                if (resultSet.next()) {
                    nextRowNr = resultSet.getInt("next_row_nr");
                } else {
                    throw new DataAccessException("Failed to determine the next row number for battlelogId: " + battlelogId);
                }

            }

            // Step 3: Insert the new log entry
            try (PreparedStatement insertLogStmt = this.unitOfWork.prepareStatement(
                    """
                            INSERT INTO "battlelog" (pk_battlelog_id, row_nr, log_text) 
                            VALUES (?, ?, ?)
                            """
            )) {
                insertLogStmt.setInt(1, battlelogId);
                insertLogStmt.setInt(2, nextRowNr);
                insertLogStmt.setString(3, line);
                insertLogStmt.execute();
            }

        } catch (SQLException e) {
            throw new DataAccessException("Failed to add battle log entry for battleId: " + battleId, e);
        }
    }

    public void joinBattle(int battleId, String requestingUser) {
        try {
            //addLogLine(battleId, requestingUser + " joined the battle");

            // Insert into user_battle table
            PreparedStatement userBattleStmt = this.unitOfWork.prepareStatement(
                    """
                            INSERT INTO "user_battle" (status, fk_pk_user_id, fk_pk_battle_id) 
                            VALUES (NULL, (SELECT pk_user_id FROM "user" WHERE username = ?), ?)
                            """
            );

            userBattleStmt.setString(1, requestingUser);
            userBattleStmt.setInt(2, battleId);

            userBattleStmt.execute();
        } catch (SQLException e) {
            throw new DataAccessException("Transaction failed", e);
        }
    }

    public void finalizeUserBattle(int battleId, int userId, BattleStatus status) {
        try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                """
                UPDATE "user_battle"
                SET status = ?::battlestatus
                WHERE fk_pk_battle_id = ? AND fk_pk_user_id = ?
                """
        )) {
            // Set the new status
            preparedStatement.setString(1, status.name());
            preparedStatement.setInt(2, battleId);
            preparedStatement.setInt(3, userId);

            // Execute the update
            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated == 0) {
                throw new DataAccessException("No user_battle entry found for battleId: " + battleId + " and userId: " + userId);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Transaction failed", e);
        }
    }

    public boolean checkBattleComplete(int battleId) {
        try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                """
                            SELECT fk_pk_user_id, fk_pk_battle_id, status
                                FROM "user_battle"
                                WHERE fk_pk_battle_id = ? AND status IS NULL
                        """)) {

            preparedStatement.setInt(1, battleId);

            ResultSet resultSet = preparedStatement.executeQuery();

            Collection<UserBattle> ubRows = new ArrayList<>();

            while (resultSet.next()) {
                BattleStatus status = resultSet.getString(3) != null
                        ? BattleStatus.valueOf(resultSet.getString(3))
                        : null;

                UserBattle ub = new UserBattle(
                        resultSet.getInt(1), // Uid
                        resultSet.getInt(2), // battle id
                        status // Status
                );

                ubRows.add(ub);
            }

            return ubRows.size() <= 0;
        } catch (SQLException e) {
            throw new DataAccessException("Select not successful", e);
        }
    }

    public Collection<BattleLog> getBattleLog(int battleId) {
        try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                """
                        SELECT 
                            bl.pk_battlelog_id,
                            bl.row_nr,
                            bl.log_text
                        FROM "battlelog" bl
                        JOIN "battle" b ON bl.pk_battlelog_id = b.fk_pk_battlelog_id
                        WHERE b.pk_battle_id = ?
                        ORDER BY bl.row_nr
                        """
        )) {
            preparedStatement.setInt(1, battleId); // Set battleId

            Collection<BattleLog> battleLogs = new ArrayList<>();

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    battleLogs.add(
                            new BattleLog(
                                    resultSet.getInt(1),
                                    resultSet.getInt(2),
                                    resultSet.getString(3)
                            )
                    );
                }

                return battleLogs;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to retrieve battle logs for battleId: " + battleId, e);
        }
    }

    public Collection<UserBattle> getBattleUsers(int battleId) {
        try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                """
                            SELECT fk_pk_user_id, fk_pk_battle_id, status
                                FROM "user_battle"
                                WHERE fk_pk_battle_id = ? AND status IS NULL
                        """)) {

            preparedStatement.setInt(1, battleId);

            ResultSet resultSet = preparedStatement.executeQuery();

            Collection<UserBattle> ubRows = new ArrayList<>();

            while (resultSet.next()) {
                BattleStatus status = resultSet.getString(3) != null
                        ? BattleStatus.valueOf(resultSet.getString(3))
                        : null;

                UserBattle ub = new UserBattle(
                        resultSet.getInt(1), // Uid
                        resultSet.getInt(2), // battle id
                        status // Status
                );

                ubRows.add(ub);
            }

            return ubRows;
        } catch (SQLException e) {
            throw new DataAccessException("Select not successful", e);
        }
    }
}
