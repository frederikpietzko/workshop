package iits.workshop.htmx;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;

    public DataInitializer(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) {
        List<Product> products = List.of(
                new Product(
                        "Wireless Bluetooth Headphones",
                        new BigDecimal("79.99"),
                        "Electronics",
                        "Premium noise-cancelling headphones with 30-hour battery life"
                ),
                new Product(
                        "Mechanical Gaming Keyboard",
                        new BigDecimal("129.99"),
                        "Electronics",
                        "RGB backlit mechanical keyboard with Cherry MX switches"
                ),
                new Product(
                        "Stainless Steel Water Bottle",
                        new BigDecimal("24.99"),
                        "Home & Kitchen",
                        "Insulated 32oz water bottle keeps drinks cold for 24 hours"
                ),
                new Product(
                        "Yoga Mat Premium",
                        new BigDecimal("34.99"),
                        "Sports",
                        "Extra thick exercise mat with carrying strap"
                ),
                new Product(
                        "Coffee Maker",
                        new BigDecimal("89.99"),
                        "Home & Kitchen",
                        "Programmable 12-cup coffee maker with thermal carafe"
                ),
                new Product(
                        "Running Shoes",
                        new BigDecimal("119.99"),
                        "Sports",
                        "Lightweight running shoes with responsive cushioning"
                ),
                new Product(
                        "Laptop Stand",
                        new BigDecimal("39.99"),
                        "Electronics",
                        "Adjustable aluminum laptop stand for ergonomic viewing"
                ),
                new Product(
                        "Book: Clean Code",
                        new BigDecimal("44.99"),
                        "Books",
                        "A handbook of agile software craftsmanship by Robert C. Martin"
                ),
                new Product(
                        "USB-C Hub",
                        new BigDecimal("49.99"),
                        "Electronics",
                        "7-in-1 USB-C hub with HDMI, USB 3.0, and SD card reader"
                ),
                new Product(
                        "Desk Lamp LED",
                        new BigDecimal("29.99"),
                        "Home & Kitchen",
                        "Adjustable LED desk lamp with touch controls and USB charging"
                ),
                new Product(
                        "Backpack Laptop",
                        new BigDecimal("59.99"),
                        "Accessories",
                        "Water-resistant backpack with padded laptop compartment"
                ),
                new Product(
                        "Wireless Mouse",
                        new BigDecimal("19.99"),
                        "Electronics",
                        "Ergonomic wireless mouse with silent clicks"
                )
        );

        productRepository.saveAll(products);
    }
}
