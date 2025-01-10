package at.fhtw.mctg.dal.Repository;

import at.fhtw.mctg.dal.DataAccessException;
import at.fhtw.mctg.dal.UnitOfWork;
import at.fhtw.mctg.model.Stats;
import at.fhtw.mctg.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Repository class for managing User entities.
 * Provides methods to interact with the user-related data in the database.
 */
public class UserRepository {
    private final UnitOfWork unitOfWork;

    /**
     * Constructs a new UserRepository with the given UnitOfWork instance.
     *
     * @param unitOfWork the UnitOfWork instance used to manage database transactions
     */
    public UserRepository(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    /**
     * Creates a new user record in the database.
     *
     * @param user the User object containing the username and password to be added to the database
     * @throws DataAccessException if a database access error occurs or the insert operation fails
     */
    public void createUser(User user) {
        try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                """
                        INSERT INTO "user" (username, password) VALUES (?, ?)
                        """)) {
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());

            preparedStatement.execute();

        } catch (SQLException e) {
            throw new DataAccessException("Insert not successful", e);
        }
    }

    /**
     * Retrieves a collection of users from the database based on their username.
     *
     * @param name the username to search for
     * @return a collection containing {@code User} objects matching the given username
     * @throws DataAccessException if a database access error occurs or the query fails
     */
    public Collection<User> getUserByName(String name) {
        try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                """
                            select * from "user" where username = ?
                        """)) {

            preparedStatement.setString(1, name);

            ResultSet resultSet = preparedStatement.executeQuery();

            Collection<User> userRows = new ArrayList<>();

            while (resultSet.next()) {
                User user = new User(
                        resultSet.getInt(1), // ID
                        resultSet.getString(2), // username
                        resultSet.getString(3), // password
                        resultSet.getString(4), // name
                        resultSet.getString(5), // bio
                        resultSet.getString(6), // image
                        resultSet.getInt(7), // wallet
                        resultSet.getInt(8) // elo
                );

                userRows.add(user);
            }

            return userRows;
        } catch (SQLException e) {
            throw new DataAccessException("Select not successful", e);
        }
    }

    /**
     * Retrieves a user from the database by their unique identifier.
     *
     * @param userId the unique ID of the user to retrieve
     * @return a {@code User} object if a user with the specified ID exists, or {@code null} if no such user is found
     * @throws DataAccessException if a database access error occurs during the operation
     */
    public User getUserById(int userId) {
        try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                """
                            select * from "user" where pk_user_id = ?
                        """)) {

            preparedStatement.setInt(1, userId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return new User(
                        resultSet.getInt(1), // ID
                        resultSet.getString(2), // username
                        resultSet.getString(3), // password
                        resultSet.getString(4), // name
                        resultSet.getString(5), // bio
                        resultSet.getString(6), // image
                        resultSet.getInt(7), // wallet
                        resultSet.getInt(8) // elo
                );
            }

            return null;
        } catch (SQLException e) {
            throw new DataAccessException("Select not successful", e);
        }
    }

    /**
     * Updates a user's details in the database based on the provided username.
     *
     * @param username the username of the user whose details are to be updated
     * @param user     the User object containing updated details such as bio, name, image, password, and elo
     * @throws DataAccessException if a database access error occurs or the update operation fails
     */
    public void updateUserByName(String username, User user) {
        try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                """
                        UPDATE "user" SET bio = ?, name = ?, image = ?, password = ?, elo = ? WHERE username = ?
                        """)) {
            preparedStatement.setString(1, user.getBio());
            preparedStatement.setString(2, user.getName());
            preparedStatement.setString(3, user.getImage());
            preparedStatement.setString(4, user.getPassword());
            preparedStatement.setInt(5, user.getElo());
            preparedStatement.setString(6, username);

            preparedStatement.execute();

        } catch (SQLException e) {
            throw new DataAccessException("Update not successful", e);
        }
    }

    /**
     * Updates the wallet balance of a user based on the username and the specified change.
     * This method retrieves the current wallet balance of the user,
     * adjusts it by the specified change amount, and updates it in the database.
     *
     * @param username the username of the user whose wallet will be updated
     * @param change   the amount by which the user's wallet balance will be adjusted; can be positive or negative
     * @throws DataAccessException if a database access error occurs or the update operation fails
     */
    public void updateWalletByName(String username, int change) {
        int current = ((ArrayList<User>) getUserByName(username)).get(0).getWallet();
        try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                """
                        UPDATE "user" SET wallet = ? WHERE username = ?
                        """)) {
            preparedStatement.setInt(1, (current + change));
            preparedStatement.setString(2, username);

            preparedStatement.execute();

        } catch (SQLException e) {
            throw new DataAccessException("Update not successful", e);
        }
    }

    /**
     * Retrieves the statistics for a user identified by their username.
     *
     * @param username the username of the user whose stats are to be retrieved
     * @return a {@code Stats} object containing the user's statistics, including username, name, elo, wins, losses, and ties
     * @throws DataAccessException if a database access error occurs or the query fails
     */
    public Stats getUserStatsByName(String username) {
        try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                """
                            SELECT
                                u.pk_user_id,
                                u.username,
                                u.name,
                                u.elo,
                                SUM(CASE WHEN ub.status = 'WIN' THEN 1 ELSE 0 END) AS total_wins,
                                SUM(CASE WHEN ub.status = 'LOSS' THEN 1 ELSE 0 END) AS total_losses,
                                SUM(CASE WHEN ub.status = 'TIE' THEN 1 ELSE 0 END) AS total_ties
                            FROM "user" u
                            LEFT JOIN "user_battle" ub ON u.pk_user_id = ub.fk_pk_user_id
                            WHERE u.username = ?
                            GROUP BY u.pk_user_id, u.username, u.elo;
                        """)) {

            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();

            resultSet.next();

            return new Stats(
                    resultSet.getString(2), // username
                    resultSet.getString(3), // name
                    resultSet.getInt(4), // elo
                    resultSet.getInt(5), // wins
                    resultSet.getInt(6), // losses
                    resultSet.getInt(7) // ties
            );
        } catch (SQLException e) {
            throw new DataAccessException("Select not successful", e);
        }
    }

    /**
     * Retrieves a collection of user statistics ordered by Elo score in descending order.
     * The statistics include username, name, Elo score, total wins, total losses, and total ties.
     * The method queries the database, processes the results, and generates a collection of {@code Stats} objects.
     *
     * @return a {@code Collection} of {@code Stats} objects representing user statistics sorted by Elo score in descending order
     * @throws DataAccessException if a database access error occurs or the query fails
     */
    public Collection<Stats> getOrderedStats() {
        try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                """
                            SELECT
                                u.username,
                                u.name,
                                u.pk_user_id,
                                u.elo,
                                SUM(CASE WHEN ub.status = 'WIN' THEN 1 ELSE 0 END) AS total_wins,
                                SUM(CASE WHEN ub.status = 'LOSS' THEN 1 ELSE 0 END) AS total_losses,
                                SUM(CASE WHEN ub.status = 'TIE' THEN 1 ELSE 0 END) AS total_ties
                            FROM "user" u
                            LEFT JOIN "user_battle" ub ON u.pk_user_id = ub.fk_pk_user_id
                            GROUP BY u.pk_user_id, u.username, u.elo
                            ORDER BY elo desc;
                        """)) {

            ResultSet resultSet = preparedStatement.executeQuery();

            Collection<Stats> scoreboard = new ArrayList<>();

            while (resultSet.next()) {
                Stats stats = new Stats(
                        resultSet.getString(1), // username
                        resultSet.getString(2), // name
                        resultSet.getInt(4), // elo
                        resultSet.getInt(5), // wins
                        resultSet.getInt(6), // losses
                        resultSet.getInt(7) // ties
                );

                scoreboard.add(stats);
            }

            return scoreboard;
        } catch (SQLException e) {
            throw new DataAccessException("Select not successful", e);
        }
    }
}
