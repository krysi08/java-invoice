package pl.edu.agh.mwo.invoice;

import org.junit.jupiter.api.Test;
import pl.edu.agh.mwo.invoice.product.BottleOfWine;
import pl.edu.agh.mwo.invoice.product.FuelCanister;
import pl.edu.agh.mwo.invoice.product.Product;

import java.math.BigDecimal;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ProductTest {

    @Test
    void testRegularProductPriceWithTax() {
        Product product = new Product("Test", new BigDecimal("100.00"), new BigDecimal("0.23"));
        BigDecimal expected = new BigDecimal("123.00");
        assertEquals(0, expected.compareTo(product.getPriceWithTax()));
    }

    @Test
    void testBottleOfWineIncludesExciseDuty() {
        Product wine = new BottleOfWine("Wine", new BigDecimal("50.00"), new BigDecimal("0.23"));
        BigDecimal expected = new BigDecimal("67.06");
        assertEquals(0, expected.compareTo(wine.getPriceWithTax()));
    }

    @Test
    void testFuelCanisterIncludesExciseDuty() {
        Product fuel = new FuelCanister("Fuel", new BigDecimal("120.00"), new BigDecimal("0.23"));
        BigDecimal expected = new BigDecimal("153.16");
        assertEquals(0, expected.compareTo(fuel.getPriceWithTax()));
    }

    @Test
    void testInvalidProductThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Product(null, new BigDecimal("100.00"), new BigDecimal("0.23"));
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new Product("", new BigDecimal("100.00"), new BigDecimal("0.23"));
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new Product("Test", new BigDecimal("-1"), new BigDecimal("0.23"));
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new Product("Test", new BigDecimal("100.00"), new BigDecimal("-0.01"));
        });
    }
}