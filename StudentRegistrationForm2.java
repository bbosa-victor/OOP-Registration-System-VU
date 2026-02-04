
package group.studentregistrationform2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class StudentRegistrationForm2 extends JFrame {
    // Declare components
    private JTextField firstNameField, lastNameField, emailField, confirmEmailField;
    private JPasswordField passwordField, confirmPasswordField;
    private JComboBox<String> yearBox, monthBox, dayBox;
    private JRadioButton maleBtn, femaleBtn;
    private ButtonGroup genderGroup;
    private JComboBox<String> deptBox;
    private JTextArea outputArea;

    private JButton submitBtn, cancelBtn;

    // Auto ID counter
    private static int idCounter = 1;

    // Constructor 
    public StudentRegistrationForm2() {
        setTitle("Student Registration Form");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(14, 2, 10, 5));
        add(formPanel);

        // First Name

        formPanel.add(new JLabel("First Name:"));
        firstNameField = new JTextField();
        formPanel.add(firstNameField);

        // Last Name
        formPanel.add(new JLabel("Last Name:"));
        lastNameField = new JTextField();
        formPanel.add(lastNameField);

        // Email
        formPanel.add(new JLabel("Email Address:"));
        emailField = new JTextField();
        formPanel.add(emailField);

        // Confirm Email
        formPanel.add(new JLabel("Confirm Email Address:"));
        confirmEmailField = new JTextField();

        formPanel.add(confirmEmailField);

        // Password
        formPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        formPanel.add(passwordField);

        // Confirm Password
        formPanel.add(new JLabel("Confirm Password:"));
        confirmPasswordField = new JPasswordField();
        formPanel.add(confirmPasswordField);

        // Date of Birth
        formPanel.add(new JLabel("Select Year:"));
        int currentYear = java.time.LocalDate.now().getYear();
        DefaultComboBoxModel<String> yearModel = new DefaultComboBoxModel<>();
        for (int y = currentYear; y >= 1960; y--) {   // adjust start year as needed
            yearModel.addElement(String.valueOf(y));
        }
        yearBox = new JComboBox<>(yearModel);
        formPanel.add(yearBox);

        formPanel.add(new JLabel("Select Month:"));
        monthBox = new JComboBox<>(new String[]{
            "January","February","March","April","May","June",
            "July","August","September","October","November","December"});
        formPanel.add(monthBox);

        formPanel.add(new JLabel("Select Day:"));
        DefaultComboBoxModel<String> dayModel = new DefaultComboBoxModel<>();
        for (int d = 1; d <= 31; d++) {
            dayModel.addElement(String.valueOf(d));
        }
        dayBox = new JComboBox<>(dayModel);
        formPanel.add(dayBox);

        // Gender

        formPanel.add(new JLabel("Gender:"));
        maleBtn = new JRadioButton("Male");
        femaleBtn = new JRadioButton("Female");
        genderGroup = new ButtonGroup();
        genderGroup.add(maleBtn);
        genderGroup.add(femaleBtn);
        JPanel genderPanel = new JPanel();
        genderPanel.add(maleBtn);
        genderPanel.add(femaleBtn);
        formPanel.add(genderPanel);

        // Department
        formPanel.add(new JLabel("Department:"));
        deptBox = new JComboBox<>(new String[]{
            "Civil", "Computer Science and Engineering", "Electrical",
            "Electronics and Communication", "Mechanical"
        });
        formPanel.add(deptBox);

        // Buttons
        submitBtn = new JButton("Submit");
        cancelBtn = new JButton("Cancel");
        formPanel.add(submitBtn);
        formPanel.add(cancelBtn);

        // Output Area
        formPanel.add(new JLabel("Your Data is Below:"));
        outputArea = new JTextArea(100, 100);
        outputArea.setEditable(false);
        formPanel.add(new JScrollPane(outputArea));

        // Button Actions
        submitBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                handleSubmit();
            }
        });

        cancelBtn.addActionListener(e -> System.exit(0));

        // Show the window
        setVisible(true);
    }

    // Validation and output
    private void handleSubmit() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();

        String confirmEmail = confirmEmailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String year = (String) yearBox.getSelectedItem();
        String month = (String) monthBox.getSelectedItem();
        String day = (String) dayBox.getSelectedItem();
        String gender = maleBtn.isSelected() ? "M" : femaleBtn.isSelected() ? "F" : "";
        String department = (String) deptBox.getSelectedItem();

        // Basic validation
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || confirmEmail.isEmpty()
                || password.isEmpty() || confirmPassword.isEmpty() || gender.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.");
            return;
        }

        if (!email.equals(confirmEmail)) {
            JOptionPane.showMessageDialog(this, "Emails do not match.");
            return;
        }

        // Password rules
        if (!password.equals(confirmPassword)) {
            

        JOptionPane.showMessageDialog(this, "Passwords do not match.");
            return;
        }
        if (password.length() < 8) {
            JOptionPane.showMessageDialog(this, "Password must be at least 8 characters.");
            return;
        }
        if (!password.matches(".*[A-Za-z].*") || !password.matches(".*\\d.*")) {
            JOptionPane.showMessageDialog(this, "Password must contain both letters and digits.");
            return;
        }

        // Age validation
        int birthYear = Integer.parseInt(year);

        int currentYear = java.time.LocalDate.now().getYear();
        int age = currentYear - birthYear;
        if (age < 16 || age > 60) {
            JOptionPane.showMessageDialog(this, "Age must be between 16 and 60 years.");
            return;
        }

        // Auto ID generation
        String studentId = String.format("2026-%05d", idCounter++);

        // Display formatted output
        String formatted = String.format("%s | %s %s | %s | %s | %s-%s-%s | %s",
                studentId, firstName, lastName, gender, department, year, month, day, email);
        outputArea.setText(formatted);


        // Save to CSV
        try (java.io.FileWriter writer = new java.io.FileWriter("students.csv", true)) {
            writer.write(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
                studentId, firstName, lastName, gender, department,
                year, month, day, email));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving data: " + ex.getMessage());
        }
    }

    // MAIN METHOD
    public static void main(String[] args) {
        new StudentRegistrationForm2(); // Launch the form
    }

}
