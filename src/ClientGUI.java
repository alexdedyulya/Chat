import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Alex on 16.11.2017.
 */
public class ClientGUI {
    JTextField fieldLogin = new JTextField(15);
    JButton enterButton = new JButton("Enter");
    JTextArea incoming = new JTextArea(27,30);
    JTextField fieldInput = new JTextField(30);
    JList list;
    JButton sendButton = new JButton("Send");
    JFrame loginFrame = new JFrame("Chat client Login");
    JFrame frame = new JFrame("Chat client");
    String[] clientList = {"alex", "gena"};
    DefaultListModel listModel = new DefaultListModel();


    public static void main(String[] args) {
        ClientGUI clientGui = new ClientGUI();
        clientGui.go();
    }
    public void go(){
        listModel.addElement("alex");
        listModel.addElement("gena");

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
        sendButton.addActionListener(new ClientGUI.SendButtonListener());

        c.gridx = 1;
        c.gridy = 0;
        c.ipadx = 30;
        c.insets = new Insets(5, 0, 5, 5);
        //c.ipady = 20;
        list = new JList(listModel);
        list.setLayoutOrientation(JList.VERTICAL);
        list.setVisibleRowCount(0);
        JScrollPane northScroll = new JScrollPane(list);
        northScroll.setPreferredSize(new Dimension(100, 450));

        gridBag.setConstraints(northScroll, c);
        frame.add(northScroll);

        loginFrame.setVisible(true);

    }

    public class SendButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
             //   writer.write(fieldInput.getText());
             //   writer.flush();
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
            //list.(fieldLogin.getText()+"\n");
            listModel.addElement(fieldLogin.getText());
            loginFrame.setVisible(false);

            frame.setVisible(true);
        }
    }
}
