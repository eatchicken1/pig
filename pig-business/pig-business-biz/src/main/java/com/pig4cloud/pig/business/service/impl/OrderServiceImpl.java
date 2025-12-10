package com.pig4cloud.pig.business.service.impl;

import com.pig4cloud.pig.business.service.OrderService;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {
    @Override
    public String findNameById(Long id) {
        return "order-" + id;
    }
}

