package iits.workshop.htmx;

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxResponseHeader;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.FragmentsRendering;

import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalStateException.class)
    public View handleIllegalStateException(IllegalStateException ex, Model model) {
        model.addAttribute("errors", List.of(ex.getMessage()));
        return FragmentsRendering
                .fragment("fragments/global-error :: globalError")
                .header(HtmxResponseHeader.HX_RETARGET.getValue(), "#global-error-container")
                .build();
    }
}
