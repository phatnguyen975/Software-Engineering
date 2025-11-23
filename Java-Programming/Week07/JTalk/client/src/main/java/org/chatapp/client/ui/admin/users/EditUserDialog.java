package org.chatapp.client.ui.admin.users;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.chatapp.client.ui.base.BaseDialog;
import org.chatapp.client.ui.components.FormButton;
import org.chatapp.client.ui.components.InputField;

public class EditUserDialog extends BaseDialog {
    private InputField usernamePanel;
    private InputField fullnamePanel;
    private InputField addressPanel;
    private InputField dobPanel;
    private JComboBox<String> genderCbo;
    private InputField emailPanel;

    private List<String> userInfo;

    public EditUserDialog(JFrame parent, List<String> userInfo) {
        super(parent, "Edit User", 0.25, 0.63);
        this.userInfo = userInfo;
    }

    @Override
    protected void setupContent() {
        contentPanel.setLayout(new BorderLayout(0, 10));
        contentPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Title Label
        JLabel titleLabel = new JLabel("Edit User", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));

        // Form Panel
        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

        usernamePanel = new InputField("Username", "text");
        usernamePanel.setEditable(false);
        fullnamePanel = new InputField("Full Name", "text");
        addressPanel = new InputField("Address", "text");
        dobPanel = new InputField("Date of Birth (yyyy-MM-dd)", "text");
        emailPanel = new InputField("Email", "text");
        emailPanel.setEditable(false);

        JPanel genderPanel = new JPanel();
        genderPanel.setLayout(new BoxLayout(genderPanel, BoxLayout.Y_AXIS));
        genderPanel.setOpaque(false);

        JLabel genderLabel = new JLabel("Gender");
        genderLabel.setAlignmentX(LEFT_ALIGNMENT);
        genderLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        genderCbo = new JComboBox<>(new String[] { "Male", "Female", "Other" });
        genderCbo.setAlignmentX(LEFT_ALIGNMENT);
        genderCbo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        genderCbo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        genderPanel.add(genderLabel);
        genderPanel.add(Box.createVerticalStrut(2));
        genderPanel.add(genderCbo);

        formPanel.add(usernamePanel);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(fullnamePanel);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(addressPanel);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(dobPanel);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(genderPanel);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(emailPanel);

        // Save Button
        FormButton saveBtn = new FormButton("Save Changes");

        contentPanel.add(titleLabel, BorderLayout.NORTH);
        contentPanel.add(formPanel, BorderLayout.CENTER);
        contentPanel.add(saveBtn, BorderLayout.SOUTH);

        // TODO: Load existing user info
        loadUserInfo();

        saveBtn.addActionListener(e -> handleSaveChanges());
    }

    private String orEmpty(String value) {
        return value == null ? "" : value;
    }

    private void loadUserInfo() {
        usernamePanel.setContent(orEmpty(userInfo.get(0)));
        fullnamePanel.setContent(orEmpty(userInfo.get(1)));
        addressPanel.setContent(orEmpty(userInfo.get(2)));
        dobPanel.setContent(orEmpty(userInfo.get(3)));
        emailPanel.setContent(orEmpty(userInfo.get(5)));

        String gender = userInfo.get(4);
        if (gender == null) {
            gender = "Other";
        }
        genderCbo.setSelectedItem(gender);
    }

    private void handleSaveChanges() {
        // TODO: Update user info
        JOptionPane.showMessageDialog(this, "User information updated!");
        this.dispose();
    }
}
