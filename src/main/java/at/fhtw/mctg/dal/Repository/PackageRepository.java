package at.fhtw.mctg.dal.Repository;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Response;
import at.fhtw.mctg.dal.DataAccessException;
import at.fhtw.mctg.dal.UnitOfWork;
import at.fhtw.mctg.model.CardPack;
import at.fhtw.mctg.model.User;
import org.postgresql.shaded.com.ongres.scram.common.bouncycastle.pbkdf2.Pack;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class PackageRepository {
    private UnitOfWork unitOfWork;

    public PackageRepository(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

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
