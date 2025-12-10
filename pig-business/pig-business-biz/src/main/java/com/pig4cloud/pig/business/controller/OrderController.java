package com.pig4cloud.pig.business.controller;

import com.pig4cloud.pig.business.api.dto.OrderDTO;
import com.pig4cloud.pig.common.security.annotation.HasPermission;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {

    @GetMapping("/{id}")
    @HasPermission({"order:view"})
    public OrderDTO get(@PathVariable Long id) {
        OrderDTO dto = new OrderDTO();
        dto.setId(id);
        dto.setName("order-" + id);
        return dto;
    }
}

