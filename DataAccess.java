import java.util.List;

public interface DataAccess {
    void connect(String str);

    void saveProduct(ProductModel product);

    ProductModel loadProduct(int productID);

    List<ProductModel> loadAllProducts();
}
