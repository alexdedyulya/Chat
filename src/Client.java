import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Alex on 02.11.2017.
 */
public class Client {
    Socket socket;
    BufferedReader reader;
    PrintWriter writer;
    Thread thread;
    JTextArea incoming;
    JTextField outgoing;

    public static void main(String[] args) {
        Client client = new Client();
        client.go();
    }
    public void go(){
        JFrame frame = new JFrame("Chat client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel mainPanel = new JPanel();
        incoming = new JTextArea(15,50);
        incoming.setLineWrap(true);
        incoming.setWrapStyleWord(true);
        incoming.setEditable(false);
        JScrollPane qScroller = new JScrollPane(incoming);
        qScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        qScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        outgoing = new JTextField(20);
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(new SendButtonListener());
        mainPanel.add(incoming);
        mainPanel.add(outgoing);
        mainPanel.add(sendButton);

        setNetwork();

        thread = new Thread(new IncomingReader());
        thread.start();

        frame.getContentPane().add(BorderLayout.CENTER, mainPanel);
        frame.setSize(400,500);
        frame.setVisible(true);

    }
    private void setNetwork() {
        try {
            socket = new Socket("192.168.1.104", 5001);
            InputStreamReader streamReader = new InputStreamReader(socket.getInputStream());
            reader = new BufferedReader(streamReader);
            writer = new PrintWriter(socket.getOutputStream());
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
                    outgoing.setText(message + "fggf\n");
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
                writer.println(outgoing.getText());
                writer.flush();
            }
            catch (Exception e1)
            {
                e1.printStackTrace();
            }
            outgoing.setText("");
            outgoing.requestFocus();
        }
    }

    public synchronized void SendString(String msg)
    {
        writer.println(outgoing.getText());
        writer.flush();
    }
}
