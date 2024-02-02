package dev.redvx.loader.protection;

import dev.redvx.loader.util.Cryptography;
import dev.redvx.loader.util.NoStackTrace;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;

public class Authentication {
    public static void auth() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            throw new NoStackTrace("UIManager failed to initialize");
        }

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel titleLabel = new JLabel("Enter Credentials");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, gbc);

        gbc.gridy++;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(new JLabel("Username:"), gbc);

        gbc.gridy++;
        panel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        JTextField usernameField = new JTextField(15);
        panel.add(usernameField, gbc);

        gbc.gridy++;
        JPasswordField passwordField = new JPasswordField(15);
        panel.add(passwordField, gbc);

        gbc.gridy++;
        JCheckBox showPasswordCheckbox = new JCheckBox("Show Password");
        panel.add(showPasswordCheckbox, gbc);

        showPasswordCheckbox.addItemListener(itemEvent -> {
            if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('*');
            }
        });

        JOptionPane pane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
        JDialog dialog = pane.createDialog("Authentication System");
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.setVisible(true);

        int result = (int) pane.getValue();

        if (result == JOptionPane.OK_OPTION) {
            String currentHWID = HWIDProtection.getHardWareID();
            char[] passwordChars = passwordField.getPassword();
            String password = new String(passwordChars);
            try {
                URL url = new URL("https://pastebin.com/raw/wTqGVsKw");
                BufferedReader reader = new BufferedReader(new InputStreamReader(url.openConnection().getInputStream()));
                String userdata = Cryptography.encrypt(usernameField.getText() + ":" + password) + ":" + HWIDProtection.encryptHWID(currentHWID);
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.equals(userdata)) {
                        NoStackTraceCrash("Stop trying to crack the client.");
                    }
                }
            } catch (Exception e) {
                NoStackTraceCrash("Error contacting server");
            }
        } else {
            NoStackTraceCrash("User Closed Login Gui");
        }
    }

    private static void NoStackTraceCrash(String message) {
        try {
            System.out.println(message);
            Method shutdownMethod = Class.forName("java.lang.Shutdown").getDeclaredMethod("exit", int.class);
            shutdownMethod.setAccessible(true);
            shutdownMethod.invoke(null, 0);
        } catch (Exception e) {
            throw new NoStackTrace(message);
        }
    }
}