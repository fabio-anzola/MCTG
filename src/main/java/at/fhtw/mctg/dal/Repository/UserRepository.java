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

import static java.lang.Thread.sleep;

public class UserRepository {
    private UnitOfWork unitOfWork;

    public UserRepository(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

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

    public void updateUserByName(String username, User user) {
        try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                """
                        UPDATE "user" SET bio = ?, name = ?, image = ?, password = ? WHERE username = ?
                        """)) {
            preparedStatement.setString(1, user.getBio());
            preparedStatement.setString(2, user.getName());
            preparedStatement.setString(3, user.getImage());
            preparedStatement.setString(4, user.getPassword());
            preparedStatement.setString(5, username);

            preparedStatement.execute();

        } catch (SQLException e) {
            throw new DataAccessException("Update not successful", e);
        }
    }

    public void updateWalletByName(String username, int change) {
        int current = ((ArrayList<User>)getUserByName(username)).get(0).getWallet();
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

//    public Collection<Weather> findAllWeather() {
//        try (PreparedStatement preparedStatement =
//                     this.unitOfWork.prepareStatement("""
//                    select * from weather
//                    where region = ?
//                """))
//        {
//            preparedStatement.setString(1, "Europe");
//            ResultSet resultSet = preparedStatement.executeQuery();
//            Collection<User> weatherRows = new ArrayList<>();
//            while(resultSet.next())
//            {
//                Weather weather = new Weather(
//                        resultSet.getInt(1),
//                        resultSet.getString(2),
//                        resultSet.getString(3),
//                        resultSet.getInt(4));
//                weatherRows.add(weather);
//            }
//
//            return weatherRows;
//        } catch (SQLException e) {
//            throw new DataAccessException("Select nicht erfolgreich", e);
//        }
//    }
}
