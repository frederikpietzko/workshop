package iits.workshop.htmx.view.model;

import iits.workshop.htmx.model.Order;
import jakarta.validation.constraints.NotBlank;

public record PaymentInformationForm(
        @NotBlank(message = "Payment method is required")
        String paymentMethod,

        @NotBlank(message = "Billing address is required")
        String billingAddress
) {
    public static PaymentInformationForm fromOrder(Order order) {
        if (order == null) {
            return new PaymentInformationForm(null, null);
        }
        return new PaymentInformationForm(
                order.getPaymentMethod(),
                order.getBillingAddress()
        );
    }
}
