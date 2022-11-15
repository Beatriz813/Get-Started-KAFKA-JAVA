package br.com.alura.ecommerce.usermodule.modelos;

import java.math.BigDecimal;

public class Order {
    private final String orderId, email;
    private final BigDecimal amount;


    public Order(String orderId, BigDecimal amount, String _email) {
        this.orderId = orderId;
        this.amount = amount;
        this.email = _email;
    }
    public String getEmail() {
        return this.email;
    }

}
