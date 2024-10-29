import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Date;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.SqlDateModel;

public class ReadPage extends JFrame {
    private JTextArea entryTextArea;
    private JDatePickerImpl datePicker;
    private JButton backButton;

    public ReadPage() {
        setTitle("Read Entry");
        setSize(800, 600); // Match size with MenuPage
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
        datePicker.setPreferredSize(new Dimension(200, 30));
        mainPanel.add(datePicker, gbc);

        // Entry Text Area
        entryTextArea = new JTextArea();
        entryTextArea.setLineWrap(true);
        entryTextArea.setWrapStyleWord(true);
        entryTextArea.setFont(new Font("Verdana", Font.PLAIN, 14));
        entryTextArea.setForeground(Color.WHITE);
        entryTextArea.setBackground(new Color(60, 63, 65));
        entryTextArea.setEditable(false);
        entryTextArea.setBorder(BorderFactory.createCompoundBorder(
                entryTextArea.getBorder(),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        JScrollPane scrollPane = new JScrollPane(entryTextArea);
        scrollPane.setPreferredSize(new Dimension(500, 300)); // New dimensions for the text area
        mainPanel.add(scrollPane, gbc);

        // Back Button
        backButton = createCustomButton("Back", "back_icon.png");
        mainPanel.add(backButton, gbc);

        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new MenuPage().setVisible(true);
                dispose(); // Close this page
            }
        });

        // Add action listener to date picker
        datePicker.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadEntry();
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

    private void loadEntry() {
        Date selectedDate = (Date) datePicker.getModel().getValue();
        if (selectedDate == null) {
            entryTextArea.setText(""); // Clear the text area if no date is selected
            return;
        }
        java.sql.Date sqlDate = new java.sql.Date(selectedDate.getTime());
        Connection conn = DatabaseConnection.getConnection();
        if (conn != null) {
            try {
                String query = "SELECT content FROM diary_entries WHERE user_id = ? AND entry_date = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, 1); // Replace with the logged-in user's ID
                stmt.setDate(2, sqlDate);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    entryTextArea.setText(rs.getString("content"));
                } else {
                    entryTextArea.setText(""); // Clear the text area if no entry is found
                    JOptionPane.showMessageDialog(this, "No entry found for the selected date.");
                }
                rs.close();
                stmt.close();
                conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error loading the entry.");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ReadPage().setVisible(true));
    }
}
