/**
 * Created by Alex on 17.11.2017.
 */
public interface ConnectionClient {
    void connection(Client client);
    void disconnection(Client client);
    void receiveString(String message);

}
