import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class register extends JDialog {
    private JTextField txtname;
    private JButton btnregister;
    private JButton btncancel;
    private JPanel RegisterPanel;
    private JTextField txtemail;
    private JTextField txtphone;
    private JTextField txtaddress;
    private JPasswordField pwd;
    private JPasswordField cfpwd;
    public User user;//user: An instance of the User class to hold registered user data.


    public register(JFrame parent) {
        super(parent);
        setTitle("CREATE NEW ACCOUNT");
        setContentPane(RegisterPanel);
        setMinimumSize(new Dimension(800, 600));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        btnregister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });

        btncancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        setVisible(true);
    }

    private void registerUser() {
        String name = txtname.getText();
        String email = txtemail.getText();
        String phone = txtphone.getText();
        String address = txtaddress.getText();
        String password = String.valueOf(pwd.getPassword());
        String confirmpassword = String.valueOf(cfpwd.getPassword());

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter all fields", "Try again", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmpassword)) {
            JOptionPane.showMessageDialog(this, "Confirm password does not match", "Try again", JOptionPane.ERROR_MESSAGE);
            return;
        }

        user = adduserToDatabase(name, email, phone, address, password);//Calls adduserToDatabase() to insert user data into the MySQL database.

        if (user != null) {
            JOptionPane.showMessageDialog(this, "User registered successfully: " + user.name);
            dispose(); // Close the registration form on successful registration
        } else {
            JOptionPane.showMessageDialog(this, "Failed to register user", "Try again", JOptionPane.ERROR_MESSAGE);
        }
    }

    private User adduserToDatabase(String name, String email, String phone, String address, String password) {
        User user = null;
        final String DB_URL = "jdbc:mysql://localhost/mystore?serverTimezone=UTC";
        final String USERNAME = "root";
        final String PASSWORD = "";

        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql = "INSERT INTO users(name, email, phone, address, password) VALUES(?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, phone);
            preparedStatement.setString(4, address);
            preparedStatement.setString(5, password);

            int addedRows = preparedStatement.executeUpdate();
            if (addedRows > 0) {
                user = new User();
                user.name = name;
                user.email = email;
                user.phone = phone;
                user.address = address;
                user.password = password;
            }

            preparedStatement.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return user;
    }

    public static void main(String[] args) {
        register myform = new register(null);//Creates an instance of register (dialog) with null parent frame.
        User user = myform.user;//Retrieves the user object from the dialog after registration attempt.
        if (user != null) {
            System.out.println("Successful registration of: " + user.name);
        } else {
            System.out.println("User registration was canceled or failed.");
        }
    }
}
