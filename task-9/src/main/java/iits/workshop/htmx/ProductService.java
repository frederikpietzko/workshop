package iits.workshop.htmx;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public Page<Product> searchProducts(String search, String category, int page, String sortBy, String direction) {
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction)
            ? Sort.Direction.DESC
            : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, 10, Sort.by(sortDirection, sortBy));

        boolean hasSearch = search != null && !search.trim().isEmpty();
        boolean hasCategory = category != null && !category.trim().isEmpty();

        if (hasSearch && hasCategory) {
            return productRepository.findByNameContainingIgnoreCaseAndCategory(search.trim(), category.trim(), pageable);
        } else if (hasSearch) {
            return productRepository.findByNameContainingIgnoreCase(search.trim(), pageable);
        } else if (hasCategory) {
            return productRepository.findByCategory(category.trim(), pageable);
        }

        return productRepository.findAll(pageable);
    }

    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<String> getAllCategories() {
        return productRepository.findDistinctCategories();
    }
}
