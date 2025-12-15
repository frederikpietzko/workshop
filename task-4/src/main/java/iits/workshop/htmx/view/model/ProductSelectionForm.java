package iits.workshop.htmx.view.model;

import iits.workshop.htmx.model.Order;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductSelectionForm(
        @NotBlank(message = "Product category is required")
        String productCategory,

        @NotNull(message = "Make is required")
        Long makeId,

        @NotNull(message = "Model is required")
        Long modelId,

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        Integer quantity
) {
    public static ProductSelectionForm fromOrder(Order order) {
        if (order == null) {
            return new ProductSelectionForm(null, null, null, null);
        }
        return new ProductSelectionForm(
                order.getProductCategory(),
                order.getMake() != null ? order.getMake().getId() : null,
                order.getModel() != null ? order.getModel().getId() : null,
                order.getQuantity()
        );
    }
}
