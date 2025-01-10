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

/**
 * Repository for handling `Battle` data operations in the application.
 * Provides methods to query, create, update, and manage battles and associated data
 * in the database.
 */
public class BattleRepository {
    private final UnitOfWork unitOfWork;

    /**
     * Constructs a new instance of the BattleRepository.
     * This repository is responsible for managing battles and interacting with the database
     * through the provided UnitOfWork instance.
     *
     * @param unitOfWork the UnitOfWork instance used for managing database transactions and operations.
     */
    public BattleRepository(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }


    /**
     * Retrieves a collection of pending battles from the database.
     * Pending battles are those where one user has joined and the status remains unassigned.
     *
     * This method queries the database using a SQL statement to select battles with a null status
     * and exactly one user associated with them. It creates a list of `Battle` objects based on the
     * query result and returns it.
     *
     * @return a collection of `Battle` objects representing pending battles. If no battles are found,
     * an empty collection is returned.
     * @throws DataAccessException if a database error occurs during query execution.
     */
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

    /**
     * Creates a new battle in the database for a given user.
     * This method initializes a battle by adding entries to the battlelog, battle,
     * and user_battle tables. It also returns a `Battle` object representing the
     * newly created battle.
     *
     * @param username the username of the user who is initiating the battle
     * @return a `Battle` object containing details of the newly created battle
     * @throws DataAccessException if an error occurs during database operations
     */
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

    /**
     * Updates the start time of a battle with the specified battle ID in the database.
     * The method modifies the "time_start" field of the battle entry and retrieves
     * the updated battle information as a {@code Battle} object.
     *
     * @param battleId the unique identifier of the battle to be updated
     * @param startTime the start time of the battle to be set
     * @return the updated {@code Battle} object containing the battle's details
     * @throws DataAccessException if the battle is not found or a database error occurs during the update
     */
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

    /**
     * Finalizes an ongoing battle by updating its end time and the number of rounds in the database.
     * This method modifies the battle entry corresponding to the given battle ID and retrieves
     * the updated battle information as a {@code Battle} object.
     *
     * @param battleId the unique identifier of the battle to be finalized
     * @param endTime the end time of the battle as a {@code Timestamp}
     * @param roundsNr the number of rounds played in the battle
     * @return the updated {@code Battle} object containing the finalized battle's details
     * @throws DataAccessException if the battle is not found or a database error occurs during the operation
     */
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

    /**
     * Adds a log entry to the battle log associated with a specific battle ID.
     * This method retrieves the battle log ID for the given battle ID, determines the next row
     * number in the battle log, and inserts the provided log line into the database.
     *
     * @param battleId the unique identifier of the battle for which the log entry is to be added
     * @param line the log entry text to be added to the battle log
     * @throws DataAccessException if an error occurs during the database operation,
     *         such as unable to find the battle log or insert the log entry
     */
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

    /**
     * Allows a user to join a battle by inserting their information into the user_battle table.
     * This method associates the user with the specified battle in the database.
     *
     * @param battleId the unique identifier of the battle to join
     * @param requestingUser the username of the user attempting to join the battle
     * @throws DataAccessException if a database error occurs during the operation
     */
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

    /**
     * Finalizes a user's participation in a battle by updating the status in the database.
     * This method updates the status of an entry in the "user_battle" table corresponding to
     * a specific battle and user.
     *
     * @param battleId the unique identifier of the battle to be finalized for the user
     * @param userId the unique identifier of the user whose participation is being finalized
     * @param status the final status of the user in the battle, represented as a {@code BattleStatus} enumeration
     * @throws DataAccessException if the database operation fails or no entry is found for the given battleId and userId
     */
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

    /**
     * Checks if a battle with the specified battle ID is complete.
     * A battle is considered complete if there are no users associated with it who have a null status.
     *
     * @param battleId the unique identifier of the battle to be checked
     * @return {@code true} if the battle is complete (no pending users with null status),
     *         {@code false} otherwise
     * @throws DataAccessException if a database error occurs during the operation
     */
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

    /**
     * Retrieves a collection of battle log entries for a specific battle.
     * This method queries the database to fetch all log entries related to the provided battle ID.
     * Each log entry is represented as a {@code BattleLog} object and contains information
     * like the log row number and the log text.
     *
     * @param battleId the unique identifier of the battle whose log entries are to be fetched
     * @return a collection of {@code BattleLog} objects containing the log entries for the battle
     * @throws DataAccessException if a database error occurs while retrieving the log entries
     */
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

    /**
     * Retrieves a collection of users associated with a specific battle who have a null status.
     * This method queries the database to fetch entries from the "user_battle" table
     * where the `fk_pk_battle_id` matches the provided battle ID and the status is null.
     *
     * @param battleId the unique identifier of the battle whose users are to be retrieved
     * @return a collection of {@code UserBattle} objects representing users associated with the battle.
     *         If no matching users are found, an empty collection is returned.
     * @throws DataAccessException if a database error occurs during query execution
     */
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
