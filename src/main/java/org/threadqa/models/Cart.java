package org.threadqa.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Cart {
    private Integer id;
    private Integer userId;
    private String date;
    private String description;
    private List<CartProduct> cartProduct;
    private Integer __v;
}
