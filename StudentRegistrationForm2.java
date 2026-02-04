package group.studentregistrationform2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*; // For Access Database connection
import java.time.YearMonth; // For leap year logic

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

    public StudentRegistrationForm2() {
        setTitle("Student Registration Form");
        setSize(700, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(15, 2, 10, 5));
        add(new JScrollPane(formPanel));

        // Text Fields
        formPanel.add(new JLabel("First Name:"));
        firstNameField = new JTextField();
        formPanel.add(firstNameField);

        formPanel.add(new JLabel("Last Name:"));
        lastNameField = new JTextField();
        formPanel.add(lastNameField);

        formPanel.add(new JLabel("Email Address:"));
        emailField = new JTextField();
        formPanel.add(emailField);

        formPanel.add(new JLabel("Confirm Email Address:"));
        confirmEmailField = new JTextField();
        formPanel.add(confirmEmailField);

        formPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        formPanel.add(passwordField);

        formPanel.add(new JLabel("Confirm Password:"));
        confirmPasswordField = new JPasswordField();
        formPanel.add(confirmPasswordField);

        // --- Date of Birth Section (Fixed Initialization Order) ---
        formPanel.add(new JLabel("Select Year:"));
        int currentYear = java.time.LocalDate.now().getYear();
        DefaultComboBoxModel<String> yearModel = new DefaultComboBoxModel<>();
        for (int y = currentYear; y >= 1960; y--) {
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
        dayBox = new JComboBox<>(); // Initialize empty first
        formPanel.add(dayBox);

        // Add Leap Year/Month Logic AFTER components are initialized to avoid NullPointerException
        ItemListener dateUpdater = e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                try {
                    int year = Integer.parseInt((String) yearBox.getSelectedItem());
                    int month = monthBox.getSelectedIndex() + 1;
                    int daysInMonth = YearMonth.of(year, month).lengthOfMonth();
                    
                    dayBox.removeAllItems();
                    for (int i = 1; i <= daysInMonth; i++) {
                        dayBox.addItem(String.valueOf(i));
                    }
                } catch (Exception ex) {}
            }
        };
        yearBox.addItemListener(dateUpdater);
        monthBox.addItemListener(dateUpdater);
        // Trigger initial day population
        monthBox.setSelectedIndex(0); 

        // Gender & Department
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
        outputArea = new JTextArea(5, 20);
        outputArea.setEditable(false);
        formPanel.add(new JScrollPane(outputArea));

        submitBtn.addActionListener(e -> handleSubmit());
        cancelBtn.addActionListener(e -> System.exit(0));

        setVisible(true);
    }

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

        // 1. Basic Validation
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || gender.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.");
            return;
        }

        // 2. Email & Password Match
        if (!email.equals(confirmEmail) || !password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Email or Password confirmation does not match.");
            return;
        }

        // 3. Password Length (8-20 characters as required)
        if (password.length() < 8 || password.length() > 20) {
            JOptionPane.showMessageDialog(this, "Password must be between 8 and 20 characters.");
            return;
        }

        // 4. Age Validation (16-60 years)
        int age = java.time.LocalDate.now().getYear() - Integer.parseInt(year);
        if (age < 16 || age > 60) {
            JOptionPane.showMessageDialog(this, "Age must be between 16 and 60 years.");
            return;
        }

        // 5. Auto ID Generation (YYYY-xxxxx)
        String studentId = String.format("2026-%05d", idCounter++);

        // 6. Display Output
        String formatted = String.format("%s | %s %s | %s | %s | %s-%s-%s | %s",
                studentId, firstName, lastName, gender, department, year, month, day, email);
        outputArea.setText(formatted);

        // 7. Save to CSV
        saveToCSV(studentId, firstName, lastName, gender, department, year, month, day, email);

        // 8. Save to MS Access
        saveToAccess(studentId, firstName, lastName, gender, department, year + "-" + month + "-" + day, email);
    }

    private void saveToCSV(String id, String fn, String ln, String g, String d, String y, String m, String day, String em) {
        try (java.io.FileWriter writer = new java.io.FileWriter("students.csv", true)) {
            writer.write(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s\n", id, fn, ln, g, d, y, m, day, em));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "CSV Error: " + ex.getMessage());
        }
    }

    private void saveToAccess(String id, String fName, String lName, String gen, String dept, String dob, String email) {
        String dbUrl = "jdbc:ucanaccess://database/students_db.accdb"; 
        String query = "INSERT INTO Students (StudentID, FirstName, LastName, Gender, Department, DOB, Email) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, id);
            pstmt.setString(2, fName);
            pstmt.setString(3, lName);
            pstmt.setString(4, gen);
            pstmt.setString(5, dept);
            pstmt.setString(6, dob);
            pstmt.setString(7, email);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new StudentRegistrationForm2(); 
    }
}
