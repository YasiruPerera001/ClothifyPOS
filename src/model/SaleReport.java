package model;

import java.util.Date;
import javafx.beans.property.*;

public class SaleReport {
    private IntegerProperty orderId;
    private ObjectProperty<Date> date;
    private StringProperty productName;
    private IntegerProperty quantity;
    private DoubleProperty price;
    private DoubleProperty total;

    public SaleReport(int orderId, Date date, String productName, int quantity, double price, double total){
        this.orderId = new SimpleIntegerProperty(orderId);
        this.date = new SimpleObjectProperty<>(date);
        this.productName = new SimpleStringProperty(productName);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.price = new SimpleDoubleProperty(price);
        this.total = new SimpleDoubleProperty(total);
    }

    // getters
    public int getOrderId() { return orderId.get(); }
    public Date getDate() { return date.get(); }
    public String getProductName() { return productName.get(); }
    public int getQuantity() { return quantity.get(); }
    public double getPrice() { return price.get(); }
    public double getTotal() { return total.get(); }
}