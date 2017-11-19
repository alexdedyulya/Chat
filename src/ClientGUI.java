import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.*;
import java.util.ArrayList;

/**
 * Created by Alex on 16.11.2017.
 */
public class ClientGUI implements ConnectionClient {
    private String connectionUrl = "jdbc:mysql://localhost:3306/chat";
    private String name = "root";
    private String password = "root";
    private Connection connection;
    private PreparedStatement prStatement;
    private InetAddress inetAddress;
    String clients;

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
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
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

        frame.addComponentListener(new FrameFocus());
        frame.addWindowListener(new WindowClose());

    }



    public class FrameFocus extends ComponentAdapter {
            public void componentHidden(ComponentEvent e) {}
            public void componentShown(ComponentEvent e) {
                System.out.println("FrameFocus");
                listOfClient.setText("");
                clients = "";
                try {
                    prStatement = connection.prepareStatement("SELECT id FROM clients WHERE login = ?");
                    prStatement.setString(1, fieldLogin.getText());
                    ResultSet existClient = prStatement.executeQuery();
                    if (!existClient.next()) {
                        prStatement = connection.prepareStatement("INSERT INTO clients (login, status) VALUE (?, ?)");
                        prStatement.setString(1, fieldLogin.getText());
                        prStatement.setBoolean(2, true);
                        prStatement.execute();
                        ResultSet resultSet = prStatement.executeQuery("SELECT * FROM clients");
                        parseClients(resultSet);
                    }
                    else
                    {
                        prStatement = connection.prepareStatement("UPDATE clients SET status = ? where login = ?");
                        prStatement.setBoolean(1, true);
                        prStatement.setString(2, fieldLogin.getText());
                        prStatement.executeUpdate();
                        ResultSet resultSet = prStatement.executeQuery("SELECT * FROM clients");
                        parseClients(resultSet);
                    }
                    System.out.println("БД " + clients);
                    //listOfClient.setText(clients);
                    //prStatement.close();
                    //connection.close();
                } catch (SQLException m) {
                    System.out.println("Исключение подключ");
                }
                client.SendString("ClearListOf@1234");
                client.SendString(clients);
            }
    }

    public void parseClients(ResultSet resultSet){
        try {
            while (resultSet.next()) {
                clients += resultSet.getString("login") + "("
                        + (resultSet.getBoolean("status") ==
                        true ? "онлайн" : "офлайн")
                        + ")" + "\n";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public class WindowClose implements WindowListener{

        @Override
        public void windowOpened(WindowEvent e) {}

        @Override
        public void windowClosing(WindowEvent e) {
            disconnection(client);
            client.SendString("ClearListOf@1234");
            client.SendString(clients);
            System.exit(0);
        }

        @Override
        public void windowClosed(WindowEvent e) {

        }

        @Override
        public void windowIconified(WindowEvent e) {}

        @Override
        public void windowDeiconified(WindowEvent e) {}

        @Override
        public void windowActivated(WindowEvent e) {}

        @Override
        public void windowDeactivated(WindowEvent e) {}
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

            try {
                inetAddress = InetAddress.getLocalHost();
                System.out.println("Local IP:" + inetAddress.getHostAddress());
            } catch (UnknownHostException a) {
                a.printStackTrace();
            }
            client = new Client();
            try {
                socket = new Socket(inetAddress.getHostAddress(), 5000);
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            client.setNetwork(socket, ClientGUI.this);


            loginFrame.setVisible(false);
            frame.setVisible(true);
        }
    }

    @Override
    public void connection(Client client) {
        System.out.println("Подкл");
        //client.setName(fieldLogin.getText());
        //tellMessage("Подключился");
        System.out.println(client.getName() + client);

    }

    @Override
    public void disconnection(Client client) {
        clients = "";
        System.out.println("Откл");
        //tellMessage("Отключился");
        try {
            prStatement = connection.prepareStatement("UPDATE clients SET status = ? where login = ?");
            prStatement.setBoolean(1, false);
            prStatement.setString(2, fieldLogin.getText());
            prStatement.executeUpdate();
            ResultSet resultSet = prStatement.executeQuery("SELECT * FROM clients");
            parseClients(resultSet);
            System.out.println("БД " + clients);
            listOfClient.setText(clients);
         //   prStatement.close();
         //   connection.close();

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
        if (message.contains("(онлайн)") || message.contains("(офлайн)")) {
            listOfClient.append(message+ "\n");
            listOfClient.setCaretPosition(listOfClient.getDocument().getLength());
        }
        else if(message.equals("ClearListOf@1234")) {
            listOfClient.setText("");
        }
        else {
            incoming.append(message + "\n");
            incoming.setCaretPosition(incoming.getDocument().getLength());
        }
    }
}

