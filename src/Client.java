
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
    ConnectionClient connectionClient;
    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setNetwork(Socket socket1, ConnectionClient connectionClient) {
        try {
            this.connectionClient = connectionClient;
            this.socket = socket1;
            InputStreamReader streamReader = new InputStreamReader(socket.getInputStream());
            reader = new BufferedReader(streamReader);
            OutputStreamWriter streamWriter = new OutputStreamWriter(socket.getOutputStream());
            writer = new BufferedWriter(streamWriter);
            System.out.println("Network...");
            thread = new Thread(new IncomingReader());
            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class IncomingReader implements Runnable{
        @Override
        public void run() {
            String message;

            try {
                connectionClient.connection(Client.this);

                while (!thread.isInterrupted())
                {
                    message=reader.readLine();
                    connectionClient.receiveString(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                connectionClient.disconnection(Client.this);
            }

        }
    }


    public synchronized void SendString(String msg)
    {
        try {
            writer.write(msg+"\n");
            writer.flush(); //сбрасывает буферы
        } catch (IOException e) {
            e.printStackTrace();
            disconnect();
        }
    }

    @Override
    public String toString() {
        return "Клиент:" + socket.getInetAddress() + ":" + socket
                .getPort();
    }

    public synchronized void disconnect ()
    {
        thread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
