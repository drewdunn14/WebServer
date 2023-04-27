import com.hp.gagawa.java.elements.*;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.util.List;

public class WebServer {

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8500), 0);
        HttpContext root = server.createContext("/");
        root.setHandler(WebServer::handleRequest);

        HttpContext order = server.createContext("/orders");
        order.setHandler(WebServer::handleRequestOneOrder);

        HttpContext allOrders = server.createContext("/orders/all");
        allOrders.setHandler(WebServer::handleRequestAllOrders);

        HttpContext product = server.createContext("/products");
        product.setHandler(WebServer::handleRequestOneProduct);

        HttpContext allProducts = server.createContext("/products/all");
        allProducts.setHandler(WebServer::handleRequestAllProducts);

        HttpContext user = server.createContext("/users");
        user.setHandler(WebServer::handleRequestOneUser);

        HttpContext allUsers = server.createContext("/users/all");
        allUsers.setHandler(WebServer::handleRequestAllUsers);



        server.start();
    }

    private static void handleRequest(HttpExchange exchange) throws IOException {

        Html html = new Html();
        Head head = new Head();

        html.appendChild( head );

        Title title = new Title();
        title.appendChild( new Text("Online shopping web server") );
        head.appendChild( title );

        Body body = new Body();

        P paraProductLink = new P();
        P paraUserLink = new P();
        P paraOrderLink = new P();

        A linkProducts = new A("/products/all", "/products/all");
        linkProducts.appendText("Product list");

        A linkUsers = new A("/users/all", "/users/all");
        linkUsers.appendText("User List");

        A linkOrders = new A("/orders/all", "/orders/all");
        linkOrders.appendText("Order List");

        paraProductLink.appendChild(linkProducts);
        body.appendChild(paraProductLink);
        paraUserLink.appendChild(linkUsers);
        body.appendChild(paraUserLink);
        paraOrderLink.appendChild(linkOrders);
        body.appendChild(paraOrderLink);
        html.appendChild( body );
        String response = html.write();
        exchange.sendResponseHeaders(200, response.getBytes().length);//response code and length
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private static void handleRequestOneOrder(HttpExchange exchange) throws IOException {
        String uri =  exchange.getRequestURI().toString();

        int orderID = Integer.parseInt(uri.substring(uri.lastIndexOf('/')+1));

        System.out.println(orderID);

        String url = "jdbc:sqlite:store.db";

        SQLiteDataAdapter dao = new SQLiteDataAdapter();

        dao.connect(url);

        Html html = new Html();
        Head head = new Head();

        html.appendChild( head );

        Title title = new Title();
        title.appendChild( new Text("Order Details"));
        head.appendChild( title );

        Body body = new Body();

        html.appendChild( body );

        H1 h1 = new H1();
        h1.appendChild( new Text("Order Details"));
        body.appendChild( h1 );

        P para = new P();
        para.appendChild( new Text("The server time is " + LocalDateTime.now()));
        body.appendChild(para);

        Order order = dao.loadOrder(orderID);

        if (order != null) {

            Table table = new Table();
            Tr row = new Tr();
            Th header = new Th(); header.appendText("OrderID"); row.appendChild(header);
            header = new Th(); header.appendText("Order Date"); row.appendChild(header);
            header = new Th(); header.appendText("Customer"); row.appendChild(header);
            header = new Th(); header.appendText("Total Cost"); row.appendChild(header);
            header = new Th(); header.appendText("Total Tax"); row.appendChild(header);
            table.appendChild(row);


            row = new Tr();
            //create link on each orderID mapped to "/user/<username>"
            Td cell = new Td();
            cell.appendText(String.valueOf(order.getOrderID()));
            row.appendChild(cell);
            cell = new Td(); cell.appendText(order.getDate()); row.appendChild(cell);
            cell = new Td(); cell.appendText(order.getCustomerName()); row.appendChild(cell);
            cell = new Td(); cell.appendText(String.valueOf(order.getTotalCost())); row.appendChild(cell);
            cell = new Td(); cell.appendText(String.valueOf(order.getTotalTax())); row.appendChild(cell);
            table.appendChild(row);
            table.setBorder("1");
            html.appendChild(table);

        }
        else {
            para = new P();
            para.appendText("User not found");
            html.appendChild(para);
        }

        String response = html.write();

        System.out.println(response);

        exchange.sendResponseHeaders(200, response.getBytes().length);//response code and length
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private static void handleRequestAllOrders(HttpExchange exchange) throws IOException {

        String url = "jdbc:sqlite:store.db";

        SQLiteDataAdapter dao = new SQLiteDataAdapter();

        dao.connect(url);

        List<Order> list = dao.loadAllOrders();

        Html html = new Html();
        Head head = new Head();

        html.appendChild( head );

        Title title = new Title();
        title.appendChild( new Text("Order List") );
        head.appendChild( title );

        Body body = new Body();

        html.appendChild( body );

        H1 h1 = new H1();
        h1.appendChild( new Text("Order List") );
        body.appendChild( h1 );

        P para = new P();
        para.appendChild( new Text("The server time is " + LocalDateTime.now()) );
        body.appendChild(para);

        para = new P();
        para.appendChild( new Text("The server has " + list.size() + " orders." ));
        body.appendChild(para);

        Table table = new Table();
        Tr row = new Tr();
        Th header = new Th(); header.appendText("OrderID"); row.appendChild(header);
        header = new Th(); header.appendText("Order Date"); row.appendChild(header);
        header = new Th(); header.appendText("Customer"); row.appendChild(header);
        header = new Th(); header.appendText("Total Cost"); row.appendChild(header);
        header = new Th(); header.appendText("Total Tax"); row.appendChild(header);
        table.appendChild(row);

        for (Order order : list) {

            row = new Tr();
            //create link on each orderID mapped to "/user/<username>"
            Td cell = new Td();
            A link = new A("/orders/" + String.valueOf(order.getOrderID()));
            link.appendText(String.valueOf(order.getOrderID()));
            cell.appendChild(link);
            row.appendChild(cell);
            cell = new Td(); cell.appendText(order.getDate()); row.appendChild(cell);
            cell = new Td(); cell.appendText(order.getCustomerName()); row.appendChild(cell);
            cell = new Td(); cell.appendText(String.valueOf(order.getTotalCost())); row.appendChild(cell);
            cell = new Td(); cell.appendText(String.valueOf(order.getTotalTax())); row.appendChild(cell);
            table.appendChild(row);
        }

        table.setBorder("1");

        html.appendChild(table);
        String response = html.write();

        System.out.println(response);


        exchange.sendResponseHeaders(200, response.getBytes().length);//response code and length
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }


    private static void handleRequestAllUsers(HttpExchange exchange) throws IOException {

        String url = "jdbc:sqlite:store.db";

        SQLiteDataAdapter dao = new SQLiteDataAdapter();

        dao.connect(url);

        List<User> list = dao.loadAllUsers();

        Html html = new Html();
        Head head = new Head();

        html.appendChild( head );

        Title title = new Title();
        title.appendChild( new Text("User List") );
        head.appendChild( title );

        Body body = new Body();

        html.appendChild( body );

        H1 h1 = new H1();
        h1.appendChild( new Text("User List") );
        body.appendChild( h1 );

        P para = new P();
        para.appendChild( new Text("The server time is " + LocalDateTime.now()) );
        body.appendChild(para);

        para = new P();
        para.appendChild( new Text("The server has " + list.size() + " users." ));
        body.appendChild(para);

        Table table = new Table();
        Tr row = new Tr();
        Th header = new Th(); header.appendText("userID"); row.appendChild(header);
        header = new Th(); header.appendText("Username"); row.appendChild(header);
        header = new Th(); header.appendText("Password"); row.appendChild(header);
        header = new Th(); header.appendText("Display Name"); row.appendChild(header);
        header = new Th(); header.appendText("isManager"); row.appendChild(header);
        table.appendChild(row);

        for (User user : list) {
            row = new Tr();
            Td cell = new Td(); cell.appendText(String.valueOf(user.userID)); row.appendChild(cell);
            //create link on each username mapped to "/user/<username>"
            cell = new Td();
            A link = new A("/users/" + user.userName);
            link.appendText(String.valueOf(user.userName));
            cell.appendChild(link);
            row.appendChild(cell);

            cell = new Td(); cell.appendText(user.password); row.appendChild(cell);
            cell = new Td(); cell.appendText(user.displayName); row.appendChild(cell);
            cell = new Td(); cell.appendText(String.valueOf(user.isManager)); row.appendChild(cell);
            table.appendChild(row);
        }

        table.setBorder("1");

        html.appendChild(table);
        String response = html.write();

        System.out.println(response);


        exchange.sendResponseHeaders(200, response.getBytes().length);//response code and length
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private static void handleRequestOneUser(HttpExchange exchange) throws IOException {
        String uri =  exchange.getRequestURI().toString();

        String username = uri.substring(uri.lastIndexOf('/')+1);

        System.out.println(username);


        String url = "jdbc:sqlite:store.db";

        SQLiteDataAdapter dao = new SQLiteDataAdapter();

        dao.connect(url);

        Html html = new Html();
        Head head = new Head();

        html.appendChild( head );

        Title title = new Title();
        title.appendChild( new Text("User Details"));
        head.appendChild( title );

        Body body = new Body();

        html.appendChild( body );

        H1 h1 = new H1();
        h1.appendChild( new Text("User Details"));
        body.appendChild( h1 );

        P para = new P();
        para.appendChild( new Text("The server time is " + LocalDateTime.now()) );
        body.appendChild(para);

        User user = dao.loadUser(username);

        if (user != null) {

            Table table = new Table();
            Tr row = new Tr();
            Th header = new Th(); header.appendText("userID"); row.appendChild(header);
            header = new Th(); header.appendText("Username"); row.appendChild(header);
            header = new Th(); header.appendText("Password"); row.appendChild(header);
            header = new Th(); header.appendText("Display Name"); row.appendChild(header);
            header = new Th(); header.appendText("isManager"); row.appendChild(header);
            table.appendChild(row);


            row = new Tr();
            Td cell = new Td(); cell.appendText(String.valueOf(user.userID)); row.appendChild(cell);
            cell = new Td(); cell.appendText(user.userName); row.appendChild(cell);
            cell = new Td(); cell.appendText(user.password); row.appendChild(cell);
            cell = new Td(); cell.appendText(user.displayName); row.appendChild(cell);
            cell = new Td(); cell.appendText(String.valueOf(user.isManager)); row.appendChild(cell);
            table.appendChild(row);
            table.setBorder("1");
            html.appendChild(table);

        }
        else {
            para = new P();
            para.appendText("User not found");
            html.appendChild(para);
        }

        String response = html.write();

        System.out.println(response);

        exchange.sendResponseHeaders(200, response.getBytes().length);//response code and length
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }



    private static void handleRequestAllProducts(HttpExchange exchange) throws IOException {

        String url = "jdbc:sqlite:store.db";

        SQLiteDataAdapter dao = new SQLiteDataAdapter();

        dao.connect(url);

        List<ProductModel> list = dao.loadAllProducts();

        Html html = new Html();
        Head head = new Head();

        html.appendChild( head );

        Title title = new Title();
        title.appendChild( new Text("Product List") );
        head.appendChild( title );

        Body body = new Body();

        html.appendChild( body );

        H1 h1 = new H1();
        h1.appendChild( new Text("Product List") );
        body.appendChild( h1 );

        P para = new P();
        para.appendChild( new Text("The server time is " + LocalDateTime.now()) );
        body.appendChild(para);

        para = new P();
        para.appendChild( new Text("The server has " + list.size() + " products." ));
        body.appendChild(para);

        Table table = new Table();
        Tr row = new Tr();
        Th header = new Th(); header.appendText("ProductID"); row.appendChild(header);
        header = new Th(); header.appendText("Product name"); row.appendChild(header);
        header = new Th(); header.appendText("Price"); row.appendChild(header);
        header = new Th(); header.appendText("Quantity"); row.appendChild(header);
        table.appendChild(row);

        for (ProductModel product : list) {
            row = new Tr();
            Td cell = new Td();
            //create link on each productID mapped to "/products/<ProductID>"
            A link = new A("/products/" + product.productID);
            link.appendText(String.valueOf(product.productID));
            cell.appendChild(link);
            row.appendChild(cell);

            cell = new Td(); cell.appendText(product.name); row.appendChild(cell);
            cell = new Td(); cell.appendText(String.valueOf(product.price)); row.appendChild(cell);
            cell = new Td(); cell.appendText(String.valueOf(product.quantity)); row.appendChild(cell);
            table.appendChild(row);
        }

        table.setBorder("1");

        html.appendChild(table);
        String response = html.write();

        System.out.println(response);


        exchange.sendResponseHeaders(200, response.getBytes().length);//response code and length
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }


    private static void handleRequestOneProduct(HttpExchange exchange) throws IOException {
        String uri =  exchange.getRequestURI().toString();

        int id = Integer.parseInt(uri.substring(uri.lastIndexOf('/')+1));

        System.out.println(id);


        String url = "jdbc:sqlite:store.db";

        SQLiteDataAdapter dao = new SQLiteDataAdapter();

        dao.connect(url);

        Html html = new Html();
        Head head = new Head();

        html.appendChild( head );

        Title title = new Title();
        title.appendChild( new Text("Product Details"));
        head.appendChild( title );

        Body body = new Body();

        html.appendChild( body );

        H1 h1 = new H1();
        h1.appendChild( new Text("Product Details"));
        body.appendChild( h1 );

        P para = new P();
        para.appendChild( new Text("The server time is " + LocalDateTime.now()) );
        body.appendChild(para);

        ProductModel product = dao.loadProduct(id);

        if (product != null) {

            Table table = new Table();
            Tr row = new Tr();
            Th header = new Th(); header.appendText("ProductID"); row.appendChild(header);
            header = new Th(); header.appendText("Product name"); row.appendChild(header);
            header = new Th(); header.appendText("Price"); row.appendChild(header);
            header = new Th(); header.appendText("Quantity"); row.appendChild(header);
            table.appendChild(row);


            row = new Tr();
            Td cell = new Td(); cell.appendText(String.valueOf(product.productID)); row.appendChild(cell);
            cell = new Td(); cell.appendText(product.name); row.appendChild(cell);
            cell = new Td(); cell.appendText(String.valueOf(product.price)); row.appendChild(cell);
            cell = new Td(); cell.appendText(String.valueOf(product.quantity)); row.appendChild(cell);
            table.appendChild(row);
            table.setBorder("1");
            html.appendChild(table);
        }
        else {
            para = new P();
            para.appendText("Product not found");
            html.appendChild(para);
        }

        String response = html.write();

        System.out.println(response);

        exchange.sendResponseHeaders(200, response.getBytes().length);//response code and length
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
