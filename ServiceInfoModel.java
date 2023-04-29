public class ServiceInfoModel {
    public static int PRODUCT_INFO_SERVICE = 1;
    public static int ORDER_INFO_SERVICE = 2;
    public static int PRODUCT_PRICE_UPDATE_SERVICE = 3;
    public static int PRODUCT_QUANTITY_UPDATE_SERVICE = 4;

    public int serviceCode;
    public String serviceHostAddress;

    public int serviceHostPort;

    public String toString() {
        String result = "Service Code: " + this.serviceCode
                + "\nService Addr: " + this.serviceHostAddress
                + "\nService Host: " + this.serviceHostPort;

        return result;
    }



}
