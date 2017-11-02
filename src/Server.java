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
    ArrayList ClientOutputStream;
    public class ClientHandler implements Runnable{
        BufferedReader reader;
        Socket socket;
        public ClientHandler(Socket clientSocket)
        {
            try {
                socket = clientSocket;
                InputStreamReader isReader = new InputStreamReader(socket.getInputStream());
                reader = new BufferedReader(isReader);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            String message;

            try {
                while ((message = reader.readLine()) != null)
                {
                    System.out.println("read "+message);
                    tellEveryone(message);
                }
            } catch (IOException e) {
                e.printStackTrace();;
            }

        }
    }

    public static void main(String[] args) {
        new Server().go();
        //new Client();
    }

    public void go(){
        ClientOutputStream = new ArrayList();
        try {
            ServerSocket serverSocket = new ServerSocket(5001);

            while(true)
            {
                Socket clientSocket = serverSocket.accept();
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
                ClientOutputStream.add(writer);

                Thread t = new Thread(new ClientHandler(clientSocket));
                t.start();
                System.out.println("got a connection");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void tellEveryone(String message){
        Iterator it = ClientOutputStream.iterator();
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
