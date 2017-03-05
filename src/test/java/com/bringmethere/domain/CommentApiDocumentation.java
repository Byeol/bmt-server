package com.bringmethere.domain;

import com.bringmethere.comments.Comment;
import com.bringmethere.comments.CommentRepository;
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
import static com.bringmethere.domain.ReviewApiDocumentation.API_URL_REVIEWS;
import static com.bringmethere.domain.ReviewApiDocumentation.getSampleReview;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CommentApiDocumentation extends AbstractApiDocumentation {

    static final String API_URL_COMMENTS = "/api/comments";

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Before
    public void initialize() {
        commentRepository.deleteAll();
        reviewRepository.deleteAll();
        orderRepository.deleteAll();
        productRepository.deleteAll();
        super.initialize();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    public void reviewCommentsListExample() throws Exception {
        Product product = createProduct("제주도 여행", "즐겁고 신나고 행복한 3박 4일 제주도 여행 상품입니다.");
        createOrder(ProductOrder.Status.APPROVED, product);
        Review review = createReview("즐거운 여행", "브링거와 함께한 제주도 여행은 정말 즐거웠어요!", product);

        createComment("좋은 리뷰입니다!", review);
        createComment("도움이 많이 되었습니다!", review);
        createComment("저도 가고 싶어요!", review);

        performGet(API_URL_REVIEWS + "/" + review.getId() + "/comments")
                .andDo(
                        document("review-comments-list-example",
                                links(
                                        linkWithRel("self").description("Canonical link for this resource")
                                ),
                                responseFields(
                                        fieldWithPath("_embedded.comments").description("An array of <<resources-comment, Comment resources>>"),
                                        fieldWithPath("_links").description("<<resources-tags-list-links, Links>> to other resources")
                                )
                        )
                );
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    public void commentsCreateExample() throws Exception {
        Map<String, Object> product = getSampleProduct();
        String productLocation = getLocation(API_URL_PRODUCTS, product);

        Map<String, Object> order = getSampleOrder(productLocation);
        String orderLocation = getLocation(API_URL_ORDERS, order);

        Map<String, Object> review = getSampleReview(productLocation);
        String reviewLocation = getLocation(API_URL_REVIEWS, review);

        Map<String, Object> comment = getSampleComment(reviewLocation);

        performPost(API_URL_COMMENTS, comment)
                .andDo(
                        document("comments-create-example",
                                requestFields(
                                        fieldWithPath("body").description("The body of the comment"),
                                        fieldWithPath("review").description("The review of the comment")
                                )
                        )
                );
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    public void commentGetExample() throws Exception {
        Map<String, Object> product = getSampleProduct();
        String productLocation = getLocation(API_URL_PRODUCTS, product);

        Map<String, Object> order = getSampleOrder(productLocation);
        String orderLocation = getLocation(API_URL_ORDERS, order);

        Map<String, Object> review = getSampleReview(productLocation);
        String reviewLocation = getLocation(API_URL_REVIEWS, review);

        Map<String, Object> comment = getSampleComment(reviewLocation);
        String commentLocation = getLocation(API_URL_COMMENTS, comment);

        performGet(commentLocation)
                .andExpect(jsonPath("body", is(comment.get("body"))))
                .andExpect(jsonPath("_links.self.href", is(commentLocation)))
                .andExpect(jsonPath("_embedded.review._links.self.href", startsWith(reviewLocation)))
                .andDo(
                        document("comment-get-example",
                                links(
                                        linkWithRel("self").description("Canonical link for this <<resources-comment,comment>>"),
                                        linkWithRel("comment").description("This <<resources-comment,comment>>"),
                                        linkWithRel("review").description("The <<resources-review,review>> that have this comment"),
                                        linkWithRel("createdBy").description("The user who created"),
                                        linkWithRel("lastModifiedBy").description("The user who last modified")
                                ),
                                responseFields(
                                        fieldWithPath("body").description("The body of the comment"),
                                        fieldWithPath("createdDate").description("The created date of the review"),
                                        fieldWithPath("lastModifiedDate").description("The last modified date of the review"),
                                        fieldWithPath("_embedded.review").description("The <<resources-review,review>> that have this comment"),
                                        fieldWithPath("_embedded.createdBy").description("The user who created"),
                                        fieldWithPath("_embedded.lastModifiedBy").description("The user who last modified"),
                                        fieldWithPath("_links").description("<<resources-comment-links,Links>> to other resources")
                                )
                        )
                );

        performGet(reviewLocation + "/comments")
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.comments[0]._links.self.href", is(commentLocation)));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    public void commentUpdateExample() throws Exception {
        Map<String, Object> product = getSampleProduct();
        String productLocation = getLocation(API_URL_PRODUCTS, product);

        Map<String, Object> order = getSampleOrder(productLocation);
        String orderLocation = getLocation(API_URL_ORDERS, order);

        Map<String, Object> review = getSampleReview(productLocation);
        String reviewLocation = getLocation(API_URL_REVIEWS, review);

        Map<String, Object> comment = getSampleComment(reviewLocation);
        String commentLocation = getLocation(API_URL_COMMENTS, comment);

        performGet(commentLocation)
                .andExpect(status().isOk())
                .andExpect(jsonPath("body", is(comment.get("body"))))
                .andExpect(jsonPath("_links.self.href", is(commentLocation)))
                .andExpect(jsonPath("_embedded.review._links.self.href", startsWith(reviewLocation)));

        Map<String, Object> commentUpdate = new HashMap<>();
        commentUpdate.put("body", "즐거운 여행이었습니다!");

        performPatch(commentLocation, commentUpdate)
                .andDo(
                        document("comment-update-example",
                                requestFields(
                                        fieldWithPath("body").description("The body of the comment").type(JsonFieldType.STRING).optional()
                                )
                        )
                );

        performGet(commentLocation)
                .andExpect(jsonPath("body", is(commentUpdate.get("body"))))
                .andExpect(jsonPath("_links.self.href", is(commentLocation)));
    }

    static Map<String, Object> getSampleComment(String reviewLocation) {
        Map<String, Object> comment = new HashMap<>();
        comment.put("body", "좋은 리뷰입니다!");
        comment.put("review", reviewLocation);
        return comment;
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

    private Comment createComment(String body, Review review) {
        Comment comment = new Comment();
        comment.setBody(body);
        comment.setReview(review);

        return commentRepository.save(comment);
    }
}