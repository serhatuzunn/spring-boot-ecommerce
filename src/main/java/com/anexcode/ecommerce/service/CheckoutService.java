package com.anexcode.ecommerce.service;

import com.anexcode.ecommerce.dto.PaymentInfo;
import com.anexcode.ecommerce.dto.Purchase;
import com.anexcode.ecommerce.dto.PurchaseResponse;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

public interface CheckoutService {

    PurchaseResponse placeOrder(Purchase purchase);

    PaymentIntent createPaymentIntent(PaymentInfo paymentInfo) throws StripeException;

}
