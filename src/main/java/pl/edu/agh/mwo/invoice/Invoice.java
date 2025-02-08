package pl.edu.agh.mwo.invoice;

import java.math.BigDecimal;
import java.util.Collection;

import pl.edu.agh.mwo.invoice.product.Product;

public class Invoice {
    private Collection<Product> products;

    public void addProduct(Product product) {
        this.products.add(product);
    }

    public void addProduct(Product product, Integer quantity) {

        this.products.add(product);
    this.products.add(product);
    }

    public BigDecimal getSubtotal() {
        return null;
    }

    public BigDecimal getTax() {
        return null;
    }

    public BigDecimal getTotal() {
        return null;
    }
}
