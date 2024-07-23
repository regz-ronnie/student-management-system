import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class unitregistration extends JFrame {
    private JPanel panel1;
    private JCheckBox selectCheckBox;
    private JCheckBox selectCheckBox1;
    private JCheckBox selectCheckBox2;
    private JCheckBox selectCheckBox3;
    private JButton confirmButton;
    private void createUIComponents() {
        // Initialize custom components here if needed
        selectCheckBox = new JCheckBox("Unit 1");
        selectCheckBox1 = new JCheckBox("Unit 2");
        selectCheckBox2 = new JCheckBox("Unit 3");
        selectCheckBox3 = new JCheckBox("Unit 4");
        confirmButton = new JButton("Confirm");
    }

    public unitregistration() {
        setTitle("Unit Registration");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        panel1 = new JPanel();
        panel1.setLayout(new BoxLayout(panel1, BoxLayout.Y_AXIS));

        selectCheckBox = new JCheckBox("Unit 1");
        selectCheckBox1 = new JCheckBox("Unit 2");
        selectCheckBox2 = new JCheckBox("Unit 3");
        selectCheckBox3 = new JCheckBox("Unit 4");
        confirmButton = new JButton("Confirm");

        panel1.add(selectCheckBox);
        panel1.add(selectCheckBox1);
        panel1.add(selectCheckBox2);
        panel1.add(selectCheckBox3);
        panel1.add(confirmButton);

        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleConfirmButtonClick();
            }
        });

        add(panel1);
        setVisible(true);
    }

    private void handleConfirmButtonClick() {
        StringBuilder selectedUnits = new StringBuilder("Selected Units:\n");
        if (selectCheckBox.isSelected()) {
            selectedUnits.append("Unit 1\n");
        }
        if (selectCheckBox1.isSelected()) {
            selectedUnits.append("Unit 2\n");
        }
        if (selectCheckBox2.isSelected()) {
            selectedUnits.append("Unit 3\n");
        }
        if (selectCheckBox3.isSelected()) {
            selectedUnits.append("Unit 4\n");
        }

        JOptionPane.showMessageDialog(this, selectedUnits.toString());

        // Insert selected units into database
        insertSelectedUnits();
    }

    private void insertSelectedUnits() {
        final String DB_URL = "jdbc:mysql://localhost/mystore?serverTimezone=UTC";
        final String USERNAME = "root";
        final String PASSWORD = "";

        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
            String sql = "INSERT INTO unit_selections (unit_name) VALUES (?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);

            if (selectCheckBox.isSelected()) {
                preparedStatement.setString(1, "Unit 1");
                preparedStatement.executeUpdate();
            }
            if (selectCheckBox1.isSelected()) {
                preparedStatement.setString(1, "Unit 2");
                preparedStatement.executeUpdate();
            }
            if (selectCheckBox2.isSelected()) {
                preparedStatement.setString(1, "Unit 3");
                preparedStatement.executeUpdate();
            }
            if (selectCheckBox3.isSelected()) {
                preparedStatement.setString(1, "Unit 4");
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new unitregistration());
    }
}
