import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

/**
 * Created by Alex on 02.11.2017.
 */
public class Client {
    Socket socket;
    BufferedReader reader;
    BufferedWriter writer;
    Thread thread;
    JTextField fieldLogin = new JTextField(15);
    JButton enterButton = new JButton("Enter");
    JTextArea incoming = new JTextArea(27,30);
    JTextField fieldInput = new JTextField(30);
    JTextArea list = new JTextArea(27,10);
    JButton sendButton = new JButton("Send");
    JFrame loginFrame = new JFrame("Chat client Login");
    JFrame frame = new JFrame("Chat client");

    public static void main(String[] args) {
        Client client = new Client();
        client.go();
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
        enterButton.addActionListener(new EnterButtonListener());



        frame.setBounds(200,100,500, 600);
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
        gridBag.setConstraints(incoming, c);
        frame.add(incoming);


        c.gridy = GridBagConstraints.RELATIVE;
        gridBag.setConstraints(fieldInput, c);
        frame.add(fieldInput);

        c.gridx = GridBagConstraints.RELATIVE;
        c.gridy = 1;
        gridBag.setConstraints(sendButton, c);
        frame.add(sendButton);
        sendButton.addActionListener(new SendButtonListener());

        c.gridx = 1;
        c.gridy = 0;
        gridBag.setConstraints(list, c);
        frame.add(list);

     /*   Container mainContainer = frame.getContentPane();
        mainContainer.setLayout(new GridLayout(3,2,2,2));
        incoming = new JTextArea(15,50);
        //mainContainer.add(incoming);
        //incoming.setLineWrap(true);
        //incoming.setWrapStyleWord(true);
        //incoming.setEditable(false);
        //JScrollPane qScroller = new JScrollPane(incoming);
        //qScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        //qScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        outgoing = new JTextField(20);
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(new SendButtonListener());
        mainContainer.add(incoming);
        mainContainer.add(outgoing);
        mainContainer.add(sendButton);

       // setNetwork();

       // thread = new Thread(new IncomingReader());
       // thread.start();

        //frame.getContentPane().add(BorderLayout.CENTER, Panel);
       // frame.setSize(400,500);*/
        loginFrame.setVisible(true);

    }
    private void setNetwork() {
        try {
            socket = new Socket("192.168.1.104", 5001);
            InputStreamReader streamReader = new InputStreamReader(socket.getInputStream());
            reader = new BufferedReader(streamReader);
            OutputStreamWriter streamWriter = new OutputStreamWriter(socket.getOutputStream());
            writer = new BufferedWriter(streamWriter);
            System.out.println("Network...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class IncomingReader implements Runnable{
        @Override
        public void run() {
            String message;
            try {
                while ((message=reader.readLine()) != null)
                {
                    System.out.println("read " + message);
                    fieldInput.setText(message + "fggf\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class SendButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                writer.write(fieldInput.getText());
                writer.flush();
            }
            catch (Exception e1)
            {
                e1.printStackTrace();
            }
            incoming.append(fieldInput.getText()+"\n");
            fieldInput.setText("");
            fieldInput.requestFocus();
        }
    }

    public class EnterButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            list.append(fieldLogin.getText()+"\n");
            loginFrame.setVisible(false);
            frame.setVisible(true);
        }
    }

    public synchronized void SendString(String msg)
    {
        try {
            writer.write(fieldInput.getText());
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
