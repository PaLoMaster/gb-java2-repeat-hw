package ru.khusyainov.gb.java2.hw4;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class HomeWork4 extends JFrame {
    public HomeWork4() {
        setTitle("Чат");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
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
        ActionListener sendMessage = e -> {
            chatTexts.append(messageField.getText() + "\n");
            messageField.setText("");
            messageField.grabFocus();
        };
        sendButton.setFont(font);
        sendButton.addActionListener(sendMessage);
        messageField.addActionListener(sendMessage);
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(messageField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
        setVisible(true);
        messageField.grabFocus();
    }

    public static void main(String[] args) {
        new HomeWork4();
    }
}