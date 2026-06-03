package com.trustcart.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class CartService {
    @SuppressWarnings("unchecked")
    public Map<Long, Integer> getCart(HttpSession session) {
        Object cart = session.getAttribute("cart");
        if (cart instanceof Map<?, ?> map) {
            return (Map<Long, Integer>) map;
        }
        Map<Long, Integer> newCart = new HashMap<>();
        session.setAttribute("cart", newCart);
        return newCart;
    }

    public int countItems(HttpSession session) {
        return getCart(session).values().stream().mapToInt(Integer::intValue).sum();
    }

    public void clear(HttpSession session) {
        session.removeAttribute("cart");
    }
}
