import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class ServerWindow extends JFrame {
    private JTextArea logArea;
    private JButton startButton, stopButton;
    private ServerSocket serverSocket;
    private boolean running;

    public ServerWindow() {
        setTitle("Chat Server");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel bottomPanel = new JPanel(new GridLayout(1, 2));
        startButton = new JButton("Start");
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                start();
            }
        });
        bottomPanel.add(startButton);
        stopButton = new JButton("Stop");
        stopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                stop();
            }
        });
        bottomPanel.add(stopButton);
        add(bottomPanel, BorderLayout.SOUTH);

        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void start() {
        if (!running) {
            try {
                int port = 1234;
                serverSocket = new ServerSocket(port);
                running = true;
                logArea.setText("Server started.\n");

                Thread thread = new Thread(new Runnable() {
                    public void run() {
                        while (running) {
                            try {
                                Socket socket = serverSocket.accept();
                                ClientHandler clientHandler = new ClientHandler(socket);
                                clientHandler.start();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                thread.start();
            } catch (IOException e) {
                e.printStackTrace();
                logArea.setText("Server failed to start.\n");
            }
        } else {
            logArea.setText("Server is already running.\n");
        }
    }

    private void stop() {
        if (running) {
            try {
                serverSocket.close();
                running = false;
                logArea.setText("Server stopped.\n");
            } catch (IOException e) {
                e.printStackTrace();
                logArea.setText("Server failed to stop.\n");
            }
        } else {
            logArea.setText("Server was stopped.\n");
        }
    }

    private class ClientHandler extends Thread {
        private Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                String login = in.readLine();
                logArea.append(login + " connected.\n");

                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                while (true) {
                    String message = in.readLine();
                    if (message == null)
                        break;
                    logArea.append(login + ": " + message + "\n");

                    out.println(login + ": " + message);
                }

                logArea.append(login + " disconnected.\n");
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}