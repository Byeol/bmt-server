package com.bringmethere.reviews;

import com.bringmethere.comments.Comment;
import com.bringmethere.products.Product;
import com.bringmethere.products.domain.AbstractProductEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.core.Authentication;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(force = true)
@Data
@EqualsAndHashCode(of = "id")
@ToString(of = "id")
@Entity
public class Review extends AbstractProductEntity {

    @NotEmpty
    private String title;

    @NotEmpty
    private String content;

    @ManyToOne(cascade = CascadeType.REFRESH, optional = false)
    private Product product;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "review")
    private List<Comment> comments = new ArrayList<>();

    public boolean isCustomer(Authentication authentication) {
        return getProduct().isCustomer(authentication);
    }

    public boolean isBringer(Authentication authentication) {
        return getProduct().isBringer(authentication);
    }
}