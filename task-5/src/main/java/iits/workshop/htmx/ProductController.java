package iits.workshop.htmx;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/")
    public String index(@RequestParam(required = false) String search, Model model) {
        List<Product> products;
        
        if (search != null && !search.trim().isEmpty()) {
            products = productService.searchProducts(search);
        } else {
            products = productService.getAllProducts();
        }
        
        model.addAttribute("products", products);
        model.addAttribute("search", search != null ? search : "");
        
        return "index";
    }
}
