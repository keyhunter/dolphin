package com.dolphin.test;

import java.util.List;
import java.util.Map;

public class Order {

    private long                      orderNumber;

    private List<Product>             products;

    private Map<String, List<Product>> activityProducts;

    public long getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(long orderNumber) {
        this.orderNumber = orderNumber;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public Map<String, List<Product>> getActivityProducts() {
        return activityProducts;
    }

    public void setActivityProducts(Map<String, List<Product>> activityProducts) {
        this.activityProducts = activityProducts;
    }
    
    

}
