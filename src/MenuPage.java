import com.toedter.calendar.JCalendar;
import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MenuPage extends JFrame {
    private JButton writeButton;
    private JButton readButton;
    private JButton editButton;
    private JButton deleteButton;
    private JLabel dateLabel;
    private JLabel userLabel;

    public MenuPage() {
        setTitle("Diary Menu");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main Panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        add(mainPanel);

        // Menu Panel with Background Color
        JPanel menuPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(45, 45, 45)); // Darker background color
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        menuPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(10, 10, 10, 10); // Add some padding
        gbc.anchor = GridBagConstraints.WEST; // Left align the components

        // Date and Username Labels
        dateLabel = new JLabel(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        userLabel = new JLabel("User: " + getUsername());
        dateLabel.setFont(new Font("Verdana", Font.BOLD, 16));
        userLabel.setFont(new Font("Verdana", Font.BOLD, 16));
        dateLabel.setForeground(Color.WHITE);
        userLabel.setForeground(Color.WHITE);
        menuPanel.add(dateLabel, gbc);
        menuPanel.add(userLabel, gbc);

        // Customize Buttons
        writeButton = createCustomButton("Write Entry", "write_icon.png");
        readButton = createCustomButton("Read Entry", "read_icon.png");
        editButton = createCustomButton("Edit Entry", "edit_icon.png");
        deleteButton = createCustomButton("Delete Entry", "delete_icon.png");
        menuPanel.add(writeButton, gbc);
        menuPanel.add(readButton, gbc);
        menuPanel.add(editButton, gbc);
        menuPanel.add(deleteButton, gbc);

        // Add Action Listeners for Buttons
        writeButton.addActionListener(e -> {
            new EntryPage().setVisible(true);
            dispose();
        });

        readButton.addActionListener(e -> {
            new ReadPage().setVisible(true);
            dispose();
        });

        editButton.addActionListener(e -> {
            new EditPage().setVisible(true);
            dispose();
        });

        deleteButton.addActionListener(e -> {
            new DeletePage().setVisible(true);
            dispose();
        });

        // Calendar Panel with Padding
        JPanel calendarPanel = new JPanel();
        calendarPanel.setPreferredSize(new Dimension(400, 600));
        calendarPanel.setLayout(new BorderLayout());
        calendarPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add padding
        calendarPanel.setBackground(new Color(45, 45, 45)); // Match background color

        JCalendar calendar = new JCalendar();
        customizeCalendar(calendar);

        calendarPanel.add(calendar, BorderLayout.CENTER);
        mainPanel.add(menuPanel, BorderLayout.CENTER);
        mainPanel.add(calendarPanel, BorderLayout.EAST);
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

    private String getUsername() {
        String username = "Unknown";
        try (Connection connection = DatabaseConnection.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT username FROM users LIMIT 1");
            if (resultSet.next()) {
                username = resultSet.getString("username");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return username;
    }

    private void customizeCalendar(JCalendar calendar) {
        calendar.setFont(new Font("Verdana", Font.PLAIN, 14));
        calendar.getDayChooser().getDayPanel().setBackground(new Color(60, 63, 65));
        calendar.getMonthChooser().getComboBox().setBackground(new Color(70, 130, 180));
        calendar.getMonthChooser().getComboBox().setForeground(Color.WHITE);
        calendar.getYearChooser().getSpinner().setForeground(Color.WHITE);
        calendar.getYearChooser().getSpinner().setBackground(new Color(70, 130, 180));
        calendar.setForeground(Color.BLACK);

        UIManager.put("Calendar.background", new ColorUIResource(new Color(60, 63, 65)));
        UIManager.put("Calendar.foreground", new ColorUIResource(Color.WHITE));
        UIManager.put("Calendar.selectionBackground", new ColorUIResource(new Color(70, 130, 180)));
        UIManager.put("Calendar.selectionForeground", new ColorUIResource(Color.WHITE));
        UIManager.put("Calendar.font", new FontUIResource(new Font("Verdana", Font.PLAIN, 14)));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MenuPage().setVisible(true));
    }
}
