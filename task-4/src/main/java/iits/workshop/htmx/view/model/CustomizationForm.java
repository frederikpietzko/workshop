package iits.workshop.htmx.view.model;

import iits.workshop.htmx.model.Order;
import jakarta.validation.constraints.NotBlank;

public record CustomizationForm(
        @NotBlank(message = "Color is required")
        String color,

        @NotBlank(message = "Interior is required")
        String interior,

        @NotBlank(message = "Tire type is required")
        String tireType
) {
    public static CustomizationForm fromOrder(Order order) {
        if (order == null) {
            return new CustomizationForm(null, null, null);
        }
        return new CustomizationForm(
                order.getColor(),
                order.getInterior(),
                order.getTireType()
        );
    }
}
