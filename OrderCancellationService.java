import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class OrderCancellationService {

    public static void main(String[] args) throws Exception {

        // server is listening on port 5050

        int port = 5055;

        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        ServerSocket ss = new ServerSocket(port);

        // running infinite loop for getting
        // client request

        System.out.println("Starting UserAuthentication service at port = " + port);

        boolean result = register("localhost", 5000, "localhost", port); // Register with the Registry, so the client know how to find!

        if (!result) {
            System.out.println("Register unsuccessfully!");
            ss.close();
            return;
        }

        int nClients = 0;

        while (true)
        {
            Socket s = null;
            // socket object to receive incoming client requests
            s = ss.accept();
            nClients++;
            System.out.println("A new client is connected : " + s + " client number: " + nClients);
            serve(s, nClients);

        }
    }

    private static void serve(Socket socket, int clientID) throws Exception {

        DataInputStream reader = new DataInputStream(socket.getInputStream());

        String msg = reader.readUTF();

        Gson gson = new Gson();

        User user = gson.fromJson(msg, User.class);

        //System.out.println("OrderID from client " + clientID + ": " + id);
        Class.forName("org.sqlite.JDBC");
        DataAccess adapter = new SQLiteDataAdapter();
        adapter.connect("jdbc:sqlite:store.db");
        User retrievedUser = adapter.authenticateUser(user);
        if (retrievedUser != null) {
            System.out.println("\'" + retrievedUser.userName + "\'" + " has been authenticated!");
        } else {
            System.out.println("The requested user does not appear in the database.");
        }
        String ans = gson.toJson(retrievedUser);
        DataOutputStream printer = new DataOutputStream(socket.getOutputStream());
        printer.writeUTF(ans);
        printer.flush();

        String orderCancellation = reader.readUTF();
        Order cancelledOrder = gson.fromJson(orderCancellation, Order.class);
        cancelledOrder = adapter.loadOrder(cancelledOrder.getOrderID());

        if (retrievedUser.userName.equals(cancelledOrder.getCustomerName())) {
            adapter.cancelOrder(cancelledOrder);
            orderCancellation = gson.toJson(cancelledOrder);
            printer.writeUTF(orderCancellation);
            printer.flush();
        } else {
            System.out.println("User does not have the privileges to cancel this order.");
        }



        printer.close();
        reader.close();
        socket.close();
    }
    /*
        Register this service to the Registry!
     */
    private static boolean register(String regHost, int regPort, String myHost, int myPort) throws IOException {

        ServiceInfoModel info = new ServiceInfoModel();
        info.serviceCode = ServiceInfoModel.ORDER_CANCELLATION_SERVICE;
        info.serviceHostAddress = myHost;
        info.serviceHostPort = myPort;

        Gson gson = new Gson();

        ServiceMessageModel req = new ServiceMessageModel();
        req.code = ServiceMessageModel.SERVICE_PUBLISH_REQUEST;
        req.data = gson.toJson(info);

        Socket socket = new Socket(regHost, regPort);

        DataOutputStream printer = new DataOutputStream(socket.getOutputStream());
        printer.writeUTF(gson.toJson(req));
        printer.flush();

        DataInputStream reader = new DataInputStream(socket.getInputStream());
        String msg = reader.readUTF();
        printer.close();
        reader.close();
        socket.close();


        System.out.println("Message from server: " + msg);
        ServiceMessageModel res = gson.fromJson(msg, ServiceMessageModel.class);

        if (res.code == ServiceMessageModel.SERVICE_PUBLISH_OK)
            return true;
        else
            return false;
    }

    private static void deregister() {

    }


}
