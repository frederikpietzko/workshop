package iits.workshop.htmx;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
    
    private final ProductRepository productRepository;
    
    @Override
    public void run(String... args) {
        if (productRepository.count() == 0) {
            loadSampleData();
        }
    }
    
    private void loadSampleData() {
        Random random = new Random();
        List<Product> products = new ArrayList<>();
        
        String[] categories = {"Electronics", "Clothing", "Food", "Books", "Toys", "Sports", "Home", "Garden", "Tools", "Beauty"};
        String[] adjectives = {"Premium", "Deluxe", "Classic", "Modern", "Vintage", "Professional", "Budget", "Luxury", "Standard", "Advanced"};
        String[] items = {
            "Laptop", "Mouse", "Keyboard", "Monitor", "Phone", "Tablet", "Headphones", "Speaker", "Camera", "Watch",
            "Shirt", "Pants", "Shoes", "Hat", "Jacket", "Dress", "Socks", "Gloves", "Belt", "Scarf",
            "Coffee", "Tea", "Chocolate", "Cookies", "Bread", "Cheese", "Milk", "Juice", "Water", "Snacks",
            "Novel", "Textbook", "Magazine", "Comic", "Dictionary", "Atlas", "Cookbook", "Guide", "Manual", "Journal",
            "Ball", "Doll", "Puzzle", "Game", "Car", "Train", "Plane", "Robot", "Bear", "Blocks",
            "Basketball", "Football", "Baseball", "Tennis", "Golf", "Bike", "Skateboard", "Helmet", "Bat", "Glove",
            "Chair", "Table", "Lamp", "Rug", "Pillow", "Blanket", "Curtain", "Mirror", "Clock", "Vase",
            "Seeds", "Shovel", "Rake", "Hose", "Pot", "Soil", "Fertilizer", "Gloves", "Pruner", "Sprinkler",
            "Hammer", "Screwdriver", "Wrench", "Drill", "Saw", "Pliers", "Tape", "Level", "Chisel", "File",
            "Shampoo", "Soap", "Lotion", "Cream", "Perfume", "Lipstick", "Mascara", "Foundation", "Brush", "Mirror"
        };
        
        for (int i = 0; i < 150; i++) {
            String adjective = adjectives[random.nextInt(adjectives.length)];
            String item = items[random.nextInt(items.length)];
            String category = categories[random.nextInt(categories.length)];
            
            Product product = new Product();
            product.setName(adjective + " " + item + " " + (i + 1));
            product.setCategory(category);
            product.setPrice(Math.round((10 + random.nextDouble() * 990) * 100.0) / 100.0);
            product.setStock(random.nextInt(500));
            
            products.add(product);
        }
        
        productRepository.saveAll(products);
        System.out.println("Loaded " + products.size() + " sample products");
    }
}
