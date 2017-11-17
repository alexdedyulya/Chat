import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

/**
 * Created by Alex on 02.11.2017.
 */
public class Server implements ConnectionClient {
    static ArrayList<Client> clientConnection;


    public static void main(String[] args) {
        new Server().go();
    }

    public void go(){
        clientConnection = new ArrayList<Client>();
        try {
            ServerSocket serverSocket = new ServerSocket(5000);

            while(true)
            {
                Client client = new Client();
                client.setNetwork(serverSocket.accept(), this);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public synchronized void connection(Client client)
    {
        clientConnection.add(client);
        tellEveryone("Соединение установлено с " + client);
    }

    @Override
    public synchronized void disconnection(Client client)
    {
        clientConnection.remove(client);
        //client.disconnect();
        tellEveryone("Соединение разорванно с " + client);
    }

    @Override
    public synchronized void receiveString(String message) {
        tellEveryone(message);
    }

    public void tellEveryone(String message){
        System.out.println(message);
        for (int i = 0; i < clientConnection.size(); i++) {
            clientConnection.get(i).SendString(message);
        }
    }
}
