public class ProductModel {
    public int productID;
    public String name;
    public double price;
    public double quantity;

    public String toString() {
        String result = "ProductID: " + this.productID
                + "\n     Name: " + this.name
                + "\n    Price: " + this.price
                + "\n Quantity: " + this.quantity;

        return result;
    }

}



