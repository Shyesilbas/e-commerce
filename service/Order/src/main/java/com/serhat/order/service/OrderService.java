package com.serhat.order.service;

import com.serhat.order.client.CustomerClient;
import com.serhat.order.client.ProductClient;
import com.serhat.order.dto.object.*;
import com.serhat.order.dto.requests.OrderRequest;
import com.serhat.order.dto.requests.PlaceOrderRequestDTO;
import com.serhat.order.dto.responses.OrderHistoryResponse;
import com.serhat.order.dto.responses.OrderPlacedResponse;
import com.serhat.order.dto.responses.OrderProductResponse;
import com.serhat.order.entity.Order;
import com.serhat.order.entity.OrderProduct;
import com.serhat.order.entity.Status;
import com.serhat.order.exception.CustomerNotFoundException;
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
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    private final CustomerClient customerClient;
    private final ProductClient productClient;

    public String getTokenFromPrincipal(Principal principal) {
        if (principal instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken token = (JwtAuthenticationToken) principal;
            return token.getToken().getTokenValue();
        }
        return null;
    }


    @Transactional
    public OrderPlacedResponse placeOrder(Principal principal, OrderRequest request) {
        String token = getTokenFromPrincipal(principal);
        if (token == null) {
            throw new RuntimeException("No auth.");
        }

        CustomerDto customer = customerClient.customerInfo("Bearer " + token);
        if (customer == null) {
            throw new CustomerNotFoundException("Customer not found");
        }
        int customerId = customer.customerId();
        String authorizationHeader = "Bearer " + token;

        Order order = Order.builder()
                .customerId(customerId)
                .quantity(0)
                .totalPrice(BigDecimal.ZERO)
                .orderDate(LocalDateTime.now())
                .status(Status.PENDING)
                .addressId(request.addressId())
                .build();

        order = orderRepository.save(order);

        BigDecimal totalPrice = BigDecimal.ZERO;
        int totalQuantity = 0;

        AddressDTO address = customerClient.addressInfo(request.addressId(), authorizationHeader);
        if (address == null) {
            throw new RuntimeException("Address not found for the provided address ID!");
        }

        List<AddressDTO> customerAddresses = customerClient.addressInfoById(authorizationHeader);
        boolean addressExists = customerAddresses.stream()
                .anyMatch(addr -> addr.addressId().equals(address.addressId()));

        if (!addressExists) {
            throw new RuntimeException("Check your address Id's!");
        }

        List<OrderProductDto> orderProductDtos = new ArrayList<>();
        for (int i = 0; i < request.productCodes().size(); i++) {
            String productCode = request.productCodes().get(i);
            int quantity = request.quantities().get(i);

            productClient.updateProductQuantity(productCode, quantity, authorizationHeader);

            ProductDTO product = productClient.productInfo(productCode);
            if (product == null) {
                throw new ProductNotFoundException("Product with code " + productCode + " not found!");
            }

            BigDecimal productTotalPrice = product.price().multiply(BigDecimal.valueOf(quantity));

            OrderProductDto orderProductDto = new OrderProductDto(product.name(),productCode, quantity, productTotalPrice);

            OrderProduct orderProduct = OrderProduct.builder()
                    .order(order)
                    .productName(product.name())
                    .productCode(productCode)
                    .quantity(quantity)
                    .totalPrice(productTotalPrice)
                    .build();

            orderProductRepository.save(orderProduct);

            customerClient.updateTotalOrders(customerId, 1, authorizationHeader);

            totalPrice = totalPrice.add(productTotalPrice);
            totalQuantity += quantity;
            orderProductDtos.add(orderProductDto);
        }

        order.setTotalPrice(totalPrice);
        order.setQuantity(totalQuantity);
        order.setStatus(Status.COMPLETED);

        Order savedOrder = orderRepository.save(order);

        return new OrderPlacedResponse(
                "Your order placed successfully with order id: " + savedOrder.getId(),
                customer.email(),
                savedOrder.getOrderDate(),
                orderProductDtos,
                savedOrder.getTotalPrice(),
                address
        );
    }


    public List<OrderHistoryResponse> orderHistory(Principal p) {
        String token = getTokenFromPrincipal(p);
        if (token == null) {
            throw new RuntimeException("No authorization token found.");
        }

        CustomerDto customerDto = customerClient.customerInfo("Bearer " + token);
        if (customerDto == null) {
            throw new CustomerNotFoundException("Customer not found.");
        }

        List<Order> orders = orderRepository.findByCustomerId(customerDto.customerId());
        if (orders.isEmpty()) {
            throw new RuntimeException("No orders found for the customer.");
        }

        List<OrderHistoryResponse> orderHistoryResponses = new ArrayList<>();

        for (Order order : orders) {
            List<OrderProduct> orderProducts = orderProductRepository.findByOrderId(order.getId());

            List<OrderProductResponse> productResponses = orderProducts.stream()
                    .map(product -> new OrderProductResponse(
                            product.getProductName(),
                            product.getProductCode(),
                            product.getQuantity(),
                            product.getTotalPrice()
                    ))
                    .toList();

            OrderHistoryResponse response = new OrderHistoryResponse(
                    order.getId(),
                    order.getQuantity(),
                    order.getTotalPrice(),
                    order.getOrderDate(),
                    order.getStatus(),
                    order.getAddressId(),
                    productResponses
            );

            orderHistoryResponses.add(response);
        }

        return orderHistoryResponses;
    }





}
