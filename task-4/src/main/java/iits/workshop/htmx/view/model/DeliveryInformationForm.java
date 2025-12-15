package iits.workshop.htmx.view.model;

import iits.workshop.htmx.model.Order;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record DeliveryInformationForm(
        @NotBlank(message = "Delivery address is required")
        String deliveryAddress,

        @NotBlank(message = "Contact name is required")
        String contactName,

        @NotBlank(message = "Contact email is required")
        @Email(message = "Please provide a valid email")
        String contactEmail,

        @NotBlank(message = "Contact phone is required")
        String contactPhone
) {
    public static DeliveryInformationForm fromOrder(Order order) {
        if (order == null) {
            return new DeliveryInformationForm(null, null, null, null);
        }
        return new DeliveryInformationForm(
                order.getDeliveryAddress(),
                order.getContactName(),
                order.getContactEmail(),
                order.getContactPhone()
        );
    }
}
