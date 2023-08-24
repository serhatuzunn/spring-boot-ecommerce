package com.anexcode.ecommerce.dto;

import com.anexcode.ecommerce.entity.Address;
import com.anexcode.ecommerce.entity.Customer;
import com.anexcode.ecommerce.entity.Order;
import com.anexcode.ecommerce.entity.OrderItem;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class Purchase {

    private Customer customer;

    private Address shippingAddress;

    private Address billingAddress;

    private Order order;

    private Set<OrderItem> orderItems;
}
