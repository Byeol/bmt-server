package com.bringmethere.domain;

import com.bringmethere.orders.OrderRepository;
import com.bringmethere.orders.ProductOrder;
import com.bringmethere.products.Product;
import com.bringmethere.products.ProductRepository;
import com.bringmethere.reviews.Review;
import com.bringmethere.reviews.ReviewRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.HashMap;
import java.util.Map;

import static com.bringmethere.domain.OrderApiDocumentation.API_URL_ORDERS;
import static com.bringmethere.domain.OrderApiDocumentation.getSampleOrder;
import static com.bringmethere.domain.ProductApiDocumentation.API_URL_PRODUCTS;
import static com.bringmethere.domain.ProductApiDocumentation.getSampleProduct;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ReviewApiDocumentation extends AbstractApiDocumentation {

    static final String API_URL_REVIEWS = "/api/reviews";

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Before
    public void initialize() {
        reviewRepository.deleteAll();
        orderRepository.deleteAll();
        productRepository.deleteAll();
        super.initialize();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    public void productReviewsListExample() throws Exception {
        Product product = createProduct("제주도 여행", "즐겁고 신나고 행복한 3박 4일 제주도 여행 상품입니다.");
        createOrder(ProductOrder.Status.APPROVED, product);

        createReview("즐거운 여행", "브링거와 함께한 제주도 여행은 정말 즐거웠어요!", product);
        createReview("신나는 여행", "브링거와 함께한 제주도 여행은 정말 신났어요!", product);
        createReview("행복한 여행", "브링거와 함께한 제주도 여행은 정말 행복했어요!", product);

        performGet(API_URL_PRODUCTS + "/" + product.getId() + "/reviews")
                .andDo(
                        document("product-reviews-list-example",
                                links(
                                        linkWithRel("self").description("Canonical link for this resource")
                                ),
                                responseFields(
                                        fieldWithPath("_embedded.reviews").description("An array of <<resources-review, Review resources>>"),
                                        fieldWithPath("_links").description("<<resources-tags-list-links, Links>> to other resources")
                                )
                        )
                );
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    public void reviewsCreateExample() throws Exception {
        Map<String, Object> product = getSampleProduct();
        String productLocation = getLocation(API_URL_PRODUCTS, product);

        Map<String, Object> order = getSampleOrder(productLocation);
        String orderLocation = getLocation(API_URL_ORDERS, order);

        Map<String, Object> review = getSampleReview(productLocation);

        performPost(API_URL_REVIEWS, review)
                .andDo(
                        document("reviews-create-example",
                                requestFields(
                                        fieldWithPath("title").description("The title of the review"),
                                        fieldWithPath("content").description("The content of the review"),
                                        fieldWithPath("product").description("The product of the review")
                                )
                        )
                );
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    public void reviewGetExample() throws Exception {
        Map<String, Object> product = getSampleProduct();
        String productLocation = getLocation(API_URL_PRODUCTS, product);

        Map<String, Object> order = getSampleOrder(productLocation);
        String orderLocation = getLocation(API_URL_ORDERS, order);

        Map<String, Object> review = getSampleReview(productLocation);
        String reviewLocation = getLocation(API_URL_REVIEWS, review);

        performGet(reviewLocation)
                .andExpect(jsonPath("title", is(review.get("title"))))
                .andExpect(jsonPath("_links.self.href", is(reviewLocation)))
                .andExpect(jsonPath("_embedded.product._links.self.href", startsWith(productLocation)))
                .andDo(
                        document("review-get-example",
                                links(
                                        linkWithRel("self").description("Canonical link for this <<resources-review,review>>"),
                                        linkWithRel("review").description("This <<resources-review,review>>"),
                                        linkWithRel("product").description("The <<resources-product,product>> that have this review"),
                                        linkWithRel("comments").description("The <<resources-comments,comments>> of this review"),
                                        linkWithRel("createdBy").description("The user who created"),
                                        linkWithRel("lastModifiedBy").description("The user who last modified")
                                ),
                                responseFields(
                                        fieldWithPath("title").description("The title of the review"),
                                        fieldWithPath("content").description("The content of the review"),
                                        fieldWithPath("createdDate").description("The created date of the product"),
                                        fieldWithPath("lastModifiedDate").description("The last modified date of the product"),
                                        fieldWithPath("_embedded.product").description("The <<resources-product,product>> that have this review"),
                                        fieldWithPath("_embedded.createdBy").description("The user who created"),
                                        fieldWithPath("_embedded.lastModifiedBy").description("The user who last modified"),
                                        fieldWithPath("_links").description("<<resources-review-links,Links>> to other resources")
                                )
                        )
                );

        performGet(productLocation + "/reviews")
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.reviews[0]._links.self.href", is(reviewLocation)));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    public void reviewUpdateExample() throws Exception {
        Map<String, Object> product = getSampleProduct();
        String productLocation = getLocation(API_URL_PRODUCTS, product);

        Map<String, Object> order = getSampleOrder(productLocation);
        String orderLocation = getLocation(API_URL_ORDERS, order);

        Map<String, Object> review = getSampleReview(productLocation);
        String reviewLocation = getLocation(API_URL_REVIEWS, review);

        performGet(reviewLocation)
                .andExpect(status().isOk())
                .andExpect(jsonPath("title", is(review.get("title"))))
                .andExpect(jsonPath("content", is(review.get("content"))))
                .andExpect(jsonPath("_links.self.href", is(reviewLocation)))
                .andExpect(jsonPath("_embedded.product._links.self.href", startsWith(productLocation)));

        Map<String, Object> reviewUpdate = new HashMap<>();
        reviewUpdate.put("title", "편집된 즐거운 여행");

        performPatch(reviewLocation, reviewUpdate)
                .andDo(
                        document("review-update-example",
                                requestFields(
                                        fieldWithPath("title").description("The title of the review").type(JsonFieldType.STRING).optional()
                                )
                        )
                );

        performGet(reviewLocation)
                .andExpect(jsonPath("title", is(reviewUpdate.get("title"))))
                .andExpect(jsonPath("_links.self.href", is(reviewLocation)));
    }

    static Map<String, Object> getSampleReview(String productLocation) {
        Map<String, Object> review = new HashMap<>();
        review.put("title", "즐거운 여행");
        review.put("content", "브링거와 함께한 제주도 여행은 정말 즐거웠어요!");
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

    private Review createReview(String title, String content, Product product) {
        Review review = new Review();
        review.setTitle(title);
        review.setContent(content);
        review.setProduct(product);

        return reviewRepository.save(review);
    }
}