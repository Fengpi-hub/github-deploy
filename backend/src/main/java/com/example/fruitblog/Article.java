package com.example.fruitblog;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 文章实体：一篇水果介绍。
 * images 字段用逗号分隔保存多张图片的访问地址，方便学习（也可拆成单独的图片表）。
 * author 记录发布者用户名，用于“只能修改/删除自己的文章”的权限控制。
 */
@Entity
@Table(name = "articles")
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    /** 逗号分隔的图片地址，例如 /uploads/aaa.png,/uploads/bbb.png */
    @Column(columnDefinition = "TEXT")
    private String images;

    /** 发布者用户名（来自登录用户） */
    private String author;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Article() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /** 每次更新前自动刷新 updatedAt */
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
