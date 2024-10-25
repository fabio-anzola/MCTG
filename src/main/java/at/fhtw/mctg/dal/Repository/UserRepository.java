package at.fhtw.mctg.dal.Repository;

import at.fhtw.mctg.dal.DataAccessException;
import at.fhtw.mctg.dal.UnitOfWork;
import at.fhtw.mctg.model.User;
import at.fhtw.mctg.model.Weather;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

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
                        resultSet.getString(4), // bio
                        resultSet.getString(5), // image
                        resultSet.getInt(6), // wallet
                        resultSet.getInt(7) // elo
                );

                userRows.add(user);
            }

            return userRows;
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
