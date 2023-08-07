package jdbc;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SimpleJDBCRepository {

    private Connection connection = null;
    private PreparedStatement ps = null;
    private Statement st = null;

    private static final String createUserSQL = "INSERT INTO Users (firstname, lastname, age) VALUES (?, ?, ?)";
    private static final String updateUserSQL = "UPDATE Users SET firstname = ?, lastname = ?, age = ? WHERE id = ?";
    private static final String deleteUser = "DELETE FROM Users WHERE id = ?";
    private static final String findUserByIdSQL = "SELECT id, firstname, lastname, age FROM Users WHERE id = ?";
    private static final String findUserByNameSQL = "SELECT id, firstname, lastname, age FROM Users WHERE firstname = ?";
    private static final String findAllUserSQL = "SELECT id, firstname, lastname, age FROM Users";

    public Long createUser(User user) {
        try(Connection connection1 = CustomDataSource.getInstance().getConnection();
            PreparedStatement prepareStatement = connection1.prepareStatement(createUserSQL, Statement.RETURN_GENERATED_KEYS)){
            prepareStatement.setString(1, user.getFirstName());
            prepareStatement.setString(2, user.getLastName());
            prepareStatement.setInt(3, user.getAge());
            int affectedRows = prepareStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }

            ResultSet resultSet = prepareStatement.getGeneratedKeys();
            if (resultSet.next()) {
                return resultSet.getLong(1);
            }else {
                throw new SQLException("Creating user failed, no ID obtained.");
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public User findUserById(Long userId) {
        try(Connection connection1 = CustomDataSource.getInstance().getConnection();
            PreparedStatement prepareStatement = connection1.prepareStatement(findUserByIdSQL)){
            prepareStatement.setLong(1, userId);
            ResultSet resultSet = prepareStatement.executeQuery();
            if (!resultSet.next()) {
                throw new SQLException("User with id=" + userId + "not found!");
            }
            Long id = resultSet.getLong("id");
            String firstName = resultSet.getString("firstname");
            String lastName = resultSet.getString("lastname");
            int age = resultSet.getInt("age");
            return new User(id, firstName, lastName, age);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public User findUserByName(String userName) {
        try(Connection connection1 = CustomDataSource.getInstance().getConnection();
            PreparedStatement prepareStatement = connection1.prepareStatement(findUserByNameSQL)){
            prepareStatement.setString(1, userName);
            ResultSet resultSet = prepareStatement.executeQuery();
            if (!resultSet.next()) {
                throw new SQLException("User with name=" + userName + "not found!");
            }
            Long id = resultSet.getLong("id");
            String firstName = resultSet.getString("firstname");
            String lastName = resultSet.getString("lastname");
            int age = resultSet.getInt("age");
            return new User(id, firstName, lastName, age);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public List<User> findAllUser() {
        List<User> users = new ArrayList<>();
        try(Connection connection1 = CustomDataSource.getInstance().getConnection();
            Statement statement = connection1.createStatement()){
            ResultSet resultSet = statement.executeQuery(findAllUserSQL);
            while(resultSet.next()) {
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("firstname");
                String lastName = resultSet.getString("lastname");
                int age = resultSet.getInt("age");
                users.add(new User(id, firstName, lastName, age));
            }
            return users;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public User updateUser(User user) {
        try(Connection connection1 = CustomDataSource.getInstance().getConnection();
            PreparedStatement prepareStatement = connection1.prepareStatement(updateUserSQL)){
            prepareStatement.setString(1, user.getFirstName());
            prepareStatement.setString(2, user.getLastName());
            prepareStatement.setInt(3, user.getAge());
            prepareStatement.setLong(4, user.getId());
            int affectedRows = prepareStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("User with id=" + user.getId() + " not found!");
            }
            return user;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void deleteUser(Long userId) {
        try(Connection connection1 = CustomDataSource.getInstance().getConnection();
            PreparedStatement prepareStatement = connection1.prepareStatement(deleteUser)){
            prepareStatement.setLong(1, userId);
            int affectedRows = prepareStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("User with id=" + userId + " not found!");
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}
