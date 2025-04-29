package pl.edu.agh.mwo.invoice;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.junit.jupiter.api.BeforeEach;
import pl.edu.agh.mwo.invoice.product.DairyProduct;
import pl.edu.agh.mwo.invoice.product.OtherProduct;
import pl.edu.agh.mwo.invoice.product.Product;
import pl.edu.agh.mwo.invoice.product.TaxFreeProduct;


import static org.junit.jupiter.api.Assertions.*;

public class InvoiceTest {
    private Invoice invoice;
    private Product product;

    @Before
    public void createEmptyInvoiceForTheTest() {
        invoice = new Invoice();
    }

    @Test
    public void testEmptyInvoiceHasEmptySubtotal() {
        Assert.assertThat(BigDecimal.ZERO, Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testEmptyInvoiceHasEmptyTaxAmount() {
        Assert.assertThat(BigDecimal.ZERO, Matchers.comparesEqualTo(invoice.getTaxTotal()));
    }

    @Test
    public void testEmptyInvoiceHasEmptyTotal() {
        Assert.assertThat(BigDecimal.ZERO, Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test
    public void testInvoiceSubtotalWithTwoDifferentProducts() {
        Product onions = new TaxFreeProduct("Orange", new BigDecimal("10"));
        Product apples = new TaxFreeProduct("Apple", new BigDecimal("10"));
        invoice.addProduct(onions);
        invoice.addProduct(apples);
        Assert.assertThat(new BigDecimal("20"), Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testInvoiceSubtotalWithManySameProducts() {
        Product onions = new TaxFreeProduct("Orange", BigDecimal.valueOf(10));
        invoice.addProduct(onions, 100);
        Assert.assertThat(new BigDecimal("1000"), Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testInvoiceHasTheSameSubtotalAndTotalIfTaxIsZero() {
        Product taxFreeProduct = new TaxFreeProduct("Orange", new BigDecimal("199.99"));
        invoice.addProduct(taxFreeProduct);
        Assert.assertThat(invoice.getNetTotal(), Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test
    public void testInvoiceHasProperSubtotalForManyProducts() {
        invoice.addProduct(new TaxFreeProduct("Fruit", new BigDecimal("200")));
        invoice.addProduct(new DairyProduct("Fruit", new BigDecimal("100")));
        invoice.addProduct(new OtherProduct("Wine", new BigDecimal("10")));
        Assert.assertThat(new BigDecimal("310"), Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testInvoiceHasProperTaxValueForManyProduct() {

        invoice.addProduct(new TaxFreeProduct("Vegetables", new BigDecimal("200")));

        invoice.addProduct(new DairyProduct("Ice", new BigDecimal("100")));

        invoice.addProduct(new OtherProduct("Beer", new BigDecimal("10")));
        Assert.assertThat(new BigDecimal("10.30"), Matchers.comparesEqualTo(invoice.getTaxTotal()));
    }

    @Test
    public void testInvoiceHasProperTotalValueForManyProduct() {

        invoice.addProduct(new TaxFreeProduct("Games", new BigDecimal("200")));

        invoice.addProduct(new DairyProduct("Sweet", new BigDecimal("100")));

        invoice.addProduct(new OtherProduct("Chips", new BigDecimal("10")));
        Assert.assertThat(new BigDecimal("320.30"), Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test
    public void testInvoiceHasPropoerSubtotalWithQuantityMoreThanOne() {

        invoice.addProduct(new TaxFreeProduct("Cup", new BigDecimal("5")), 2);

        invoice.addProduct(new DairyProduct("Cottage Cheese", new BigDecimal("10")), 3);

        invoice.addProduct(new OtherProduct("Picture", new BigDecimal("0.01")), 1000);
        Assert.assertThat(new BigDecimal("50"), Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testInvoiceHasPropoerTotalWithQuantityMoreThanOne() {

        invoice.addProduct(new TaxFreeProduct("Bread", new BigDecimal("5")), 2);

        invoice.addProduct(new DairyProduct("Chedar", new BigDecimal("10")), 3);

        invoice.addProduct(new OtherProduct("Pork", new BigDecimal("0.01")), 1000);

        Assert.assertThat(new BigDecimal("52.40"), Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvoiceWithZeroQuantity() {
        invoice.addProduct(new TaxFreeProduct("Laptop", new BigDecimal("1678")), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvoiceWithNegativeQuantity() {
        invoice.addProduct(new DairyProduct("Cottage Milk", new BigDecimal("5.55")), -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddingNullProduct() {
        invoice.addProduct(null);
    }

    @BeforeEach
    public void resetCounter() throws Exception {
        Field counterField = Invoice.class.getDeclaredField("counter");
        counterField.setAccessible(true);
        counterField.set(null, 0);
    }

    @Test
    public void firstInvoiceHasNumberOne() {
        Invoice inv = new Invoice();
        assertEquals(1, inv.getInvoiceNumber(), "First inv. should be no. 1");
    }

    @Test
    public void sequentialInvoicesHaveIncrementingNumbers() {
        Invoice inv1 = new Invoice();
        Invoice inv2 = new Invoice();
        Invoice inv3 = new Invoice();

        assertEquals(1, inv1.getInvoiceNumber(), "First inv. should be no. 1");
        assertEquals(2, inv2.getInvoiceNumber(), "Second inv. should be no. 2");
        assertEquals(3, inv3.getInvoiceNumber(), "Third inv. should be no. 3");
    }

    @Test
    public void invoiceNumbersAreUnique() {
        Invoice inv1 = new Invoice();
        Invoice inv2 = new Invoice();
        assertNotEquals(inv1.getInvoiceNumber(), inv2.getInvoiceNumber(),
                "Two others inv. can't be the same");
    }

    @Test
    public void manyInvoicesProduceUniqueNumbers() {
        int n = 10;
        Set<Integer> seen = new HashSet<>();
        for (int i = 0; i < n; i++) {
            Invoice inv = new Invoice();
            boolean added = seen.add(inv.getInvoiceNumber());
            assertTrue(added, "Inv No " + inv.getInvoiceNumber() + " is duplicated");
        }
        assertEquals(n, seen.size(), "Should be exactly " + n + " unique numbers");
    }


    @Test
    public void testDetailsMultipleProducts() {
        Invoice invoice = new Invoice();
        Product bread = new Product("Bread", new BigDecimal("5.00"), new BigDecimal("0.23"));
        Product milk  = new Product("Milk", new BigDecimal("3.50"), new BigDecimal("0.23"));

        invoice.addProduct(bread, 2);
        invoice.addProduct(milk, 3);

        String expected =
                "No. Invoice 1\n" +
                        "Bread, Quantity: 2, Price: 5.00\n" +
                        "Milk, Quantity: 3, Price: 3.50\n" +
                        "No. Positions: 2";

        assertEquals(expected, invoice.getInvoiceDetails());
    }

    @Test
    public void testEmptyInvoiceDetails() {
        Invoice invoice = new Invoice();

        String expected =
                "No. Invoice 1\n" +
                        "No. Positions: 0";

        assertEquals(expected, invoice.getInvoiceDetails());
    }
    @Test
    public void addingSameProductTwiceIncreasesQuantityNotLines() {
        Invoice invoice = new Invoice();
        Product bread = new Product("Bread", new BigDecimal("5.00"), new BigDecimal("0.23"));


        invoice.addProduct(bread, 1);

        invoice.addProduct(bread, 2);


        String details = invoice.getInvoiceDetails();

        String expected =
                "No. Invoice 1\n" +
                        "Bread, Quantity: 3, Price: 5.00\n" +
                        "No. Positions: 1";

        assertEquals(expected, details);
    }
}
