package com.example.fruitblog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    /** 首页按发布时间倒序展示 */
    List<Article> findAllByOrderByCreatedAtDesc();
}
