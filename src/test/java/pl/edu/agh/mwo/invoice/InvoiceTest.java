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
import pl.edu.agh.mwo.invoice.Invoice;
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
        Product onions = new TaxFreeProduct("Warzywa", new BigDecimal("10"));
        Product apples = new TaxFreeProduct("Owoce", new BigDecimal("10"));
        invoice.addProduct(onions);
        invoice.addProduct(apples);
        Assert.assertThat(new BigDecimal("20"), Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testInvoiceSubtotalWithManySameProducts() {
        Product onions = new TaxFreeProduct("Warzywa", BigDecimal.valueOf(10));
        invoice.addProduct(onions, 100);
        Assert.assertThat(new BigDecimal("1000"), Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testInvoiceHasTheSameSubtotalAndTotalIfTaxIsZero() {
        Product taxFreeProduct = new TaxFreeProduct("Warzywa", new BigDecimal("199.99"));
        invoice.addProduct(taxFreeProduct);
        Assert.assertThat(invoice.getNetTotal(), Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test
    public void testInvoiceHasProperSubtotalForManyProducts() {
        invoice.addProduct(new TaxFreeProduct("Owoce", new BigDecimal("200")));
        invoice.addProduct(new DairyProduct("Maslanka", new BigDecimal("100")));
        invoice.addProduct(new OtherProduct("Wino", new BigDecimal("10")));
        Assert.assertThat(new BigDecimal("310"), Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testInvoiceHasProperTaxValueForManyProduct() {
        // tax: 0
        invoice.addProduct(new TaxFreeProduct("Pampersy", new BigDecimal("200")));
        // tax: 8
        invoice.addProduct(new DairyProduct("Kefir", new BigDecimal("100")));
        // tax: 2.30
        invoice.addProduct(new OtherProduct("Piwko", new BigDecimal("10")));
        Assert.assertThat(new BigDecimal("10.30"), Matchers.comparesEqualTo(invoice.getTaxTotal()));
    }

    @Test
    public void testInvoiceHasProperTotalValueForManyProduct() {
        // price with tax: 200
        invoice.addProduct(new TaxFreeProduct("Maskotki", new BigDecimal("200")));
        // price with tax: 108
        invoice.addProduct(new DairyProduct("Maslo", new BigDecimal("100")));
        // price with tax: 12.30
        invoice.addProduct(new OtherProduct("Chipsy", new BigDecimal("10")));
        Assert.assertThat(new BigDecimal("320.30"), Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test
    public void testInvoiceHasPropoerSubtotalWithQuantityMoreThanOne() {
        // 2x kubek - price: 10
        invoice.addProduct(new TaxFreeProduct("Kubek", new BigDecimal("5")), 2);
        // 3x kozi serek - price: 30
        invoice.addProduct(new DairyProduct("Kozi Serek", new BigDecimal("10")), 3);
        // 1000x pinezka - price: 10
        invoice.addProduct(new OtherProduct("Pinezka", new BigDecimal("0.01")), 1000);
        Assert.assertThat(new BigDecimal("50"), Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testInvoiceHasPropoerTotalWithQuantityMoreThanOne() {
        // 2x chleb - price with tax: 10
        invoice.addProduct(new TaxFreeProduct("Chleb", new BigDecimal("5")), 2);
        // 3x chedar - price with tax: 32.40
        invoice.addProduct(new DairyProduct("Chedar", new BigDecimal("10")), 3);
        // 1000x pinezka - price with tax: 12.30
        invoice.addProduct(new OtherProduct("Pinezka", new BigDecimal("0.01")), 1000);
        Assert.assertThat(new BigDecimal("54.70"), Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvoiceWithZeroQuantity() {
        invoice.addProduct(new TaxFreeProduct("Tablet", new BigDecimal("1678")), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvoiceWithNegativeQuantity() {
        invoice.addProduct(new DairyProduct("Zsiadle mleko", new BigDecimal("5.55")), -1);
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
        assertEquals(2, inv.getInvoiceNumber(), "Pierwsza faktura powinna mieć numer 2");
    }

    @Test
    public void sequentialInvoicesHaveIncrementingNumbers() {
        Invoice inv1 = new Invoice();
        Invoice inv2 = new Invoice();
        Invoice inv3 = new Invoice();

        assertEquals(2, inv1.getInvoiceNumber(), "Pierwsza faktura = 2");
        assertEquals(3, inv2.getInvoiceNumber(), "Druga faktura = 3");
        assertEquals(4, inv3.getInvoiceNumber(), "Trzecia faktura = 4");
    }

    @Test
    public void invoiceNumbersAreUnique() {
        Invoice inv1 = new Invoice();
        Invoice inv2 = new Invoice();
        assertNotEquals(inv1.getInvoiceNumber(), inv2.getInvoiceNumber(),
                "Dwie różne faktury nie mogą mieć tego samego numeru");
    }

    @Test
    public void manyInvoicesProduceUniqueNumbers() {
        int n = 10;
        Set<Integer> seen = new HashSet<>();
        for (int i = 0; i < n; i++) {
            Invoice inv = new Invoice();
            boolean added = seen.add(inv.getInvoiceNumber());
            assertTrue(added, "Numer faktury " + inv.getInvoiceNumber() + " jest duplikatem");
        }
        assertEquals(n, seen.size(), "Powinno być dokładnie " + n + " unikalnych numerów");
    }


    @Test
    public void testDetailsMultipleProducts() {
        Invoice invoice = new Invoice();
        Product bread = new Product("Chleb", new BigDecimal("5.00"), new BigDecimal("0.23"));
        Product milk  = new Product("Mleko", new BigDecimal("3.50"), new BigDecimal("0.23"));

        invoice.addProduct(bread, 2);
        invoice.addProduct(milk, 3);

        String expected =
                "No. Invoice 1\n" +
                        "Chleb,  Quantity: 2, Price: 5.00\n" +
                        "Mleko,  Quantity: 3, Price: 3.50\n" +
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
        Product bread = new Product("Chleb", new BigDecimal("5.00"), new BigDecimal("0.23"));


        invoice.addProduct(bread, 1);

        invoice.addProduct(bread, 2);


        String details = invoice.getInvoiceDetails();

        String expected =
                "Invoice number 1\n" +
                        "Chleb,  Quantity: 2, Price: 5.00\n" +
                        "No. Positions: 1";

        assertEquals(expected, details);
    }
}
