package at.fhtw.mctg.dal.Repository;

import at.fhtw.mctg.dal.DataAccessException;
import at.fhtw.mctg.dal.UnitOfWork;
import at.fhtw.mctg.model.Token;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;


/**
 * The SessionRepository class provides methods for managing user session tokens
 * in the database. It allows retrieving tokens associated with a specific username
 * and creating new tokens for a specified user.
 */
public class SessionRepository {
    private final UnitOfWork unitOfWork;

    /**
     * Constructs a new SessionRepository instance.
     *
     * @param unitOfWork the UnitOfWork instance to manage database operations
     */
    public SessionRepository(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    /**
     * Retrieves a collection of tokens associated with the given username.
     *
     * @param username the username for which the tokens are to be retrieved
     * @return a collection of tokens associated with the specified username
     * @throws DataAccessException if there is an issue accessing the database
     */
    public Collection<Token> getTokenByUsername(String username) {
        try (PreparedStatement preparedStatement =
                     this.unitOfWork.prepareStatement("""
                                 select token.* from "token" 
                                 join "user" 
                                 on "token".fk_pk_user_id = "user".pk_user_id 
                                 WHERE "user".username = ?;
                             """)) {
            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();

            Collection<Token> tokenRows = new ArrayList<>();

            while (resultSet.next()) {
                Token token = new Token(
                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(4),
                        resultSet.getInt(5));
                tokenRows.add(token);
            }

            return tokenRows;
        } catch (SQLException e) {
            throw new DataAccessException("Select nicht erfolgreich", e);
        }
    }

    /**
     * Creates a new token for the specified user and stores it in the database.
     *
     * @param userId the ID of the user for whom the token is being created
     * @param token  the token value to be saved in the database
     * @throws DataAccessException if there is an error while interacting with the database
     */
    public void createToken(int userId, String token) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                """
                        INSERT INTO "token" (token, created, expires, fk_pk_user_id) VALUES (?, ?, ?, ?)
                        """)) {
            preparedStatement.setString(1, token);
            preparedStatement.setObject(2, currentDateTime);
            preparedStatement.setObject(3, currentDateTime.plusDays(30L));
            preparedStatement.setInt(4, userId);

            preparedStatement.execute();

        } catch (SQLException e) {
            throw new DataAccessException("Insert not successful", e);
        }
    }
}
