package com.trustcart.model;

import java.math.BigDecimal;

public class CartLine {
    private final Product product;
    private final int quantity;
    private final BigDecimal lineTotal;

    public CartLine(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.lineTotal = product.getPrice().multiply(BigDecimal.valueOf(quantity));
    }

    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public BigDecimal getLineTotal() { return lineTotal; }
}
