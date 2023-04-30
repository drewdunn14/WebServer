import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLiteDataAdapter implements DataAccess {
    Connection conn = null;

    @Override
    public void connect(String url) {
        try {
            // db parameters
            // create a connection to the database
            Class.forName("org.sqlite.JDBC");

            conn = DriverManager.getConnection(url);

            if (conn == null)
                System.out.println("Cannot make the connection!!!");
            else
                System.out.println("The connection object is " + conn);

            System.out.println("Connection to SQLite has been established.");

            /* Test data!!!
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Product");

            while (rs.next())
                System.out.println(rs.getString(1) + " " + rs.getString(2) + " " + rs.getString(3) + " " + rs.getString(4));
            */

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void saveProduct(ProductModel product) {
        try {
            Statement stmt = conn.createStatement();

            if (loadProduct(product.productID) == null) {           // this is a new product!
                stmt.execute("INSERT INTO Product(productID, name, price, quantity) VALUES ("
                        + product.productID + ","
                        + '\'' + product.name + '\'' + ","
                        + product.price + ","
                        + product.quantity + ")"
                );
            }
            else {
                stmt.executeUpdate("UPDATE Product SET "
                        + "productID = " + product.productID + ","
                        + "name = " + '\'' + product.name + '\'' + ","
                        + "price = " + product.price + ","
                        + "quantity = " + product.quantity +
                        " WHERE productID = " + product.productID
                );

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public ProductModel loadProduct(int productID) {
        ProductModel product = null;
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Product WHERE ProductID = " + productID);
            if (rs.next()) {
                product = new ProductModel();
                product.productID = rs.getInt(1);
                product.name = rs.getString(2);
                product.price = rs.getDouble(3);
                product.quantity = rs.getDouble(4);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return product;
    }

    @Override
    public List<ProductModel> loadAllProducts() {
        List<ProductModel> list = new ArrayList<>();
        ProductModel product = null;
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Product ");
            while (rs.next()) {
                product = new ProductModel();
                product.productID = rs.getInt(1);
                product.name = rs.getString(2);
                product.price = rs.getDouble(3);
                product.quantity = rs.getDouble(4);
                list.add(product);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }


    public User loadUser(String username) {
        User user = null;
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM User WHERE UserName = ?");
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                user = new User();
                user.userID = rs.getInt(1);
                user.userName = rs.getString(2);
                user.password = rs.getString(3);
                user.displayName = rs.getString(4);
                user.isManager = rs.getBoolean(5);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return user;
    }


    public List<User> loadAllUsers() {
        List<User> list = new ArrayList<>();
        User user = null;
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM User ");
            while (rs.next()) {
                user = new User();
                user.userID = rs.getInt(1);
                user.userName = rs.getString(2);
                user.password = rs.getString(3);
                user.displayName = rs.getString(4);
                user.isManager = rs.getBoolean(5);
                list.add(user);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    @Override
    public User authenticateUser(User user) {
        User registeredUser = new User();
        try {
            PreparedStatement statement = conn.prepareStatement("SELECT * FROM User WHERE UserName = ? AND Password = ?");
            statement.setString(1, user.userName);
            statement.setString(2, user.password);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                registeredUser.userID = resultSet.getInt("UserID");
                registeredUser.userName = resultSet.getString("UserName");
                registeredUser.password = resultSet.getString("Password");
                registeredUser.displayName = resultSet.getString("DisplayName");
                registeredUser.isManager = resultSet.getBoolean("IsManager");
                resultSet.close();
                statement.close();

                return registeredUser;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }


    public Order loadOrder(int orderID) {
        Order order = null;
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Orders WHERE OrderID = " + orderID);
            if (rs.next()) {
                order = new Order();
                order.setOrderID(rs.getInt(1));
                order.setDate(rs.getString(2));
                order.setCustomerName(rs.getString(3));
                order.setTotalCost(rs.getDouble(4));
                order.setTotalTax(rs.getDouble(5));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return order;
    }

    public List<Order> loadAllOrders() {
        List<Order> list = new ArrayList<>();
        Order order = null;
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Orders ");
            while (rs.next()) {
                order = new Order();
                order.setOrderID(rs.getInt(1));
                order.setDate(rs.getString(2));
                order.setCustomerName(rs.getString(3));
                order.setTotalCost(rs.getDouble(4));
                order.setTotalTax(rs.getDouble(5));
                list.add(order);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }


}
