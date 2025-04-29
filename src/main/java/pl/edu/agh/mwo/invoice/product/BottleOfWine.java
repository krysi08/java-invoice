package pl.edu.agh.mwo.invoice.product;


import java.math.BigDecimal;


public class BottleOfWine extends Product {
    private static final BigDecimal EXCISE = new BigDecimal("5.56");

    public BottleOfWine(String name, BigDecimal price, BigDecimal tax) {
        super(name, price, tax);
    }

    @Override
    public BigDecimal getPriceWithTax() {
        return super.getPriceWithTax().add(EXCISE);
    }
}