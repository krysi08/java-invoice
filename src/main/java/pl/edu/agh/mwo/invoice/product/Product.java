package pl.edu.agh.mwo.invoice.product;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Product {
    private final String name;
    private final BigDecimal price;
    private final BigDecimal taxPercent;

    public Product(String name, BigDecimal price, BigDecimal tax) {
        if (name == null || name.equals("") || price == null || tax == null
                || tax.compareTo(BigDecimal.ZERO) < 0
                || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException();
        }
        this.name = name;
        this.price = price;
        this.taxPercent = tax;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getTaxPercent() {
        return taxPercent;
    }

    public BigDecimal getPriceWithTax() {
        BigDecimal multiplier = BigDecimal.ONE.add(taxPercent);
        BigDecimal taxed = price.multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
        return taxed;
    }
}