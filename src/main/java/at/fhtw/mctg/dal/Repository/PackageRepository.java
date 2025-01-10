package at.fhtw.mctg.dal.Repository;

import at.fhtw.mctg.dal.DataAccessException;
import at.fhtw.mctg.dal.UnitOfWork;
import at.fhtw.mctg.model.CardPack;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The PackageRepository class is responsible for managing database interactions
 * related to packages in the system. It allows creating new packages and fetching
 * available free packages.
 */
public class PackageRepository {
    private final UnitOfWork unitOfWork;

    /**
     * Initializes a new instance of the PackageRepository class with a UnitOfWork instance.
     * This allows the repository to manage and execute database interactions within a transaction scope.
     *
     * @param unitOfWork the UnitOfWork instance that manages the database connection and transaction scope
     */
    public PackageRepository(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    /**
     * Creates a new package record in the database and retrieves the generated package ID.
     * The method executes an SQL insert query using a prepared statement. If the operation
     * is successful, the newly created package's primary key is returned.
     *
     * @return the primary key (ID) of the newly created package
     * @throws DataAccessException if the SQL execution fails or any database interaction error occurs
     */
    public int createPackage() {
        try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                """
                        INSERT INTO package DEFAUlT VALUES RETURNING pk_package_id
                        """)) {

            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getInt("pk_package_id");
        } catch (SQLException e) {
            throw new DataAccessException("Insert not successful", e);
        }
    }

    /**
     * Fetches a free card package from the database. A free package is identified as
     * one where all its associated cards have not been assigned to any user. The method
     * executes a SQL query to locate such a package and retrieves its details.
     *
     * @return a CardPack object representing the free card package, or null if no such package is available
     * @throws DataAccessException if a database error occurs during query execution
     */
    public CardPack getFreePack() {
        try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                """
                          SELECT p.pk_package_id, p.name, p.price ,COUNT(c.pk_card_id)
                          FROM "package" p
                          JOIN "card" c ON p.pk_package_id = c.fk_pk_package_id
                          GROUP BY p.pk_package_id
                          HAVING COUNT(c.pk_card_id) = SUM(CASE WHEN c.fk_pk_user_id IS NULL THEN 1 ELSE 0 END);
                        """)) {

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return new CardPack(
                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getInt(3)
                );
            }

            return null;
        } catch (SQLException e) {
            throw new DataAccessException("Select not successful", e);
        }
    }
}
