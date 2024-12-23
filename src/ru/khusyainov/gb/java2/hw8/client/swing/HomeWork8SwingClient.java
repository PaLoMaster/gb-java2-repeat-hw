package ru.khusyainov.gb.java2.hw8.client.swing;

import ru.khusyainov.gb.java2.hw8.ChatHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class HomeWork8SwingClient extends JFrame {
    public static void main(String[] args) throws IOException {
        new HomeWork8SwingClient();
    }

    private Socket socket;
    private Scanner fromServerIn;
    private PrintWriter toServerOut;
    private Thread fromServerThread;
    private final Font FONT = new Font("Arial", Font.BOLD, 16);
    private JPanel chatPanel;
    private JTextArea history;
    private JPanel loginPanel;
    private JPanel messagePanel;
    private JScrollPane clientsListPane;
    private JList<String> clientsList;
    private String nick;

    public HomeWork8SwingClient() {
        setTitle("Чат");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                serverDisconnect();
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

    private void serverConnect() throws IOException {
        if (socket == null || !socket.isConnected()) {
            socket = new Socket(ChatHelper.SERVER_HOST, ChatHelper.SERVER_PORT);
            fromServerIn = new Scanner(socket.getInputStream());
            toServerOut = new PrintWriter(socket.getOutputStream());
            fromServerThread = new Thread(() -> {
                try {
                    String fromServerMessage;
                    while (!Thread.interrupted() && fromServerIn.hasNextLine()) {
                        fromServerMessage = fromServerIn.nextLine();
                        nick = ChatHelper.getNickIfAuthorizedStatus(fromServerMessage);
                        if (nick != null) {
                            setAuthorized(true);
                            ((MyListCellRenderer) (clientsList.getCellRenderer())).setNick(nick);
                            break;
                        }
                        history.append(fromServerMessage + "\n");
                    }
                    while (!Thread.interrupted() && fromServerIn.hasNextLine()) {
                        fromServerMessage = fromServerIn.nextLine();
                        String[] clientsList = ChatHelper.getClientsIfClientsList(fromServerMessage);
                        if (clientsList == null) {
                            history.append(fromServerMessage + "\n");
                        } else {
                            this.clientsList.setListData(clientsList);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                history.append(ChatHelper.getServerDisconnectedMessage());
                if (nick != null) {
                    setAuthorized(false);
                }
                serverDisconnect();
            });
            fromServerThread.setDaemon(true);
            fromServerThread.start();
        }
    }

    public void setAuthorized(boolean authorized) {
        if (authorized) {
            paintChat();
        } else {
            paintAuthorization();
            if (nick != null) {
                nick = null;
            }
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
            JScrollPane chatScroll = new JScrollPane(history);
            chatPanel = new JPanel(new BorderLayout());
            chatPanel.add(chatScroll, BorderLayout.CENTER);
        } else {
            history.setText("");
            if (messagePanel != null) {
                chatPanel.remove(messagePanel);
                messagePanel = null;
            }
            if (clientsListPane != null) {
                remove(clientsListPane);
                clientsListPane = null;
            }
        }
        JTextField loginField = new MyTextField(25, "Введите логин");
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
            if (loginField.getText().isEmpty() || passwordField.getPassword().length == 0) {
                showMessage(ChatHelper.getAuthorizationNotFullMessage());
                return;
            }
            try {
                serverConnect();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            toServerOut.println(ChatHelper.getLoginCommand(loginField.getText(),
                    String.copyValueOf(passwordField.getPassword())));
            if (!toServerOut.checkError()) {
                history.append(ChatHelper.addTimeToMyMessage(
                        "Отправлен запрос авторизации " + loginField.getText() + "\n"));
                loginField.setText("");
                passwordField.setText("");
                passwordField.grabFocus();
            } else {
                showMessage(ChatHelper.getAuthorizationNotSentMessage());
            }
        };
        loginField.addActionListener(sendMessage);
        passwordField.addActionListener(sendMessage);
        loginButton.addActionListener(sendMessage);
        loginPanel = new JPanel(new BorderLayout());
        loginPanel.add(loginField, BorderLayout.LINE_START);
        loginPanel.add(passwordField, BorderLayout.CENTER);
        loginPanel.add(loginButton, BorderLayout.LINE_END);
        chatPanel.add(loginPanel, BorderLayout.PAGE_START);
        add(chatPanel, BorderLayout.CENTER);
        loginField.grabFocus();
    }

    private void paintChat() {
        if (loginPanel != null) {
            chatPanel.remove(loginPanel);
            loginPanel = null;
        }
        history.setText("");
        clientsList = new JList<>();
        clientsList.setFixedCellWidth(200);
        clientsList.setFont(FONT);
        clientsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        clientsList.setCellRenderer(new MyListCellRenderer());
        clientsListPane = new JScrollPane(clientsList);
        add(clientsListPane, BorderLayout.LINE_END);
        JTextField messageField = new MyTextField(50, "Введите сообщение");
        messageField.setFont(FONT);
        clientsList.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    messageField.setText(ChatHelper.getPrivateMessageCommand(clientsList.getSelectedValue()));
                    messageField.grabFocus();
                    messageField.setSelectionEnd(messageField.getText().length());
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }
            @Override
            public void mouseReleased(MouseEvent e) {
            }
            @Override
            public void mouseEntered(MouseEvent e) {
            }
            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
        JButton sendButton = new JButton("Отправить");
        sendButton.setFont(FONT);
        ActionListener sendMessage = e -> {
            String toServerMessage = messageField.getText();
            toServerOut.println(toServerMessage);
            if (!toServerOut.checkError()) {
                history.append(ChatHelper.getMessageToLocalHistory(toServerMessage + "\n"));
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
        messagePanel.add(sendButton, BorderLayout.LINE_END);
        chatPanel.add(messagePanel, BorderLayout.PAGE_END);
        messageField.grabFocus();
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(null, message);
    }

    private void serverDisconnect() {
        if (fromServerThread != null) {
            fromServerThread.interrupt();
            fromServerThread = null;
        }
        if (toServerOut != null) {
            toServerOut.close();
            toServerOut = null;
        }
        if (fromServerIn != null) {
            fromServerIn.close();
            fromServerIn = null;
        }
        try {
            if (socket != null) {
                socket.close();
                socket = null;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    class MyTextField extends JTextField {
        String hint;

        public MyTextField(int columns, String hint) {
            super(columns);
            this.hint = hint;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (getText().isEmpty()) {
                g.drawString(hint, 0, getHeight() / 2);
            }
        }
    }

    class MyListCellRenderer extends JLabel implements ListCellRenderer<String> {
        String nick;

        @Override
        public Component getListCellRendererComponent(JList<? extends String> list, String value, int index, boolean isSelected, boolean cellHasFocus) {
            if (nick != null && nick.equals(value)) {
                setText(value + " (Я)");
            } else {
                setText(value);
            }
            return this;
        }

        public void setNick(String nick) {
            this.nick = nick;
        }
    }
}