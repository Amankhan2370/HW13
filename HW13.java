import java.sql.*;

public class HW13 {
    public static void main(String[] args) {
        
        String url = "jdbc:mysql://localhost:3306/test_database"; 
        String user = "root"; 
        String password = "password"; 

        Connection connection = null;
        PreparedStatement selectStmt = null;
        PreparedStatement updateStmt = null;
        Savepoint savepoint = null;

        try {
           
            connection = DriverManager.getConnection(url, user, password);
            connection.setAutoCommit(false); 

            System.out.println("Connected to the database!");

            String selectQuery = "SELECT * FROM students WHERE id = 1";
            selectStmt = connection.prepareStatement(selectQuery);
            ResultSet resultSet = selectStmt.executeQuery();

            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                String grade = resultSet.getString("grade");

                System.out.println("Original Record: ID = " + id + ", Name = " + name + ", Age = " + age + ", Grade = " + grade);

                String newName = "Jane Doe";
                int newAge = 21;
                String newGrade = "A";

                System.out.println("Updating record to: Name = " + newName + ", Age = " + newAge + ", Grade = " + newGrade);

                String updateQuery = "UPDATE students SET name = ?, age = ?, grade = ? WHERE id = ?";
                updateStmt = connection.prepareStatement(updateQuery);
                updateStmt.setString(1, newName);
                updateStmt.setInt(2, newAge);
                updateStmt.setString(3, newGrade);
                updateStmt.setInt(4, id);
                updateStmt.executeUpdate();

                System.out.println("Record updated successfully!");

         
                savepoint = connection.setSavepoint("BeforeRollback");

                connection.rollback(savepoint);
                System.out.println("Rolled back to the original state.");
            } else {
                System.out.println("No record found with ID = 1");
            }

            connection.commit(); 

        } catch (SQLException e) {
            try {
                if (connection != null) {
                    connection.rollback(); 
                }
            } catch (SQLException rollbackEx) {
                System.err.println("Error during rollback: " + rollbackEx.getMessage());
            }
            System.err.println("Database error: " + e.getMessage());
        } finally {

            try {
                if (selectStmt != null) selectStmt.close();
                if (updateStmt != null) updateStmt.close();
                if (connection != null) connection.close();
            } catch (SQLException closeEx) {
                System.err.println("Error closing resources: " + closeEx.getMessage());
            }
        }
    }
}