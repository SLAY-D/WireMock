package org.threadqa.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.threadqa.models.Cart;
import org.threadqa.models.Product;

import java.util.List;

@RestController
@RequestMapping("/api/carts")
public class CartController {
    @Autowired
    private WebClient webClient;

    @GetMapping(value = {"/{id}"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public Cart getCart(@PathVariable Integer id){
        return webClient.get().uri(x -> x
                    .path("/carts/{id}")
                    .build(id))
                .retrieve()
                .bodyToMono(Cart.class)
                .block();
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Cart> getCarts() {
        return webClient.get().uri("/carts")
                .retrieve()
                .bodyToFlux(Cart.class)
                .collectList()
                .block();
    }

    @GetMapping(value = "/count", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getCount(){
        int size = getCarts().size();
        return String.format("{\"count\" : %d}", size);
    }
}
