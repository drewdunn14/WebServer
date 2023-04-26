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

        HttpContext context = server.createContext("/users");
        context.setHandler(WebServer::handleRequestAllUsers);

        HttpContext product = server.createContext("/products");
        product.setHandler(WebServer::handleRequestOneProduct);

        HttpContext allproducts = server.createContext("/products/all");
        allproducts.setHandler(WebServer::handleRequestAllProducts);


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

        P para = new P();

        A link = new A("/products/all", "/products/all");
        link.appendText("Product list");

        para.appendChild(link);
        body.appendChild(para);
        html.appendChild( body );
        String response = html.write();
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
            //create link on each productID mapped to "/products/<ProductID>"
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
