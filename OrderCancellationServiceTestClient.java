import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class OrderCancellationServiceTestClient {

    public static void main(String[] args) throws IOException {

        // ask for service from Registry

        Socket socket = new Socket("localhost", 5000);

        ServiceMessageModel req = new ServiceMessageModel();
        req.code = ServiceMessageModel.SERVICE_DISCOVER_REQUEST;
        req.data = String.valueOf(ServiceInfoModel.ORDER_CANCELLATION_SERVICE);

        Gson gson = new Gson();

        DataOutputStream printer = new DataOutputStream(socket.getOutputStream());
        printer.writeUTF(gson.toJson(req));
        printer.flush();


        DataInputStream reader = new DataInputStream(socket.getInputStream());
        String msg = reader.readUTF();

        System.out.println("Message from server: " + msg);

        printer.close();
        reader.close();
        socket.close();

        ServiceMessageModel res = gson.fromJson(msg, ServiceMessageModel.class);

        if (res.code == ServiceMessageModel.SERVICE_DISCOVER_OK) {
            System.out.println(res.data);
        }
        else {
            System.out.println("Service not found");
            return;
        }

        ServiceInfoModel info = gson.fromJson(res.data, ServiceInfoModel.class);


        System.out.println("\n---------------------------");
        System.out.println("- Microservice Discovered -");
        System.out.println(info.toString());
        System.out.println("---------------------------\n");


        Socket microserviceSocket = new Socket(info.serviceHostAddress, info.serviceHostPort);

        DataOutputStream microservicePrinter = new DataOutputStream(microserviceSocket.getOutputStream());

        User user = new User();

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Username: ");
        user.userName = scanner.nextLine().trim();
        System.out.print("Enter Password: ");
        user.password = scanner.nextLine().trim();

        String userObj = gson.toJson(user);


        microservicePrinter.writeUTF(userObj);

        microservicePrinter.flush();

        DataInputStream microserviceReader = new DataInputStream(microserviceSocket.getInputStream());

        String micromsg = microserviceReader.readUTF();

        User retrievedUser = gson.fromJson(micromsg, User.class);

        if (retrievedUser != null) {

            System.out.println("\n---------------------------");
            System.out.println("---- User Authenticated ---");
            System.out.println(retrievedUser.toString());
            System.out.println("---------------------------");

            System.out.print("\nEnter OrderID to request cancellation: ");
            int cancelOrderID = Integer.parseInt(scanner.nextLine().trim());
            Order order =  new Order();
            order.setOrderID(cancelOrderID);
            String orderString = gson.toJson(order);
            microservicePrinter.writeUTF(orderString);
            microservicePrinter.flush();

            String cancelledOrderString = microserviceReader.readUTF();
            Order cancelledOrder = gson.fromJson(cancelledOrderString, Order.class);
            System.out.println("\nOrderID #" + cancelledOrder.getOrderID() + " cancelled by " + retrievedUser.userName + ".");


        } else {
            System.out.println("\nThe username/password you entered cannot be found!");
        }

        microservicePrinter.close();
        microserviceReader.close();
        microserviceSocket.close();





    }



}
