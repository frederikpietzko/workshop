package iits.workshop.htmx.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Step 1: Product selection
    @NotBlank(message = "Product category is required")
    private String productCategory;

    @ManyToOne
    @JoinColumn(name = "make_id")
    @NotNull(message = "Make is required")
    private Make make;

    @ManyToOne
    @JoinColumn(name = "model_id")
    @NotNull(message = "Model is required")
    private CarModel model;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    // Step 2: Customization
    @NotBlank(message = "Color is required")
    private String color;

    @NotBlank(message = "Interior is required")
    private String interior;

    @NotBlank(message = "Tire type is required")
    private String tireType;

    // Step 3: Delivery
    @NotBlank(message = "Delivery address is required")
    private String deliveryAddress;

    @NotBlank(message = "Contact name is required")
    private String contactName;

    @NotBlank(message = "Contact email is required")
    @Email(message = "Please provide a valid email")
    private String contactEmail;

    @NotBlank(message = "Contact phone is required")
    private String contactPhone;

    // Step 4: Payment
    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    @NotBlank(message = "Billing address is required")
    private String billingAddress;

    // Order status
    private String status = "PENDING";

    // Business methods for updating order state
    public void updateProductSelection(String productCategory, Make make, CarModel model, Integer quantity) {
        this.productCategory = productCategory;
        this.make = make;
        this.model = model;
        this.quantity = quantity;
    }

    public void updateCustomization(String color, String interior, String tireType) {
        this.color = color;
        this.interior = interior;
        this.tireType = tireType;
    }

    public void updateDeliveryInformation(String deliveryAddress, String contactName,
                                         String contactEmail, String contactPhone) {
        this.deliveryAddress = deliveryAddress;
        this.contactName = contactName;
        this.contactEmail = contactEmail;
        this.contactPhone = contactPhone;
    }

    public void updatePaymentInformation(String paymentMethod, String billingAddress) {
        this.paymentMethod = paymentMethod;
        this.billingAddress = billingAddress;
    }

    public void confirm() {
        this.status = "CONFIRMED";
    }
}
