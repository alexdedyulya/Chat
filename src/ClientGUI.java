import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;

/**
 * Created by Alex on 16.11.2017.
 */
public class ClientGUI implements ConnectionClient {
    private String connectionUrl = "jdbc:mysql://localhost:3306/chat";
    private String name = "root";
    private String password = "root";
    Connection connection;
    PreparedStatement prStatement;

    public void connect(){

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            connection = DriverManager.getConnection(connectionUrl, name,password);
            System.out.println("db connect");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    static JTextField fieldLogin = new JTextField(15);
    JButton enterButton = new JButton("Enter");
    JTextArea incoming = new JTextArea(27,30);
    JTextField fieldInput = new JTextField(30);
    JTextArea listOfClient = new JTextArea(27,10);
    JButton sendButton = new JButton("Send");
    JFrame loginFrame = new JFrame("Chat client Login");
    JFrame frame = new JFrame("Chat client");
    //static DefaultListModel listModel = new DefaultListModel();
    ArrayList<String> arrayOfClient = new ArrayList<>();



    Socket socket;
    Client client;

    public static void main(String[] args) {
        ClientGUI clientGui = new ClientGUI();
        clientGui.connect();
        clientGui.go();
    }

    public void go(){

        loginFrame.setBounds(200,100,300, 300);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        GridBagLayout gridBagLogin = new GridBagLayout();
        loginFrame.setLayout(gridBagLogin);

        GridBagConstraints gbcLogin =  new GridBagConstraints();
        gbcLogin.anchor = GridBagConstraints.NORTH;
        gbcLogin.fill   = GridBagConstraints.NONE;
        gbcLogin.gridheight = 1;
        gbcLogin.gridwidth  = GridBagConstraints.REMAINDER;
        gbcLogin.gridx = GridBagConstraints.RELATIVE;
        gbcLogin.gridy = GridBagConstraints.RELATIVE;
        gbcLogin.insets = new Insets(40, 0, 5, 0);
        gbcLogin.ipadx = 0;
        gbcLogin.ipady = 0;
        gbcLogin.weightx = 0.0;
        gbcLogin.weighty = 0.0;

        gridBagLogin.setConstraints(fieldLogin, gbcLogin);
        loginFrame.add(fieldLogin);

        gridBagLogin.setConstraints(enterButton, gbcLogin);
        loginFrame.add(enterButton);
        enterButton.addActionListener(new ClientGUI.EnterButtonListener());



        frame.setBounds(200,100,550, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        GridBagLayout gridBag = new GridBagLayout();
        frame.setLayout(gridBag);

        GridBagConstraints c =  new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill   = GridBagConstraints.NONE;
        c.gridheight = GridBagConstraints.RELATIVE;
        c.gridwidth  = GridBagConstraints.RELATIVE;
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(5, 5, 5, 5);
        c.ipadx = 0;
        c.ipady = 0;
        c.weightx = 0.5;
        c.weighty = 0.5;

        incoming.setEditable(false);
        incoming.setLineWrap(true);
        JScrollPane incomingScroll = new JScrollPane(incoming);
        gridBag.setConstraints(incomingScroll, c);
        frame.add(incomingScroll);


        c.gridy = GridBagConstraints.RELATIVE;
        gridBag.setConstraints(fieldInput, c);
        frame.add(fieldInput);

        c.gridx = GridBagConstraints.RELATIVE;
        c.gridy = 1;
        gridBag.setConstraints(sendButton, c);
        frame.add(sendButton);
        sendButton.addActionListener(new ClientGUI.SendButtonListener());

        c.gridx = 1;
        c.gridy = 0;
        c.ipadx = 30;
        c.insets = new Insets(5, 0, 5, 5);
        //c.ipady = 20;

        JScrollPane listScroll = new JScrollPane(listOfClient);
        listOfClient.setEditable(false);
        listOfClient.setLineWrap(true);
        gridBag.setConstraints(listScroll, c);
        frame.add(listScroll);

        loginFrame.setVisible(true);

    }

    public class SendButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {

            if (fieldInput.getText().equals("")) {
                return;
            }
            client.SendString(fieldLogin.getText() + ": " + fieldInput.getText());
            fieldInput.setText("");
            fieldInput.requestFocus();
        }
    }

    public class EnterButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            if (fieldLogin.getText().equals("")) {
                return;
            }

            loginFrame.setVisible(false);
            frame.setVisible(true);

            client = new Client();
            try {
                socket = new Socket("192.168.1.102", 5000);
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            client.setNetwork(socket, ClientGUI.this);

        }
    }

    @Override
    public void connection(Client client) {
        System.out.println("Подкл");
        client.setName(fieldLogin.getText());
        tellMessage("Подключился");
        System.out.println(client.getName() + client);
        String clients = "";
        try {
            prStatement = connection.prepareStatement("INSERT INTO clients (login, status) VALUE (?, ?)");
            prStatement.setString(1 ,fieldLogin.getText());
            prStatement.setBoolean(2, true);
            prStatement.execute();
            ResultSet resultSet = prStatement.executeQuery("SELECT * FROM clients");
            while (resultSet.next())
            {
                clients += resultSet.getString("login") + "("
                        + (resultSet.getBoolean("status")
                        == true ? "онлайн" : "офлайн")
                        +")" + "\n";
            }
            System.out.println("БД " + clients);
            listOfClient.setText(clients);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        disconnection(client);

    }

    @Override
    public void disconnection(Client client) {
        String clients = "";
        System.out.println("Откл");
        tellMessage("Отключился");
        try {
            prStatement = connection.prepareStatement("UPDATE clients SET status = ? where login = ?");
            prStatement.setBoolean(1, false);
            prStatement.setString(2, fieldLogin.getText());
            prStatement.executeUpdate();
            ResultSet resultSet = prStatement.executeQuery("SELECT * FROM clients");
            while (resultSet.next())
            {
                clients += resultSet.getString("login") + "("
                        + (resultSet.getBoolean("status")
                        == true ? "онлайн" : "офлайн")
                        +")" + "\n";
            }
            System.out.println("БД " + clients);
            listOfClient.setText(clients);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void receiveString(String message) {
        System.out.println(message);
        tellMessage(message);
    }



    private synchronized void tellMessage(String message)
    {
    /*    if (message.contains(socket.getInetAddress().toString()) && !message.contains("Соединение")) {

            listOfClient.append(message+ "\n");
            listOfClient.setCaretPosition(listOfClient.getDocument().getLength());
        }
        else {*/
            incoming.append(message + "\n");
            incoming.setCaretPosition(incoming.getDocument().getLength());
       // }
    }
}

