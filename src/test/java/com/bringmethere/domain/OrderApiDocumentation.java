package com.bringmethere.domain;

import com.bringmethere.orders.OrderRepository;
import com.bringmethere.orders.ProductOrder;
import com.bringmethere.products.Product;
import com.bringmethere.products.ProductRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.HashMap;
import java.util.Map;

import static com.bringmethere.domain.ProductApiDocumentation.API_URL_PRODUCTS;
import static com.bringmethere.domain.ProductApiDocumentation.getSampleProduct;
import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OrderApiDocumentation extends AbstractApiDocumentation {

    static final String API_URL_ORDERS = "/api/orders";

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Before
    public void initialize() {
        orderRepository.deleteAll();
        productRepository.deleteAll();
        super.initialize();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    public void productOrdersListExample() throws Exception {
        Product product = createProduct("제주도 여행", "즐겁고 신나고 행복한 3박 4일 제주도 여행 상품입니다.");
        createOrder(ProductOrder.Status.APPROVED, product);

        performGet(API_URL_PRODUCTS + "/" + product.getId() + "/orders")
                .andDo(
                        document("product-orders-list-example",
                                links(
                                        linkWithRel("self").description("Canonical link for this resource")
                                ),
                                responseFields(
                                        fieldWithPath("_embedded.productOrders").description("An array of <<resources-order, Order resources>>"),
                                        fieldWithPath("_links").description("<<resources-tags-list-links, Links>> to other resources")
                                )
                        )
                );
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    public void ordersCreateExample() throws Exception {
        Map<String, Object> product = getSampleProduct();
        String productLocation = getLocation(API_URL_PRODUCTS, product);

        Map<String, Object> order = getSampleOrder(productLocation);

        performPost(API_URL_ORDERS, order)
                .andDo(
                        document("orders-create-example",
                                requestFields(
                                        fieldWithPath("status").description("The status of the order"),
                                        fieldWithPath("product").description("The product of the review")
                                )
                        )
                );
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    public void orderGetExample() throws Exception {
        Map<String, Object> product = getSampleProduct();
        String productLocation = getLocation(API_URL_PRODUCTS, product);

        Map<String, Object> order = getSampleOrder(productLocation);
        String orderLocation = getLocation(API_URL_ORDERS, order);

        performGet(orderLocation)
                .andExpect(jsonPath("status", is(order.get("status"))))
                .andExpect(jsonPath("_links.self.href", is(orderLocation)))
                .andDo(
                        document("order-get-example",
                                links(
                                        linkWithRel("self").description("Canonical link for this <<resources-order,order>>"),
                                        linkWithRel("productOrder").description("This <<resources-order,order>>"),
                                        linkWithRel("product").description("The <<resources-product,product>> that have this order"),
                                        linkWithRel("createdBy").description("The user who created"),
                                        linkWithRel("lastModifiedBy").description("The user who last modified")
                                ),
                                responseFields(
                                        fieldWithPath("user").description("The user of the order"),
                                        fieldWithPath("status").description("The status of the order"),
                                        fieldWithPath("createdDate").description("The created date of the order"),
                                        fieldWithPath("lastModifiedDate").description("The last modified date of the order"),
                                        fieldWithPath("_embedded.product").description("The <<resources-product,product>> that have this order"),
                                        fieldWithPath("_embedded.createdBy").description("The user who created"),
                                        fieldWithPath("_embedded.lastModifiedBy").description("The user who last modified"),
                                        fieldWithPath("_links").description("<<resources-tags-list-links, Links>> to other resources")
                                )
                        )
                );
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    public void orderUpdateExample() throws Exception {
        Map<String, Object> product = getSampleProduct();
        String productLocation = getLocation(API_URL_PRODUCTS, product);

        Map<String, Object> order = getSampleOrder(productLocation);
        String orderLocation = getLocation(API_URL_ORDERS, order);

        performGet(orderLocation)
                .andExpect(jsonPath("status", is(order.get("status"))))
                .andExpect(jsonPath("_links.self.href", is(orderLocation)));

        Map<String, Object> orderUpdate = new HashMap<>();
        orderUpdate.put("status", "PENDING");

        performPatch(orderLocation, orderUpdate)
                .andDo(
                        document("order-update-example",
                                requestFields(
                                        fieldWithPath("status").description("The status of the order").type(JsonFieldType.STRING).optional()
                                )
                        )
                );

        performGet(orderLocation)
                .andExpect(status().isOk())
                .andExpect(jsonPath("status", is(orderUpdate.get("status"))))
                .andExpect(jsonPath("_links.self.href", is(orderLocation)));
    }

    static Map<String, Object> getSampleOrder(String productLocation) {
        Map<String, Object> review = new HashMap<>();
        review.put("status", "APPROVED");
        review.put("product", productLocation);
        return review;
    }

    private Product createProduct(String name, String description) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);

        return productRepository.save(product);
    }

    private ProductOrder createOrder(ProductOrder.Status status, Product product) {
        ProductOrder order = new ProductOrder();
        order.setStatus(status);
        product.addOrder(order);

        return orderRepository.save(order);
    }
}