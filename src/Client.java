
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


    public void setNetwork(Socket socket1) {
        try {
            //socket = new Socket("192.168.1.104", 5001);
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

    public class IncomingReader extends Server implements Runnable{
        @Override
        public void run() {
            String message;
            try {
                while ((message=reader.readLine()) != null)
                {
                    System.out.println("read " + message);
                    tellEveryone(message);
      //              fieldInput.setText(message + "fggf\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
                disconnect();
            }
        }
    }


    public synchronized void SendString(String msg)
    {
        try {
            writer.write(msg);
            writer.flush(); //сбрасывает буферы
        } catch (IOException e) {
            e.printStackTrace();
            disconnect();
        }
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
