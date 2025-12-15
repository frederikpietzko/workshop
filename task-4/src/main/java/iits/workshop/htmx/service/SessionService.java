package iits.workshop.htmx.service;

import iits.workshop.htmx.model.Order;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SessionService {

    private static final String ORDER_SESSION_KEY = "order";

    public Order getOrCreateOrder(HttpSession session) {
        return Optional.ofNullable(getOrder(session))
                .orElseGet(() -> {
                    Order newOrder = new Order();
                    setOrder(session, newOrder);
                    return newOrder;
                });
    }

    public Order getOrder(HttpSession session) {
        return Optional.ofNullable((Order) session.getAttribute(ORDER_SESSION_KEY))
                .orElseGet(Order::new);
    }

    public void setOrder(HttpSession session, Order order) {
        session.setAttribute(ORDER_SESSION_KEY, order);
    }

    public void removeOrder(HttpSession session) {
        session.removeAttribute(ORDER_SESSION_KEY);
    }

    public boolean hasOrder(HttpSession session) {
        return session.getAttribute(ORDER_SESSION_KEY) != null;
    }
}
