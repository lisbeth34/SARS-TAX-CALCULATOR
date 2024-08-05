
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

// Main class for the Tax Calculator App
public class TaxCalculatorApp extends JFrame {
    private JTextField incomeField; // Text field for entering income
    private JTextField ageField; // Text field for entering age
    private JComboBox<String> yearComboBox; // Combo box for selecting tax year
    private JLabel resultLabel; // Label for displaying tax calculation result

    private JTextField nameField; // Text field for entering name
    private JTextField emailField; // Text field for entering email
    private JRadioButton yesRadioButton; // Radio button for selecting "Yes" for citizenship
    private JRadioButton noRadioButton; // Radio button for selecting "No" for citizenship
    private JTextField idNumberField; // Text field for entering ID number
    private JTextField dobField; // Text field for entering date of birth
    private JPanel registrationPanel; // Panel for the registration form
    private JPanel calculatorPanel; // Panel for the tax calculator

    // Static maps for storing tax brackets, rates, base taxes, and rebates for different years
    private static final Map<String, double[]> BRACKETS_MAP = new HashMap<>();
    private static final Map<String, double[]> RATES_MAP = new HashMap<>();
    private static final Map<String, double[]> BASE_TAX_MAP = new HashMap<>();
    private static final Map<String, double[]> REBATES_MAP = new HashMap<>();

    // Patterns for validating email and ID number, and a formatter for date of birth
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern ID_PATTERN = Pattern.compile("^\\d{13}$");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Static initializer block to populate the tax information maps
    static {
        // Tax brackets, rates, and rebates for 2023
        BRACKETS_MAP.put("2023", new double[]{205900, 321600, 445100, 584200, 744800, 1577300});
        RATES_MAP.put("2023", new double[]{0.18, 0.26, 0.31, 0.36, 0.39, 0.41, 0.45});
        BASE_TAX_MAP.put("2023", new double[]{0, 37062, 67144, 105429, 155505, 218139, 559464});
        REBATES_MAP.put("2023", new double[]{16425, 9000, 2997});

        // Tax brackets, rates, and rebates for 2024
        BRACKETS_MAP.put("2024", new double[]{217000, 339000, 469000, 615000, 784000, 1650000});
        RATES_MAP.put("2024", new double[]{0.18, 0.26, 0.31, 0.36, 0.39, 0.41, 0.45});
        BASE_TAX_MAP.put("2024", new double[]{0, 39000, 70620, 110739, 163335, 229089, 586928});
        REBATES_MAP.put("2024", new double[]{17230, 9400, 3100});
    }

    // Constructor for the TaxCalculatorApp class
    public TaxCalculatorApp() {
        setTitle("User Registration and Tax Calculator"); // Set window title
        setSize(600, 500); // Set window size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Set default close operation
        setLocationRelativeTo(null); // Center window on screen

        showRegistrationForm(); // Display the registration form
    }

    // Method to show the registration form
    private void showRegistrationForm() {
        if (calculatorPanel != null) {
            remove(calculatorPanel); // Remove the calculator panel if it exists
        }

        registrationPanel = new BackgroundPanel(); // Create a custom panel for the registration form
        registrationPanel.setLayout(new GridBagLayout()); // Set layout manager
        GridBagConstraints gbc = new GridBagConstraints(); // Create constraints for layout
        gbc.insets = new Insets(10, 10, 10, 10); // Set padding for components

        // Create and add components to the registration panel
        JLabel sarsLabel = new JLabel("SARS"); // Title label
        sarsLabel.setFont(sarsLabel.getFont().deriveFont(24f)); // Set font size
        JLabel descriptionLabel = new JLabel("South African Revenue Services"); // Description label
        JLabel instructionLabel = new JLabel("Register"); // Instruction label
        instructionLabel.setFont(instructionLabel.getFont().deriveFont(18f)); // Set font size
        JLabel detailsLabel = new JLabel("Please enter your personal details"); // Details label

        JLabel nameLabel = new JLabel("Name and Surname: "); // Label for name
        nameField = new JTextField(15); // Text field for name

        JLabel emailLabel = new JLabel("Email: "); // Label for email
        emailField = new JTextField(15); // Text field for email

        JLabel citizenLabel = new JLabel("Are you a South African citizen? "); // Label for citizenship
        yesRadioButton = new JRadioButton("Yes"); // Radio button for "Yes"
        noRadioButton = new JRadioButton("No"); // Radio button for "No"
        ButtonGroup citizenGroup = new ButtonGroup(); // Button group for citizenship radio buttons
        citizenGroup.add(yesRadioButton); // Add "Yes" button to group
        citizenGroup.add(noRadioButton); // Add "No" button to group

        JLabel idNumberLabel = new JLabel("Identification Number: "); // Label for ID number
        idNumberField = new JTextField(15); // Text field for ID number

        JLabel dobLabel = new JLabel("Date of Birth (YYYY-MM-DD): "); // Label for date of birth
        dobField = new JTextField(15); // Text field for date of birth

        JButton nextButton = new JButton("Next"); // Button to proceed to the next form
        nextButton.addActionListener(new RegistrationButtonListener()); // Add action listener to button

        // Position components using GridBagConstraints
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        registrationPanel.add(sarsLabel, gbc);
        gbc.gridy++;
        registrationPanel.add(descriptionLabel, gbc);
        gbc.gridy++;
        registrationPanel.add(instructionLabel, gbc);
        gbc.gridy++;
        registrationPanel.add(detailsLabel, gbc);

        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = 1;
        gbc.gridy++;
        registrationPanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        registrationPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        registrationPanel.add(emailLabel, gbc);
        gbc.gridx = 1;
        registrationPanel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        registrationPanel.add(citizenLabel, gbc);
        gbc.gridx = 1;
        registrationPanel.add(yesRadioButton, gbc);
        gbc.gridx = 2;
        registrationPanel.add(noRadioButton, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        registrationPanel.add(idNumberLabel, gbc);
        gbc.gridx = 1;
        registrationPanel.add(idNumberField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        registrationPanel.add(dobLabel, gbc);
        gbc.gridx = 1;
        registrationPanel.add(dobField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        registrationPanel.add(nextButton, gbc);

        add(registrationPanel); // Add registration panel to the frame
        revalidate(); // Refresh the frame
        repaint(); // Repaint the frame
    }

    // Method to show the tax calculator form
    private void showTaxCalculator() {
        // Remove the registration panel
        remove(registrationPanel);

        // Create a background panel with a beige gold color
        calculatorPanel = new BackgroundPanel(new Color(255, 228, 196)); // Beige gold color
        calculatorPanel.setLayout(new GridBagLayout()); // Set layout manager
        GridBagConstraints gbc = new GridBagConstraints(); // Create constraints for layout
        gbc.insets = new Insets(10, 10, 10, 10); // Set padding for components

        // Create and add components to the calculator panel
        JLabel titleLabel = new JLabel("SARS Calculator"); // Title label
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 24f)); // Bold and larger font

        JLabel yearLabel = new JLabel("Select Tax Year: "); // Label for tax year
        yearComboBox = new JComboBox<>(new String[]{"2023", "2024"}); // Combo box for tax year

        JLabel incomeLabel = new JLabel("Annual Income (R): "); // Label for income
        incomeField = new JTextField(10); // Text field for income

        JLabel ageLabel = new JLabel("Age: "); // Label for age
        ageField = new JTextField(10); // Text field for age

        resultLabel = new JLabel("Your total tax payable is: "); // Label for displaying result

        JButton calculateButton = new JButton("Calculate"); // Button to calculate tax
        calculateButton.addActionListener(new CalculateButtonListener()); // Add action listener to button

        JButton backButton = new JButton("Back"); // Button to go back to registration form
        backButton.addActionListener(e -> showRegistrationForm()); // Add action listener to button

        JButton exitButton = new JButton("Exit"); // Button to exit the application
        exitButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(TaxCalculatorApp.this, "Goodbye!", "Exit", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0); // Exit the application
        });

        // Position components using GridBagConstraints
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        calculatorPanel.add(titleLabel, gbc);
        
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy++;
        calculatorPanel.add(yearLabel, gbc);
        gbc.gridx = 1;
        calculatorPanel.add(yearComboBox, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        calculatorPanel.add(incomeLabel, gbc);
        gbc.gridx = 1;
        calculatorPanel.add(incomeField, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        calculatorPanel.add(ageLabel, gbc);
        gbc.gridx = 1;
        calculatorPanel.add(ageField, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        calculatorPanel.add(new JLabel(), gbc); // Empty cell for alignment
        gbc.gridx = 1;
        calculatorPanel.add(calculateButton, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        calculatorPanel.add(resultLabel, gbc);
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        calculatorPanel.add(backButton, gbc);
        gbc.gridx = 1;
        calculatorPanel.add(exitButton, gbc);

        add(calculatorPanel); // Add calculator panel to the frame
        revalidate(); // Refresh the frame
        repaint(); // Repaint the frame
    }

    // Inner class for handling registration form actions
    private class RegistrationButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Get input values from the registration form
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String idNumber = idNumberField.getText().trim();
            String dob = dobField.getText().trim();
            boolean isCitizen = yesRadioButton.isSelected();

            // Validate the inputs
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(TaxCalculatorApp.this, "Please enter your name.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (email.isEmpty() || !EMAIL_PATTERN.matcher(email).matches()) {
                JOptionPane.showMessageDialog(TaxCalculatorApp.this, "Please enter a valid email address.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!yesRadioButton.isSelected() && !noRadioButton.isSelected()) {
                JOptionPane.showMessageDialog(TaxCalculatorApp.this, "Please select whether you are a South African citizen.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (idNumber.isEmpty() || !ID_PATTERN.matcher(idNumber).matches()) {
                JOptionPane.showMessageDialog(TaxCalculatorApp.this, "Please enter a valid 13-digit identification number.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (dob.isEmpty()) {
                JOptionPane.showMessageDialog(TaxCalculatorApp.this, "Please enter your date of birth.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                LocalDate parsedDate = LocalDate.parse(dob, DATE_FORMATTER); // Parse date of birth
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(TaxCalculatorApp.this, "Please enter the date of birth in the format YYYY-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Display success message and proceed to tax calculator
            JOptionPane.showMessageDialog(TaxCalculatorApp.this, "Registration successful! You may proceed.", "Success", JOptionPane.INFORMATION_MESSAGE);
            showTaxCalculator(); // Show the tax calculator form
        }
    }

    // Inner class for handling tax calculation actions
    private class CalculateButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                // Get input values from the tax calculator form
                String selectedYear = (String) yearComboBox.getSelectedItem();
                String incomeText = incomeField.getText().trim();
                String ageText = ageField.getText().trim();

                // Validate the inputs
                if (incomeText.isEmpty() || ageText.isEmpty()) {
                    throw new NumberFormatException("Empty fields");
                }

                double income = Double.parseDouble(incomeText); // Parse income
                int age = Integer.parseInt(ageText); // Parse age

                double taxBeforeRebate = calculateTax(income, selectedYear); // Calculate tax before rebate
                double rebates = calculateRebates(age, selectedYear); // Calculate rebates
                double taxPayable = taxBeforeRebate - rebates; // Calculate total tax payable

                if (taxPayable < 0) {
                    taxPayable = 0;
                }

                resultLabel.setText(String.format("Your total tax payable is: R %.2f", taxPayable)); // Display result
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(TaxCalculatorApp.this, "Please enter valid numeric values for income and age.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Method to calculate tax based on income and tax year
    private double calculateTax(double income, String year) {
        double[] brackets = BRACKETS_MAP.get(year); // Get tax brackets for the year
        double[] rates = RATES_MAP.get(year); // Get tax rates for the year
        double[] baseTax = BASE_TAX_MAP.get(year); // Get base taxes for the year

        double tax = 0;
        for (int i = brackets.length - 1; i >= 0; i--) {
            if (income > brackets[i]) {
                tax = baseTax[i] + (income - brackets[i]) * rates[i + 1];
                break;
            }
        }
        if (income <= brackets[0]) {
            tax = income * rates[0];
        }
        return tax; // Return calculated tax
    }

    // Method to calculate rebates based on age and tax year
    private double calculateRebates(int age, String year) {
        double[] rebates = REBATES_MAP.get(year); // Get rebates for the year
        double rebate = rebates[0]; // Primary rebate
        if (age >= 65) {
            rebate += rebates[1]; // Secondary rebate
        }
        if (age >= 75) {
            rebate += rebates[2]; // Tertiary rebate
        }
        return rebate; // Return total rebateshl
    }

    // Main method to run the application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TaxCalculatorApp app = new TaxCalculatorApp(); // Create an instance of the app
            app.setVisible(true); // Make the app visible
        });
    }

    // Custom JPanel class to paint the background image or color
    private class BackgroundPanel extends JPanel {
        private BufferedImage backgroundImage; // Background image

        public BackgroundPanel() {
            try {
                // Load the background image
                backgroundImage = ImageIO.read(new File("57.jpg")); // Replace with your image file path
            } catch (IOException e) {
                e.printStackTrace(); // Print stack trace if an error occurs
            }
        }

        public BackgroundPanel(Color bgColor) {
            setBackground(bgColor); // Set background color
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g); // Call superclass method
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this); // Draw background image
            }
        }
    }
}
