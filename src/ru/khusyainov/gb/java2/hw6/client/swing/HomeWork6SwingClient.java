package ru.khusyainov.gb.java2.hw6.client.swing;

import ru.khusyainov.gb.java2.hw6.ChatHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class HomeWork6SwingClient extends JFrame {
    public HomeWork6SwingClient() throws IOException {
        Socket socket = new Socket(ChatHelper.SERVER_HOST, ChatHelper.SERVER_PORT);
        Scanner fromServerIn = new Scanner(socket.getInputStream());
        PrintWriter toServerOut = new PrintWriter(socket.getOutputStream());
        setTitle("Чат");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                toServerOut.println(ChatHelper.CLIENT_END_STRING);
                toServerOut.close();
                fromServerIn.close();
                try {
                    socket.close();
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
        Font font = new Font("Arial", Font.BOLD, 16);
        JTextArea chatTexts = new JTextArea(10, 50);
        chatTexts.setLineWrap(true);
        chatTexts.setEditable(false);
        chatTexts.setFont(font);
        Thread fromServerThread = new Thread(() -> {
            try {
                while (!Thread.interrupted() && fromServerIn.hasNextLine()) {
                    chatTexts.append(ChatHelper.addTimeToServerMessage(fromServerIn.nextLine()) + "\n");
                }
                chatTexts.append(ChatHelper.addTime("Соединение с сервером потеряно/закрыто."));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        fromServerThread.setDaemon(true);
        fromServerThread.start();
        JScrollPane chatScroll = new JScrollPane(chatTexts);
        add(chatScroll, BorderLayout.CENTER);
        JTextField messageField = new JTextField() {
            final String hint = "Введите сообщение";

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty()) {
                    g.drawString(hint, 0, getHeight() / 2);
                }
            }
        };
        messageField.setFont(font);
        JButton sendButton = new JButton("Отправить");
        sendButton.setFont(font);
        ActionListener sendMessage = e -> {
            toServerOut.println(messageField.getText());
            if (!toServerOut.checkError()) {
                chatTexts.append(ChatHelper.addTimeToMyMessage(messageField.getText()) + "\n");
                messageField.setText("");
                messageField.grabFocus();
            } else {
                JOptionPane.showMessageDialog(null, "Сообщение не отправлено!");
            }
        };
        sendButton.addActionListener(sendMessage);
        messageField.addActionListener(sendMessage);
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(messageField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
        setVisible(true);
        messageField.grabFocus();
    }

    public static void main(String[] args) throws IOException {
        new HomeWork6SwingClient();
    }
}