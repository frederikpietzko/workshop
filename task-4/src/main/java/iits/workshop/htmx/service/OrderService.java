package iits.workshop.htmx.service;

import iits.workshop.htmx.view.model.ProductSelectionForm;
import iits.workshop.htmx.view.model.CustomizationForm;
import iits.workshop.htmx.view.model.DeliveryInformationForm;
import iits.workshop.htmx.view.model.PaymentInformationForm;
import iits.workshop.htmx.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final MakeRepository makeRepository;
    private final CarModelRepository modelRepository;
    private final OrderRepository orderRepository;

    public void updateProductSelection(Order order, ProductSelectionForm form) {
        Make make = makeRepository.findById(form.makeId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid make ID"));
        CarModel model = modelRepository.findById(form.modelId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid model ID"));

        order.updateProductSelection(form.productCategory(), make, model, form.quantity());
    }

    public void updateCustomization(Order order, CustomizationForm form) {
        order.updateCustomization(form.color(), form.interior(), form.tireType());
    }

    public void updateDeliveryInformation(Order order, DeliveryInformationForm form) {
        order.updateDeliveryInformation(form.deliveryAddress(), form.contactName(),
                form.contactEmail(), form.contactPhone());
    }

    public void updatePaymentInformation(Order order, PaymentInformationForm form) {
        order.updatePaymentInformation(form.paymentMethod(), form.billingAddress());
    }

    @Transactional
    public Order confirmOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }

        order.confirm();
        return orderRepository.save(order);
    }

    public List<Make> getAllMakes() {
        return makeRepository.findAll();
    }

    public List<CarModel> getModelsByMakeId(Long makeId) {
        return modelRepository.findByMakeId(makeId);
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
    }

    public boolean isProductSelectionComplete(Order order) {
        return order != null && order.getMake() != null && order.getModel() != null
                && order.getProductCategory() != null && order.getQuantity() != null;
    }

    public boolean isCustomizationComplete(Order order) {
        return isProductSelectionComplete(order) && order.getColor() != null
                && order.getInterior() != null && order.getTireType() != null;
    }

    public boolean isDeliveryInformationComplete(Order order) {
        return isCustomizationComplete(order) && order.getDeliveryAddress() != null
                && order.getContactName() != null && order.getContactEmail() != null
                && order.getContactPhone() != null;
    }

    public boolean isPaymentInformationComplete(Order order) {
        return isDeliveryInformationComplete(order) && order.getPaymentMethod() != null
                && order.getBillingAddress() != null;
    }
}
