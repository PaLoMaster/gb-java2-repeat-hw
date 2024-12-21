package ru.khusyainov.gb.java2.hw7.client.swing;

import ru.khusyainov.gb.java2.hw7.ChatHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class HomeWork7SwingClient extends JFrame {
    public static void main(String[] args) throws IOException {
        new HomeWork7SwingClient();
    }

    private final Socket SOCKET;
    private final Scanner FROM_SERVER_IN;
    private final PrintWriter TO_SERVER_OUT;
    private Thread fromServerThread;
    private final Font FONT = new Font("Arial", Font.BOLD, 16);
    private JTextArea history;
    private JPanel loginPanel;
    private JPanel messagePanel;

    public HomeWork7SwingClient() throws IOException {
        SOCKET = new Socket(ChatHelper.SERVER_HOST, ChatHelper.SERVER_PORT);
        FROM_SERVER_IN = new Scanner(SOCKET.getInputStream());
        TO_SERVER_OUT = new PrintWriter(SOCKET.getOutputStream());
        setVisible(false);
        setTitle("Чат");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                fromServerThread.interrupt();
                TO_SERVER_OUT.println(ChatHelper.LOGOUT_COMMAND);
                TO_SERVER_OUT.close();
                FROM_SERVER_IN.close();
                try {
                    SOCKET.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        float windowRatio = 0.6f;
        int width = (int) (screenSize.getWidth() * windowRatio);
        int height = (int) (screenSize.getHeight() * windowRatio);
        setBounds((screenSize.width - width) / 2, (screenSize.height - height) / 2, width, height);
        setAuthorized(false);
        setVisible(true);
    }

    public void setAuthorized(boolean authorized) {
        if (authorized) {
            paintChat();
        } else {
            paintAuthorization();
        }
        repaint();
        revalidate();
    }

    private void paintAuthorization() {
        if (history == null) {
            history = new JTextArea(10, 70);
            history.setLineWrap(true);
            history.setEditable(false);
            history.setFont(FONT);
            fromServerThread = new Thread(() -> {
                try {
                    while (!Thread.interrupted() && FROM_SERVER_IN.hasNextLine()) {
                        String fromServerMessage = FROM_SERVER_IN.nextLine();
                        if (ChatHelper.isAuthorizedStatus(fromServerMessage)) {
                            setAuthorized(true);
                            break;
                        }
                        history.append(fromServerMessage + "\n");
                    }
                    while (!Thread.interrupted() && FROM_SERVER_IN.hasNextLine()) {
                        history.append(FROM_SERVER_IN.nextLine() + "\n");
                    }
                    history.append(ChatHelper.getServerDisconnectedMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            fromServerThread.setDaemon(true);
            fromServerThread.start();
            JScrollPane chatScroll = new JScrollPane(history);
            add(chatScroll, BorderLayout.CENTER);
        } else {
            history.setText("");
            if (messagePanel != null) {
                remove(messagePanel);
                messagePanel = null;
            }
        }
        JTextField loginField = new JTextField(25) {
            final String HINT = "Введите логин";

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty()) {
                    g.drawString(HINT, 0, getHeight() / 2);
                }
            }
        };
        loginField.setFont(FONT);
        JPasswordField passwordField = new JPasswordField(25) {
            final String HINT = "Введите пароль";

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getPassword().length == 0) {
                    g.drawString(HINT, 0, getHeight() / 2);
                }
            }
        };
        passwordField.setFont(FONT);
        JButton loginButton = new JButton("Войти");
        loginButton.setFont(FONT);
        ActionListener sendMessage = e -> {
            TO_SERVER_OUT.println(ChatHelper.getLoginCommand(loginField.getText(),
                    String.copyValueOf(passwordField.getPassword())));
            if (!TO_SERVER_OUT.checkError()) {
                history.append(ChatHelper.addTimeToMyMessage(
                        "Отправлен запрос авторизации " + loginField.getText() + "\n"));
                loginField.setText("");
                passwordField.setText("");
                passwordField.grabFocus();
            } else {
                showMessage(ChatHelper.getAuthorizationNotSentMessage());
            }
        };
        loginButton.addActionListener(sendMessage);
        passwordField.addActionListener(sendMessage);
        loginPanel = new JPanel(new BorderLayout());
        loginPanel.add(loginField, BorderLayout.WEST);
        loginPanel.add(passwordField, BorderLayout.CENTER);
        loginPanel.add(loginButton, BorderLayout.EAST);
        add(loginPanel, BorderLayout.NORTH);
        loginField.grabFocus();
    }

    private void paintChat() {
        if (loginPanel != null) {
            remove(loginPanel);
            loginPanel = null;
        }
        history.setText("");
        JTextField messageField = new JTextField() {
            final String HINT = "Введите сообщение";

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty()) {
                    g.drawString(HINT, 0, getHeight() / 2);
                }
            }
        };
        messageField.setFont(FONT);
        JButton sendButton = new JButton("Отправить");
        sendButton.setFont(FONT);
        ActionListener sendMessage = e -> {
            String messageText = messageField.getText();
            TO_SERVER_OUT.println(messageText);
            if (!TO_SERVER_OUT.checkError()) {
                String[] parts = ChatHelper.getPartsIfPrivateMessageCommand(messageText);
                if (parts != null) {
                    String toClientNick = parts[0];
                    messageText = parts[1];
                    history.append(ChatHelper.addTimeToMyPrivateMessage(toClientNick, messageText + "\n"));
                } else {
                    history.append(ChatHelper.addTimeToMyMessage(messageText + "\n"));
                }
                messageField.setText("");
                messageField.grabFocus();
            } else {
                showMessage(ChatHelper.getMessageNotSentMessage());
            }
        };
        sendButton.addActionListener(sendMessage);
        messageField.addActionListener(sendMessage);
        messagePanel = new JPanel(new BorderLayout());
        messagePanel.add(messageField, BorderLayout.CENTER);
        messagePanel.add(sendButton, BorderLayout.EAST);
        add(messagePanel, BorderLayout.SOUTH);
        messageField.grabFocus();
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(null, message);
    }
}