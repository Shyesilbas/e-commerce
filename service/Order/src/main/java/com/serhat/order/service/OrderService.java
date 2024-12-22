package com.serhat.order.service;

import com.serhat.order.client.CustomerClient;
import com.serhat.order.client.ProductClient;
import com.serhat.order.dto.object.*;
import com.serhat.order.dto.requests.OrderRequest;
import com.serhat.order.dto.responses.*;
import com.serhat.order.entity.Order;
import com.serhat.order.entity.OrderProduct;
import com.serhat.order.entity.Status;
import com.serhat.order.exception.CustomerNotFoundException;
import com.serhat.order.exception.OrderCannotBeCancelledException;
import com.serhat.order.exception.ProductNotFoundException;
import com.serhat.order.repository.OrderProductRepository;
import com.serhat.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    private final CustomerClient customerClient;
    private final ProductClient productClient;



    private String getTokenFromPrincipal(Principal principal) {
        if (principal instanceof JwtAuthenticationToken token) {
            return token.getToken().getTokenValue();
        }
        return null;
    }

    private String authHeader(Principal principal){
        if (principal instanceof JwtAuthenticationToken token) {
            return "Bearer "+token.getToken().getTokenValue();
        }
        return null;
    }

    private CustomerDto validateCustomer(Principal principal) {
        String token = getTokenFromPrincipal(principal);
        if (token == null) {
            throw new RuntimeException("No authorization token found.");
        }
        CustomerDto customer = customerClient.customerInfo("Bearer " + token);
        if (customer == null) {
            throw new CustomerNotFoundException("Customer not found.");
        }
        return customer;
    }

    public AddressDTO findAddressById(Integer addressId,Principal p) {
        return customerClient.addressInfo(addressId,authHeader(p));
    }


    private void validateAddress(Integer addressId, String authorizationHeader) {
        AddressDTO address = customerClient.addressInfo(addressId, authorizationHeader);
        if (address == null) {
            throw new RuntimeException("Address not found for the provided address ID!");
        }

        List<AddressDTO> customerAddresses = customerClient.addressInfoById(authorizationHeader);
        boolean addressExists = customerAddresses.stream()
                .anyMatch(addr -> addr.addressId().equals(addressId));

        if (!addressExists) {
            throw new RuntimeException("Check your address Id's!");
        }
    }


    private List<OrderProductResponse> getOrderProductResponses(List<OrderProduct> orderProducts) {
        return orderProducts.stream()
                .map(product -> new OrderProductResponse(
                        product.getProductName(),
                        product.getProductCode(),
                        product.getQuantity(),
                        product.getTotalPrice()
                ))
                .toList();
    }

    private Order findOrderById(Integer id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found by order id: " + id));
    }

    private List<OrderProduct> orderProducts(Integer id){
        return orderProductRepository.findByOrderId(id);
    }

    @Transactional
    public OrderPlacedResponse placeOrder(Principal principal, OrderRequest request) {
        CustomerDto customer = validateCustomer(principal);
        String token = getTokenFromPrincipal(principal);
        String authorizationHeader = "Bearer " + token;

        Order order = Order.builder()
                .customerId(customer.customerId())
                .quantity(0)
                .totalPrice(BigDecimal.ZERO)
                .orderDate(LocalDateTime.now())
                .status(Status.PENDING)
                .addressId(request.addressId())
                .build();

        order = orderRepository.save(order);

        BigDecimal totalPrice = BigDecimal.ZERO;
        int totalQuantity = 0;

        validateAddress(request.addressId(), authorizationHeader);

        List<OrderProductResponse> orderProductResponses = new ArrayList<>();
        for (int i = 0; i < request.productCodes().size(); i++) {
            String productCode = request.productCodes().get(i);
            int quantity = request.quantities().get(i);

            ProductDTO product = productClient.productInfo(productCode);
            if (product == null) {
                throw new ProductNotFoundException("Product with code " + productCode + " not found!");
            }

            BigDecimal productTotalPrice = product.price().multiply(BigDecimal.valueOf(quantity));

            OrderProduct orderProduct = OrderProduct.builder()
                    .order(order)
                    .productName(product.name())
                    .productCode(productCode)
                    .quantity(quantity)
                    .totalPrice(productTotalPrice)
                    .build();

            orderProductRepository.save(orderProduct);
            productClient.updateProductQuantity(productCode, quantity, authorizationHeader);
            customerClient.updateTotalOrders(customer.customerId(), 1, authorizationHeader);

            totalPrice = totalPrice.add(productTotalPrice);
            totalQuantity += quantity;
            orderProductResponses.add(new OrderProductResponse(product.name(), productCode, quantity, productTotalPrice));
        }

        order.setTotalPrice(totalPrice);
        order.setQuantity(totalQuantity);
        order.setStatus(Status.COMPLETED);
        orderRepository.save(order);

        return new OrderPlacedResponse(
                "Your order placed successfully with order id: " + order.getId(),
                customer.email(),
                order.getOrderDate(),
                orderProductResponses,
                totalPrice,
                customerClient.addressInfo(request.addressId(), authorizationHeader)
        );
    }

    public List<OrderHistoryResponse> orderHistory(Principal principal) {
        CustomerDto customer = validateCustomer(principal);
        List<Order> orders = orderRepository.findByCustomerId(customer.customerId());
        if (orders.isEmpty()) {
            throw new RuntimeException("No orders found for the customer.");
        }

        List<OrderHistoryResponse> orderHistoryResponses = new ArrayList<>();
        for (Order order : orders) {
            List<OrderProduct> orderProducts = orderProducts(order.getId());
            orderHistoryResponses.add(new OrderHistoryResponse(
                    order.getId(),
                    order.getQuantity(),
                    order.getTotalPrice(),
                    order.getOrderDate(),
                    order.getStatus(),
                    order.getAddressId(),
                    getOrderProductResponses(orderProducts)
            ));
        }
        return orderHistoryResponses;
    }

    public OrderDetailsResponse orderDetails(Principal principal, Integer id) {
        validateCustomer(principal);
        Order order = findOrderById(id);
        List<OrderProduct> orderProducts = orderProducts(id);
        return new OrderDetailsResponse(
                order.getId(),
                order.getQuantity(),
                order.getTotalPrice(),
                order.getOrderDate(),
                order.getStatus(),
                customerClient.addressInfo(order.getAddressId(), authHeader(principal)),
                getOrderProductResponses(orderProducts)
        );
    }

    public List<OrderDetailsResponse> ordersBetweenTimeRange(Principal principal, LocalDateTime from, LocalDateTime to) {
        CustomerDto customer = validateCustomer(principal);
        List<Order> orders = orderRepository.findByCustomerIdAndOrderDateBetweenAndStatus(
                customer.customerId(),
                from,
                to,
                Status.COMPLETED
        );


        return orders.stream()
                .map(order -> new OrderDetailsResponse(
                        order.getId(),
                        order.getQuantity(),
                        order.getTotalPrice(),
                        order.getOrderDate(),
                        order.getStatus(),
                        customerClient.addressInfo(order.getAddressId(), authHeader(principal)),
                        getOrderProductResponses(orderProducts(order.getId()))
                ))
                .toList();
    }

    public CancelOrderResponse cancelOrder (Principal p, Integer id){
        CustomerDto customer = validateCustomer(p);
        Order order = findOrderById(id);
        List<OrderProduct> orderProduct = orderProducts(order.getId());
        LocalDateTime orderDate = order.getOrderDate();
        LocalDateTime cancelRequestDate = LocalDateTime.now();
        if(orderDate.plusHours(1).isBefore(cancelRequestDate)){
            throw new OrderCannotBeCancelledException("You have just 1 hour to cancel order.");
        }
        for (OrderProduct product : orderProduct) {
            String productCode = product.getProductCode();
            int quantity = product.getQuantity();

            productClient.updateProductQuantity(productCode, -quantity, authHeader(p));
        }
        customerClient.updateTotalOrders(customer.customerId(),-1,authHeader(p));
        order.setStatus(Status.CANCELLED);
        orderRepository.save(order);
        BigDecimal refundFee = order.getTotalPrice();

        return new CancelOrderResponse(
                "Order Cancelled SUCCESSFULLY",
                refundFee,
                cancelRequestDate
        );
    }




}
