package com.anexcode.ecommerce.service;

import com.anexcode.ecommerce.dao.CustomerRepository;
import com.anexcode.ecommerce.dto.PaymentInfo;
import com.anexcode.ecommerce.dto.Purchase;
import com.anexcode.ecommerce.dto.PurchaseResponse;
import com.anexcode.ecommerce.entity.Customer;
import com.anexcode.ecommerce.entity.Order;
import com.anexcode.ecommerce.entity.OrderItem;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class CheckoutServiceImpl implements CheckoutService{

    private CustomerRepository customerRepository;

    @Autowired
    public CheckoutServiceImpl(CustomerRepository customerRepository,
                               @Value("${stripe.key.secret}") String secretKey){
        this.customerRepository = customerRepository;

        //initalize stripe api with secret key
        Stripe.apiKey=secretKey;
    }

    @Override
    @Transactional(noRollbackFor = Exception.class)
    public PurchaseResponse placeOrder(Purchase purchase) {
        //retrieve the order info from dto

        Order order = purchase.getOrder();

        //generate tracking number
        String orderTrackingNumber = generateOrderTrackingNumber();
        order.setOrderTrackingNumber(orderTrackingNumber);

        //populate order with orderItems
        Set<OrderItem> orderItems = purchase.getOrderItems();
        orderItems.forEach(item -> order.add(item));

        //populate order with billing and shipping Address
        order.setBillingAddress(purchase.getBillingAddress());
        order.setShippingAddress(purchase.getShippingAddress());

        //populate customer with order
        Customer customer = purchase.getCustomer();

        //check if this is an existing customer
        String theEmail = customer.getEmail();
        Customer foundedCustomer = customerRepository.findByEmail(theEmail);

        if (foundedCustomer != null){
            customer = foundedCustomer;
        }

        customer.add(order);

        //save to the db
        customerRepository.save(customer);

        //return a response
        return new PurchaseResponse(orderTrackingNumber);

    }

    @Override
    public PaymentIntent createPaymentIntent(PaymentInfo paymentInfo) throws StripeException {
        List<String> paymentMethodTypes = new ArrayList<>();
        paymentMethodTypes.add("card");

        Map<String, Object> params = new HashMap<>();
        params.put("amount",paymentInfo.getAmount());
        params.put("currency",paymentInfo.getCurrency());
        params.put("payment_method_types",paymentMethodTypes);
        params.put("description","purchase anex shop");
        params.put("receipt_email",paymentInfo.getReceiptEmail());
        return PaymentIntent.create(params);
    }

    private String generateOrderTrackingNumber(){
        //generate a random UUID number (UUID v4)
        return UUID.randomUUID().toString();
    }











}
