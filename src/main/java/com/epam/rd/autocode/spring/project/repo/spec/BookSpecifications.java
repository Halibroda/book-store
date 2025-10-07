package com.epam.rd.autocode.spring.project.repo.spec;

import com.epam.rd.autocode.spring.project.model.Book;
import com.epam.rd.autocode.spring.project.model.enums.AgeGroup;
import com.epam.rd.autocode.spring.project.model.enums.Language;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public final class BookSpecifications {

    private BookSpecifications() {}

    public static Specification<Book> genreEquals(String genre) {
        return genre == null || genre.isBlank()
            ? null
            : (root, q, cb) -> cb.equal(cb.lower(root.get("genre")), genre.toLowerCase());
    }

    public static Specification<Book> languageEquals(Language language) {
        return language == null
            ? null
            : (root, q, cb) -> cb.equal(root.get("language"), language);
    }

    public static Specification<Book> ageGroupEquals(AgeGroup ageGroup) {
        return ageGroup == null
            ? null
            : (root, q, cb) -> cb.equal(root.get("ageGroup"), ageGroup);
    }

    public static Specification<Book> minPrice(BigDecimal min) {
        return min == null
            ? null
            : (root, q, cb) -> cb.greaterThanOrEqualTo(root.get("price"), min);
    }

    public static Specification<Book> maxPrice(BigDecimal max) {
        return max == null
            ? null
            : (root, q, cb) -> cb.lessThanOrEqualTo(root.get("price"), max);
    }

    public static Specification<Book> search(String text) {
        return text == null || text.isBlank()
            ? null
            : (root, q, cb) -> {
            String like = "%" + text.toLowerCase() + "%";
            return cb.or(
                cb.like(cb.lower(root.get("name")), like),
                cb.like(cb.lower(root.get("author")), like)
            );
        };
    }
}
