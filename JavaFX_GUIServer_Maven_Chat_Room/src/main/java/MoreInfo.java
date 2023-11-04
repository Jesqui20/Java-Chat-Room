import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class MoreInfo implements Serializable
// Stores Data sent by the clients.
{
    ArrayList<Integer> ActiveClients;
    boolean isNewClient;
    String message;
    int clientNum;
    String[] SendingTo;
    MoreInfo()
    {
        ActiveClients = new ArrayList<Integer>(50);
        isNewClient = false;
        message = "";
        clientNum = 0;
    }
}

