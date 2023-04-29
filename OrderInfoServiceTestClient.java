import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class OrderInfoServiceTestClient {

    public static void main(String[] args) throws IOException {

        // ask for service from Registry

        Socket socket = new Socket("localhost", 5000);

        ServiceMessageModel req = new ServiceMessageModel();
        req.code = ServiceMessageModel.SERVICE_DISCOVER_REQUEST;
        req.data = String.valueOf(ServiceInfoModel.ORDER_INFO_SERVICE);

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

        microservicePrinter.writeUTF("1");

        microservicePrinter.flush();

        DataInputStream microserviceReader = new DataInputStream(microserviceSocket.getInputStream());

        String micromsg = microserviceReader.readUTF();

        Order order = gson.fromJson(micromsg, Order.class);

        System.out.println("---------------------------");
        System.out.println("----- Order Retrieved -----");
        System.out.println(order.summary());
        System.out.println("---------------------------");

        microservicePrinter.close();
        microserviceReader.close();
        microserviceSocket.close();





    }

}
