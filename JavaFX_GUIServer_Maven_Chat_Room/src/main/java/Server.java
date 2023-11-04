import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

public class Server {
    int currentCount = 1;
    ArrayList<ClientThread> clients = new ArrayList<>();
    TheServer server;
    private Consumer<Serializable> callback;
    ArrayList<Integer> activeClients = new ArrayList<>(60);
    boolean newClient = false;
    String[] vals;
    Server(Consumer<Serializable> call){
        callback = call;
        server = new TheServer();
        server.start();
    }

    public class TheServer extends Thread{
        public void run() {
            try(ServerSocket mysocket = new ServerSocket(5555);){
                callback.accept("Server is waiting for a client!");

                while(true) {
                    ClientThread c = new ClientThread(mysocket.accept(), currentCount);
                    callback.accept("client has connected to server: " + "client #" + currentCount);
                    clients.add(c);
                    activeClients.add(currentCount);
                    newClient = true;
                    c.start();
                    currentCount++;
                }
            }//end of try
            catch(Exception e) {
                callback.accept("Server socket did not launch");
            }
        }//end of while
    }

    class ClientThread extends Thread{
        Socket connection;
        int count;
        ObjectInputStream in;
        ObjectOutputStream out;
        ClientThread(Socket s, int count){
            this.connection = s;
            this.count = count;
        }

        public void updateClients(String message, int count, ArrayList<Integer> activeClients,
                                  ArrayList<Integer> currentClients, String[] vals) {
            activeClients = currentClients;
            MoreInfo clientInformation = new MoreInfo();
            if (newClient == true) {
                for(int i = 0; i < activeClients.size(); ++i) {
                    clientInformation.ActiveClients.add(activeClients.get(i));
                }
                clientInformation.clientNum = count;
                clientInformation.isNewClient = true;
            }
            for(int i = 0; i < clients.size(); ++i) {
                ClientThread t = clients.get(i);
                synchronized(t) {
                    try {
                        MoreInfo newInformation;
                        if(vals == null) {
                            newInformation = clientInformation;
                            newInformation.message = message;
                            t.out.writeObject(newInformation);
                        }
                        if (VerifyThreads(t.count, vals) == true) {
                            newInformation = clientInformation;
                            newInformation.message = message;
                            t.out.writeObject(newInformation);
                        }
                    }
                    catch(Exception e) {}
                }

            }
        }

        public void run(){
            try {
                in = new ObjectInputStream(connection.getInputStream());
                out = new ObjectOutputStream(connection.getOutputStream());
                connection.setTcpNoDelay(true);
            }
            catch(Exception e) {
                System.out.println("Streams not open");
            }
            if (newClient == true) {
                updateClients("new client on server: client #" + count, count,
                        activeClients, activeClients, vals);
                newClient = false;
            }

            while(true) {
                try {
                    MoreInfo data = (MoreInfo)in.readObject();
                    data.isNewClient = false;
                    vals = data.SendingTo;
                    updateClients("client #"+count+" said: "+data.message, count,
                            data.ActiveClients, activeClients, vals);
                    callback.accept("client: " + count + " sent: " + data.message);
                }
                catch(Exception e) {
                    callback.accept("Error! Something wrong with the socket from client: " + count + "....closing down!");

                    callback.accept("Error! Something wrong with the socket from client: " + count + "....closing down!");

                    for (int i = 0; i < activeClients.size(); i++) {
                        if(activeClients.get(i) == count) {
                            activeClients.remove(i);
                        }
                    }

                    newClient = true;
                    updateClients("Client #"+count+" has left the server!", count, activeClients,
                            activeClients, vals);
                    clients.remove(this);
                    newClient = false;

                    break;

                }
            }
        }//end of run


    }//end of client thread

    Boolean VerifyThreads(int count, String[] vals)
    {
        for(int i = 0; i < vals.length; ++i)
        {
            int temp = Integer.parseInt(vals[i]);
            if(temp == count)
                return true;
        }

        return false;
    }
}





