import com.sun.org.apache.xpath.internal.SourceTree;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Alex on 02.11.2017.
 */
public class Server {
    ArrayList ClientConnection;


    public static void main(String[] args) {
        new Server().go();
        //new Client();
    }

    public void go(){
        ClientConnection = new ArrayList();
        try {
            ServerSocket serverSocket = new ServerSocket(5000);

            while(true)
            {
                Client client = new Client();
                client.setNetwork(serverSocket.accept());
                //PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
                ClientConnection.add(client);


            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void tellEveryone(String message){
        Iterator it = ClientConnection.iterator();
        while (it.hasNext()) {
            try {
                PrintWriter writer = (PrintWriter) it.next();
                writer.println(message);
                writer.flush();
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
