package tacos.controllers;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import tacos.models.Taco;
import tacos.models.TacoOrder;
import tacos.properties.OrderProps;
import tacos.repositories.OrderRepository;
import tacos.security.User;

import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/orders")
@SessionAttributes("tacoOrder")
public class OrderController {

    private final OrderProps orderProps;

    private final OrderRepository orderRepository;

    @Autowired
    public OrderController(OrderRepository orderRepository, OrderProps orderProps) {
        this.orderRepository = orderRepository;
        this.orderProps = orderProps;
    }

    @GetMapping("/current")
    public String orderForm() {
        return "orderForm";
    }

    @PostMapping
    public String processOrder(@Valid TacoOrder tacoOrder, Errors errors, SessionStatus sessionStatus) {
        if (errors.hasErrors()) {
            return "orderForm";
        }

        this.orderRepository.save(tacoOrder);
        sessionStatus.setComplete();

        return "redirect:/";
    }

    @GetMapping
    public String ordersForUser(@AuthenticationPrincipal User user, Model model) {
        Pageable pageable = PageRequest.of(0, orderProps.getPageSize());

        model.addAttribute("orders", orderRepository.findByDeliveryNameOrderByCreatedAtDesc(user.getFullname(), pageable));

        return "orderList";
    }

    @PutMapping(value = "/{id}", consumes = "application/json")
    public TacoOrder putTacoOrder(@PathVariable("id") Long id, @RequestBody TacoOrder tacoOrder) {
        tacoOrder.setId(id);
        return orderRepository.save(tacoOrder);
    }

    @PatchMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<TacoOrder> patchTacoOrder(@PathVariable("id") Long id, @RequestBody TacoOrder tacoOrder) {
        Optional<TacoOrder> optOrder = orderRepository.findById(id);
        return optOrder.map(
                value -> {
                    orderRepository.save(value);
                    return new ResponseEntity<>(value, HttpStatus.OK);
                }
        ).orElseGet(
                () -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND)
        );
    }
}
