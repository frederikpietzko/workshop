package iits.workshop.htmx;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductService productService;
    
    @GetMapping("/")
    public String listProducts(
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "name") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String direction,
            Model model) {
        
        Page<Product> productPage = productService.searchProducts(search, page, sortBy, direction);
        
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalItems", productPage.getTotalElements());
        model.addAttribute("search", search);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);
        model.addAttribute("hasNext", productPage.hasNext());
        model.addAttribute("hasPrevious", productPage.hasPrevious());
        
        return "products";
    }
    
    @PostMapping("/products/{id}/delete")
    public String deleteProduct(@PathVariable Long id,
                                @RequestParam(required = false, defaultValue = "") String search,
                                @RequestParam(required = false, defaultValue = "0") int page,
                                @RequestParam(required = false, defaultValue = "name") String sortBy,
                                @RequestParam(required = false, defaultValue = "asc") String direction) {
        productService.deleteProduct(id);
        return "redirect:/?search=" + search + "&page=" + page + "&sortBy=" + sortBy + "&direction=" + direction;
    }
}
