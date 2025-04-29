package pl.edu.agh.mwo.invoice;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import pl.edu.agh.mwo.invoice.product.Product;

public class Invoice {

    private static int counter = 0;
    private int invoiceNumber;


    public Invoice() {
        this.invoiceNumber = counter++;
    }

    public int getInvoiceNumber() {
        return invoiceNumber;
    }

    private Map<Product, Integer> products = new LinkedHashMap<Product, Integer>();

    public void addProduct(Product product) {
        addProduct(product, 1);
    }

    public void addProduct(Product product, Integer quantity) {
        if (product == null || quantity <= 0) {
            throw new IllegalArgumentException();
        }

        Integer existing = products.getOrDefault(product, 0);

        products.put(product, existing + quantity);
    }

    public BigDecimal getNetTotal() {
        BigDecimal totalNet = BigDecimal.ZERO;
        for (Product product : products.keySet()) {
            BigDecimal quantity = new BigDecimal(products.get(product));
            totalNet = totalNet.add(product.getPrice().multiply(quantity));
        }
        return totalNet;
    }

    public BigDecimal getTaxTotal() {
        return getGrossTotal().subtract(getNetTotal());
    }

    public BigDecimal getGrossTotal() {
        BigDecimal totalGross = BigDecimal.ZERO;
        for (Product product : products.keySet()) {
            BigDecimal quantity = new BigDecimal(products.get(product));
            totalGross = totalGross.add(product.getPriceWithTax().multiply(quantity));
        }
        return totalGross;
    }
    public String getInvoiceDetails() {
        StringBuilder sb = new StringBuilder();
        sb.append("No. Invoice ").append(invoiceNumber).append("\n");
        for (Map.Entry<Product, Integer> entry : products.entrySet()) {
            Product product = entry.getKey();
            Integer qty = entry.getValue();
            sb.append(product.getName())
                    .append(", Quantity: ").append(qty)
                    .append(", Price: ").append(product.getPrice())
                    .append("\n");
        }
        sb.append("No. Positions: ").append(products.size());
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Invoice number " + invoiceNumber + ", Positions: " + products.size();
    }
}
