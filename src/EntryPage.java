import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.SqlDateModel;

public class EntryPage extends JFrame {
    private JTextArea entryTextArea;
    private JDatePickerImpl datePicker;
    private JButton saveButton;
    private JButton backButton;

    public EntryPage() {
        setTitle("Write Entry");
        setSize(800, 600); // increased size to match MenuPage
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main Panel with Background Color
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(45, 45, 45)); // Darker background color
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new GridBagLayout());
        add(mainPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Date Picker setup
        SqlDateModel model = new SqlDateModel();
        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");
        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
        datePicker.setBounds(100, 20, 200, 30);
        mainPanel.add(datePicker, gbc);

        // Entry Text Area
        entryTextArea = new JTextArea();
        entryTextArea.setLineWrap(true);
        entryTextArea.setWrapStyleWord(true);
        entryTextArea.setFont(new Font("Verdana", Font.PLAIN, 14));
        entryTextArea.setForeground(Color.WHITE);
        entryTextArea.setBackground(new Color(60, 63, 65));
        entryTextArea.setBorder(BorderFactory.createCompoundBorder(
                entryTextArea.getBorder(),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        JScrollPane scrollPane = new JScrollPane(entryTextArea);
        scrollPane.setPreferredSize(new Dimension(500, 300)); // New dimensions for the text area
        mainPanel.add(scrollPane, gbc);

        // Save Button
        saveButton = createCustomButton("Save Entry", "save_icon.png");
        mainPanel.add(saveButton, gbc);

        // Back Button
        backButton = createCustomButton("Back", "back_icon.png");
        mainPanel.add(backButton, gbc);

        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveEntry();
            }
        });

        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new MenuPage().setVisible(true);
                dispose(); // Close this page
            }
        });
    }

    private JButton createCustomButton(String text, String iconPath) {
        JButton button = new JButton(text, new ImageIcon(iconPath));
        button.setPreferredSize(new Dimension(250, 50));
        button.setFont(new Font("Verdana", Font.BOLD, 18));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setHorizontalTextPosition(SwingConstants.RIGHT);
        button.setIconTextGap(10);
        return button;
    }

    private void saveEntry() {
        java.util.Date selectedDate = (java.util.Date) datePicker.getModel().getValue();
        String content = entryTextArea.getText();
        if (selectedDate == null || content.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }
        java.sql.Date sqlDate = new java.sql.Date(selectedDate.getTime());
        Connection conn = DatabaseConnection.getConnection();
        if (conn != null) {
            try {
                String query = "INSERT INTO diary_entries (user_id, entry_date, content) VALUES (?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, 1); // Assuming user_id is 1 for now; you can replace this with the actual logged-in user's ID
                stmt.setDate(2, sqlDate);
                stmt.setString(3, content);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Entry saved successfully!");
                stmt.close();
                conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error saving the entry.");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EntryPage().setVisible(true));
    }
}
