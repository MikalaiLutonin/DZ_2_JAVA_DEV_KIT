import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class ClientGUI extends JFrame {
    private JTextField ipField, portField, loginField, messageField;
    private JPasswordField passwordField;
    private JTextArea chatArea;
    private JButton loginButton, sendButton;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public ClientGUI() {
        setTitle("Chat Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new GridLayout(5, 2));
        topPanel.add(new JLabel("IP address:"));
        ipField = new JTextField("127.0.0.1");
        topPanel.add(ipField);
        topPanel.add(new JLabel("Port:"));
        portField = new JTextField("1234");
        topPanel.add(portField);
        topPanel.add(new JLabel("Login:"));
        loginField = new JTextField();
        topPanel.add(loginField);
        topPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        topPanel.add(passwordField);
        loginButton = new JButton("LOGIN");
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
        topPanel.add(loginButton);
        add(topPanel, BorderLayout.NORTH);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        messageField = new JTextField();
        bottomPanel.add(messageField, BorderLayout.CENTER);
        sendButton = new JButton("SEND");
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                send();
            }
        });
        bottomPanel.add(sendButton, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void login() {
        try {
            String ip = ipField.getText();
            int port = Integer.parseInt(portField.getText());
            String login = loginField.getText();
            String password = new String(passwordField.getPassword());

            socket = new Socket(ip, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            out.println(login + ":" + password);

            Thread thread = new Thread(new Runnable() {
                public void run() {
                    try {
                        while (true) {
                            String message = in.readLine();
                            if (message == null) break;
                            chatArea.append(message + "\n");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();

            chatArea.setText("Connected to server.\n");
        } catch (IOException e) {
            e.printStackTrace();
            chatArea.setText("Connection failed.\n");
        }
    }

    private void send() {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            out.println(message);
            chatArea.append("Me: " + message + "\n");
            messageField.setText("");
        }
    }


}