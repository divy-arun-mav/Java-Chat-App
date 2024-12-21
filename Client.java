import java.net.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;

public class Client extends JFrame {

    Socket socket;

    BufferedReader br;
    PrintWriter out;

    //GUI Components
    private JLabel heading = new JLabel("Client");
    private JTextArea messageArea = new JTextArea();
    private JTextField messageInput = new JTextField();
    private Font font = new Font("Roboto", Font.PLAIN, 20);

    public Client() {
        try {

            System.out.println("Sending request to server...");
            socket = new Socket("127.0.0.1", 7777);
            System.out.println("Connection successfull");

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());

            createGUI();
            handelEvents();

            startReading();
            // startWriting();

        } catch (Exception e) {
            // e.printStackTrace();
            System.out.println("Connection Closed");
        }
    }

    private void handelEvents() {
        messageInput.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
                
            }

            @Override
            public void keyPressed(KeyEvent e) {
                
            }

            @Override
            public void keyReleased(KeyEvent e) {
                // System.out.println("Key released" + e.getKeyCode());
                if (e.getKeyCode() == 10) {
                    // System.out.println("you entered enter button");
                    String contentToSend = messageInput.getText();
                    messageArea.append("Me: " + contentToSend + "\n");
                    out.println(contentToSend);
                    out.flush();
                    messageInput.setText("");
                    messageInput.requestFocus();
                    if (contentToSend.equals("exit")) {
                        try {
                            socket.close();
                            messageInput.setEnabled(false);
                        } catch (IOException e1) {
                            messageArea.append("Connection was closed");
                        }
                    }
                }
            }
            
        });
    }


    private void createGUI() {
        this.setTitle("Client");
        this.setSize(600, 600);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        ImageIcon imageIcon = new ImageIcon("clogo.png");
        Image image = imageIcon.getImage();
        Image newImage = image.getScaledInstance(80, 80, java.awt.Image.SCALE_SMOOTH);

        heading.setIcon(new ImageIcon(newImage));
        heading.setHorizontalTextPosition(SwingConstants.CENTER);
        heading.setVerticalTextPosition(SwingConstants.BOTTOM);
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));


        //components program
        heading.setFont(font);
        messageArea.setFont(font);
        messageInput.setFont(font);
        messageArea.setEditable(false);

        //frame layout
        this.setLayout(new BorderLayout());

        //adding components
        this.add(heading, BorderLayout.NORTH);
        JScrollPane jScrollPane = new JScrollPane(messageArea);
        this.add(jScrollPane, BorderLayout.CENTER);
        this.add(messageInput,BorderLayout.SOUTH);


        this.setVisible(true); 
    }


    public void startReading() {
        // data ko read karke deta rahega

        Runnable r1 = () -> {
            System.out.println("Reader started");

            try {

                while (!socket.isClosed()) {
                    String msg = br.readLine();
                    if (msg.equals("exit")) {
                        System.out.println("Server Terminated the chat");
                        JOptionPane.showMessageDialog(this, "Server Terminated the chat");
                        messageInput.setEnabled(false);
                        socket.close();
                        break;
                    }
                    // System.out.println("Server :" + msg);
                    messageArea.append("Server :" + msg + "\n");
                }
            } catch (Exception e) {
                // e.printStackTrace();
                System.out.println("Connection Closed");
            }
        };

        new Thread(r1).start();
    }

    public void startWriting() {
        // data user ko bhejega client tak

        Runnable r2 = () -> {
            try {
                while (!socket.isClosed()) {

                    BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
                    String content = br1.readLine();

                    out.println(content);
                    out.flush();

                    if (content.equals("exit")) {
                        socket.close();
                        break;
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        new Thread(r2).start();
    }

    public static void main(String[] args) {
        System.out.println("This is a java client server");
        new Client();
    }
}
