import java.util.List;

public interface DataAccess {
    void connect(String str);

    void saveProduct(ProductModel product);

    ProductModel loadProduct(int productID);

    List<ProductModel> loadAllProducts();

    Order loadOrder(int orderID);

    User authenticateUser(User user);

    void cancelOrder(Order order);
}
