import java.sql.Date;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;


public class Order {
    private int orderID;
    private String customerName;
    private double totalCost;
    private double totalTax;
    private String date;

    private List<OrderLine> lines;
    private static final DecimalFormat dfZero = new DecimalFormat("0.00");

    public Order() {
        lines = new ArrayList<>();
    }

    public String getDate() {
        return date;
    }

    public void setDate() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        this.date = dtf.format(now);
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public double getTotalTax() {
        return totalTax;
    }

    public void setTotalTax(double totalTax) {
        this.totalTax = Double.parseDouble(dfZero.format(totalTax));
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void addLine(OrderLine line) {
        lines.add(line);
    }

    public void removeLine(OrderLine line) {
        lines.remove(line);
    }

    public List<OrderLine> getLines() {
        return lines;
    }

    @Override
    public String toString() {
        String result = "orderID: " + this.orderID + "\tCustomer: " + this.customerName + "\tTotal: " + this.totalCost
                + "\tTax: " + this.totalTax + "\tDate: " + this.date + "\n";
        for (OrderLine line: lines) {
            result += line.toString() + "\n";
        }
        return result;
    }



}