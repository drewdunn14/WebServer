import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ProductQuantityUpdateServiceTestClient {

    public static void main(String[] args) throws IOException {

        // ask for service from Registry

        Socket socket = new Socket("localhost", 5000);

        ServiceMessageModel req = new ServiceMessageModel();
        req.code = ServiceMessageModel.SERVICE_DISCOVER_REQUEST;
        req.data = String.valueOf(ServiceInfoModel.PRODUCT_QUANTITY_UPDATE_SERVICE);

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

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter ProductID: ");
        String enteredID = scanner.nextLine().trim();

        microservicePrinter.writeUTF(enteredID);

        microservicePrinter.flush();

        DataInputStream microserviceReader = new DataInputStream(microserviceSocket.getInputStream());

        String micromsg = microserviceReader.readUTF();

        ProductModel productModel = gson.fromJson(micromsg, ProductModel.class);

        if (productModel != null) {

            System.out.println("\n---------------------------");
            System.out.println("---- Product Retrieved ----");
            System.out.println(productModel.toString());
            System.out.println("---------------------------\n");

            System.out.println("  Current Quantity: " + productModel.quantity);
            System.out.print("Enter New Quantity: ");
            productModel.quantity = Double.parseDouble(scanner.nextLine().trim());

            String modelString = gson.toJson(productModel);

            microservicePrinter.writeUTF(modelString);
            microservicePrinter.flush();

            micromsg = microserviceReader.readUTF();

            ProductModel updatedModel = gson.fromJson(micromsg, ProductModel.class);

            System.out.println("\nNew quantity for " + updatedModel.name + " entered into database: " + updatedModel.quantity + ".");


        } else {
            System.out.println("\nThe productID you entered could not be found!");
        }

        microservicePrinter.close();
        microserviceReader.close();
        microserviceSocket.close();


    }
}
