package iits.workshop.htmx.view;

import iits.workshop.htmx.model.CarModel;
import iits.workshop.htmx.model.Order;
import iits.workshop.htmx.service.OrderService;
import iits.workshop.htmx.service.SessionService;
import iits.workshop.htmx.view.model.CustomizationForm;
import iits.workshop.htmx.view.model.DeliveryInformationForm;
import iits.workshop.htmx.view.model.PaymentInformationForm;
import iits.workshop.htmx.view.model.ProductSelectionForm;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final SessionService sessionService;

    @GetMapping
    public String redirectToStep1() {
        return "redirect:/order/step1";
    }

    // Step 1: Product Selection
    @GetMapping("/step1")
    public String showStep1(Model model, HttpSession session) {
        Order order = sessionService.getOrCreateOrder(session);

        ProductSelectionForm form = ProductSelectionForm.fromOrder(order);
        model.addAttribute("productSelectionForm", form);
        model.addAttribute("makes", orderService.getAllMakes());
        model.addAttribute("currentStep", 1);
        return "step1";
    }

    @PostMapping("/step1")
    public String processStep1(@Valid @ModelAttribute("productSelectionForm") ProductSelectionForm productSelectionForm,
                               BindingResult result,
                               Model model,
                               HttpSession session) {
        if (result.hasErrors()) {
            model.addAttribute("makes", orderService.getAllMakes());
            model.addAttribute("currentStep", 1);
            return "step1";
        }

        Order order = sessionService.getOrder(session);
        orderService.updateProductSelection(order, productSelectionForm);

        return "redirect:/order/step2";
    }

    // Step 2: Customization
    @GetMapping("/step2")
    public String showStep2(Model model, HttpSession session) {
        Order order = sessionService.getOrder(session);
        if (!orderService.isProductSelectionComplete(order)) {
            return "redirect:/order/step1";
        }

        CustomizationForm form = CustomizationForm.fromOrder(order);
        model.addAttribute("customizationForm", form);
        model.addAttribute("currentStep", 2);
        return "step2";
    }

    @PostMapping("/step2")
    public String processStep2(@Valid @ModelAttribute("customizationForm") CustomizationForm customizationForm,
                               BindingResult result,
                               Model model,
                               HttpSession session) {
        if (result.hasErrors()) {
            model.addAttribute("currentStep", 2);
            return "step2";
        }

        Order order = sessionService.getOrder(session);
        orderService.updateCustomization(order, customizationForm);

        return "redirect:/order/step3";
    }

    // Step 3: Delivery Information
    @GetMapping("/step3")
    public String showStep3(Model model, HttpSession session) {
        Order order = sessionService.getOrder(session);
        if (!orderService.isCustomizationComplete(order)) {
            return "redirect:/order/step1";
        }

        DeliveryInformationForm form = DeliveryInformationForm.fromOrder(order);
        model.addAttribute("deliveryInformationForm", form);
        model.addAttribute("currentStep", 3);
        return "step3";
    }

    @PostMapping("/step3")
    public String processStep3(@Valid @ModelAttribute("deliveryInformationForm") DeliveryInformationForm deliveryInformationForm,
                               BindingResult result,
                               Model model,
                               HttpSession session) {
        if (result.hasErrors()) {
            model.addAttribute("currentStep", 3);
            return "step3";
        }

        Order order = sessionService.getOrder(session);
        orderService.updateDeliveryInformation(order, deliveryInformationForm);

        return "redirect:/order/step4";
    }

    // Step 4: Payment
    @GetMapping("/step4")
    public String showStep4(Model model, HttpSession session) {
        Order order = sessionService.getOrder(session);
        if (!orderService.isDeliveryInformationComplete(order)) {
            return "redirect:/order/step1";
        }

        PaymentInformationForm form = PaymentInformationForm.fromOrder(order);
        model.addAttribute("paymentInformationForm", form);
        model.addAttribute("currentStep", 4);
        return "step4";
    }

    @PostMapping("/step4")
    public String processStep4(@Valid @ModelAttribute("paymentInformationForm") PaymentInformationForm paymentInformationForm,
                               BindingResult result,
                               Model model,
                               HttpSession session) {
        if (result.hasErrors()) {
            model.addAttribute("currentStep", 4);
            return "step4";
        }

        Order order = sessionService.getOrder(session);
        orderService.updatePaymentInformation(order, paymentInformationForm);

        return "redirect:/order/step5";
    }

    // Step 5: Confirmation
    @GetMapping("/step5")
    public String showStep5(Model model, HttpSession session) {
        Order order = sessionService.getOrder(session);
        if (!orderService.isPaymentInformationComplete(order)) {
            return "redirect:/order/step1";
        }

        model.addAttribute("order", order);
        model.addAttribute("currentStep", 5);
        return "step5";
    }

    @PostMapping("/step5")
    public String processStep5(HttpSession session) {
        Order order = sessionService.getOrder(session);
        Order savedOrder = orderService.confirmOrder(order);
        sessionService.removeOrder(session);
        return "redirect:/order/success?orderId=" + savedOrder.getId();
    }

    @GetMapping("/success")
    public String showSuccess(@RequestParam Long orderId, Model model) {
        Order order = orderService.getOrderById(orderId);
        model.addAttribute("order", order);
        return "success";
    }

    // API endpoint to get models by make
    @GetMapping("/api/models")
    @ResponseBody
    public List<CarModel> getModelsByMake(@RequestParam Long makeId) {
        return orderService.getModelsByMakeId(makeId);
    }
}
