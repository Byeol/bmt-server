package com.bringmethere.domain;

import com.bringmethere.products.Product;
import com.bringmethere.products.ProductRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProductApiDocumentation extends AbstractApiDocumentation {

    static final String API_URL_PRODUCTS = "/api/products";

    @Autowired
    private ProductRepository repository;

    @Before
    public void initialize() {
        repository.deleteAll();
        super.initialize();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    public void productsListExample() throws Exception {
        createProduct("제주도 여행", "즐겁고 신나고 행복한 3박 4일 제주도 여행 상품입니다.");
        createProduct("유럽 여행", "즐겁고 신나고 행복한 7박 8일 유럽 여행 상품입니다.");

        performGet(API_URL_PRODUCTS)
                .andDo(
                        document("products-list-example",
                                links(
                                        linkWithRel("self").description("Canonical link for this resource"),
                                        linkWithRel("profile").description("The ALPS profile for this resource")
                                ),
                                responseFields(
                                        fieldWithPath("_embedded.products").description("An array of <<resources-product, Product resources>>"),
                                        fieldWithPath("_links").description("<<resources-tags-list-links, Links>> to other resources")
                                )
                        )
                );
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    public void productsCreateExample() throws Exception {
        Map<String, Object> product = getSampleProduct();

        performPost(API_URL_PRODUCTS, product)
                .andDo(
                        document("products-create-example",
                                requestFields(
                                        fieldWithPath("name").description("The name of the product"),
                                        fieldWithPath("description").description("The description of the product")
                                )
                        )
                );
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    public void productGetExample() throws Exception {
        Map<String, Object> product = getSampleProduct();
        String productLocation = getLocation(API_URL_PRODUCTS, product);

        performGet(productLocation)
                .andExpect(jsonPath("name", is(product.get("name"))))
                .andExpect(jsonPath("description", is(product.get("description"))))
                .andExpect(jsonPath("_links.self.href", is(productLocation)))
                .andDo(
                        document("product-get-example",
                                links(
                                        linkWithRel("self").description("Canonical link for this <<resources-product,product>>"),
                                        linkWithRel("product").description("This <<resources-product,product>>"),
                                        linkWithRel("createdBy").description("The user who created"),
                                        linkWithRel("lastModifiedBy").description("The user who last modified"),
                                        linkWithRel("reviews").description("The <<resources-reviews,reviews>> of the product"),
                                        linkWithRel("orders").description("The <<resources-orders,orders>> of the product")
                                ),
                                responseFields(
                                        fieldWithPath("name").description("The name of the product"),
                                        fieldWithPath("description").description("The description of the product"),
                                        fieldWithPath("createdDate").description("The created date of the product"),
                                        fieldWithPath("lastModifiedDate").description("The last modified date of the product"),
                                        fieldWithPath("_embedded.createdBy").description("The user who created"),
                                        fieldWithPath("_embedded.lastModifiedBy").description("The user who last modified"),
                                        fieldWithPath("_links").description("<<resources-tags-list-links, Links>> to other resources")
                                )
                        )
                );
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    public void productUpdateExample() throws Exception {
        Map<String, Object> product = getSampleProduct();
        String productLocation = getLocation(API_URL_PRODUCTS, product);

        performGet(productLocation)
                .andExpect(jsonPath("name", is(product.get("name"))))
                .andExpect(jsonPath("description", is(product.get("description"))))
                .andExpect(jsonPath("_links.self.href", is(productLocation)));

        Map<String, Object> productUpdate = new HashMap<>();
        productUpdate.put("name", "아시아 여행 상품");
        productUpdate.put("description", "즐겁고 신나고 행복한 3박 4일 아시아 여행 상품입니다.");

        performPatch(productLocation, productUpdate)
                .andDo(
                        document("product-update-example",
                                requestFields(
                                        fieldWithPath("name").description("The name of the product").type(JsonFieldType.STRING).optional(),
                                        fieldWithPath("description").description("The description of the product").type(JsonFieldType.STRING).optional()
                                )
                        )
                );

        performGet(productLocation)
                .andExpect(status().isOk())
                .andExpect(jsonPath("name", is(productUpdate.get("name"))))
                .andExpect(jsonPath("description", is(productUpdate.get("description"))))
                .andExpect(jsonPath("_links.self.href", is(productLocation)));
    }

    static Map<String, Object> getSampleProduct() {
        Map<String, Object> product = new HashMap<>();
        product.put("name", "제주도 여행");
        product.put("description", "즐겁고 신나고 행복한 3박 4일 제주도 여행 상품입니다.");
        return product;
    }

    private void createProduct(String name, String description) {
        Product entity = new Product();
        entity.setName(name);
        entity.setDescription(description);
        repository.save(entity);
    }
}