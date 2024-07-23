import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.MessageFormat;
import java.util.Vector;

public class Dashboardform extends JFrame {
    private JPanel DashboardPanel;
    private JLabel lbadmin;
    private JButton registerButton;
    private JButton UNITSREGButton;
    private JButton FEESButton;
    private JButton RESULTSButton;
    private JButton PRINTButton;
    private JTable table1;
    private JButton logoutButton;
    private JScrollPane scrollPane;

    private static final String DB_URL = "jdbc:mysql://localhost/mystore?serverTimezone=UTC";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    public Dashboardform() {
        setTitle("Student Management System");
        setContentPane(DashboardPanel);
        setMinimumSize(new Dimension(500, 429));
        setSize(1200, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        DashboardPanel.setLayout(new GridBagLayout());

        // Connect to the database and check if there are registered users
        boolean hasRegisteredUsers = connectToDatabase();
        if (hasRegisteredUsers) {
            // Show login form
            loginform loginform = new loginform(this);
            User user = loginform.user;
            if (user != null) {
                lbadmin.setText("Student: " + user.name);
                setVisible(true);
            } else {
                dispose();
            }
        } else {
            // Show registration form for first-time user
            register register = new register(this);
            User user = register.user;
            if (user != null) {
                lbadmin.setText("Student: " + user.name);
                setVisible(true);
            } else {
                dispose();
            }
        }

        // Register button action
        registerButton.addActionListener(e -> {
            register register = new register(Dashboardform.this);
            User user = register.user;
            if (user != null) {
                JOptionPane.showMessageDialog(Dashboardform.this,
                        "New user registered: " + user.name,
                        "Successful Registration",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // Unit Registration button action
        UNITSREGButton.addActionListener(e -> {
            unitregistration unitRegistration = new unitregistration();
            unitRegistration.setVisible(true);
        });

        // Fees button action
        FEESButton.addActionListener(e -> displayFees());

        // Results button action
        RESULTSButton.addActionListener(e -> displayResults());

        // Print button action
        PRINTButton.addActionListener(e -> printResults());

        // Logout button action
        logoutButton.addActionListener(e -> {
            setVisible(false);
            new Dashboardform().setVisible(true);
        });
    }

    private boolean connectToDatabase() {
        boolean hasRegisteredUsers = false;

        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             Statement statement = conn.createStatement()) {

            // Create tables if they do not exist
            String sqlUsers = "CREATE TABLE IF NOT EXISTS users ("
                    + "id INT(10) NOT NULL AUTO_INCREMENT PRIMARY KEY, "
                    + "name VARCHAR(200) NOT NULL, "
                    + "email VARCHAR(200) NOT NULL UNIQUE, "
                    + "phone VARCHAR(200), "
                    + "address VARCHAR(200), "
                    + "password VARCHAR(200) NOT NULL"
                    + ")";
            statement.execute(sqlUsers);

            String sqlUnits = "CREATE TABLE IF NOT EXISTS units ("
                    + "id INT(10) NOT NULL AUTO_INCREMENT PRIMARY KEY, "
                    + "name VARCHAR(200) NOT NULL, "
                    + "description TEXT"
                    + ")";
            statement.execute(sqlUnits);

            String sqlFees = "CREATE TABLE IF NOT EXISTS fees ("
                    + "id INT(10) NOT NULL AUTO_INCREMENT PRIMARY KEY, "
                    + "student_id INT(10), "
                    + "amount DECIMAL(10, 2), "
                    + "status VARCHAR(50), "
                    + "FOREIGN KEY(student_id) REFERENCES users(id)"
                    + ")";
            statement.execute(sqlFees);

            String sqlResults = "CREATE TABLE IF NOT EXISTS results ("
                    + "id INT(10) NOT NULL AUTO_INCREMENT PRIMARY KEY, "
                    + "student_id INT(10), "
                    + "unit_id INT(10), "
                    + "grade VARCHAR(10), "
                    + "FOREIGN KEY(student_id) REFERENCES users(id), "
                    + "FOREIGN KEY(unit_id) REFERENCES units(id)"
                    + ")";
            statement.execute(sqlResults);

            // Check if there are any registered users
            try (ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM users")) {
                if (resultSet.next()) {
                    int numUsers = resultSet.getInt(1);
                    hasRegisteredUsers = numUsers > 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hasRegisteredUsers;
    }

    private void displayResults() {
        String query = "SELECT r.id, u.name AS unit, r.grade, s.name AS student FROM results r "
                + "JOIN units u ON r.unit_id = u.id "
                + "JOIN users s ON r.student_id = s.id";

        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             Statement statement = conn.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            // Prepare table data
            Vector<String> columnNames = new Vector<>();
            columnNames.add("ID");
            columnNames.add("Student");
            columnNames.add("Unit");
            columnNames.add("Grade");

            Vector<Vector<Object>> data = new Vector<>();
            while (resultSet.next()) {
                Vector<Object> row = new Vector<>();
                row.add(resultSet.getInt("id"));
                row.add(resultSet.getString("student"));
                row.add(resultSet.getString("unit"));
                row.add(resultSet.getString("grade"));
                data.add(row);
            }

            // Remove existing table if present
            if (scrollPane != null) {
                DashboardPanel.remove(scrollPane);
            }

            // Set up the JTable
            table1 = new JTable(data, columnNames);
            scrollPane = new JScrollPane(table1);

            // Add JScrollPane to the panel with correct constraints
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.gridheight = GridBagConstraints.REMAINDER;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            gbc.fill = GridBagConstraints.BOTH;
            DashboardPanel.add(scrollPane, gbc);

            // Update the UI
            DashboardPanel.revalidate();
            DashboardPanel.repaint();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayFees() {
        String query = "SELECT f.id, s.name AS student, f.amount, f.status FROM fees f "
                + "JOIN users s ON f.student_id = s.id";

        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             Statement statement = conn.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            // Prepare table data
            Vector<String> columnNames = new Vector<>();
            columnNames.add("ID");
            columnNames.add("Student");
            columnNames.add("Amount");
            columnNames.add("Status");

            Vector<Vector<Object>> data = new Vector<>();
            while (resultSet.next()) {
                Vector<Object> row = new Vector<>();
                row.add(resultSet.getInt("id"));
                row.add(resultSet.getString("student"));
                row.add(resultSet.getBigDecimal("amount"));
                row.add(resultSet.getString("status"));
                data.add(row);
            }

            // Remove existing table if present
            if (scrollPane != null) {
                DashboardPanel.remove(scrollPane);
            }

            // Set up the JTable
            table1 = new JTable(data, columnNames);
            scrollPane = new JScrollPane(table1);

            // Add JScrollPane to the panel with correct constraints
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.gridheight = GridBagConstraints.REMAINDER;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            gbc.fill = GridBagConstraints.BOTH;
            DashboardPanel.add(scrollPane, gbc);

            // Update the UI
            DashboardPanel.revalidate();
            DashboardPanel.repaint();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void printResults() {
        try {
            if (table1 != null && table1.getRowCount() > 0) {
                boolean printed = table1.print(JTable.PrintMode.FIT_WIDTH, new MessageFormat("Results"), null);
                if (!printed) {
                    JOptionPane.showMessageDialog(this, "Printing was canceled.", "Print Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "No results to print.", "Print Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Dashboardform().setVisible(true));
    }
}
