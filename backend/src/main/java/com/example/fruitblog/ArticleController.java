package com.example.fruitblog;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleRepository repo;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    public ArticleController(ArticleRepository repo) {
        this.repo = repo;
    }

    /** 首页：返回全部文章（按时间倒序） */
    @GetMapping
    public List<Article> list() {
        return repo.findAllByOrderByCreatedAtDesc();
    }

    /** 详情：按 id 返回单篇文章 */
    @GetMapping("/{id}")
    public ResponseEntity<Article> detail(@PathVariable Long id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 发布文章：表单包含 title、content，以及可选的多张图片 images（二进制）。
     * 没有登录校验，符合“暂时不需要登录注册”的学习需求。
     */
    @PostMapping(consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Article> create(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "images", required = false) MultipartFile[] images) {

        if (title == null || title.trim().isEmpty()
                || content == null || content.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Article article = new Article();
        article.setTitle(title.trim());
        article.setContent(content.trim());

        if (images != null && images.length > 0) {
            List<String> urls = new ArrayList<>();
            try {
                Path dir = Paths.get(uploadDir);
                Files.createDirectories(dir);
                for (MultipartFile file : images) {
                    if (file == null || file.isEmpty()) {
                        continue;
                    }
                    String safeName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                    Files.copy(file.getInputStream(), dir.resolve(safeName),
                            StandardCopyOption.REPLACE_EXISTING);
                    urls.add("/uploads/" + safeName);
                }
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
            article.setImages(String.join(",", urls));
        }

        return ResponseEntity.ok(repo.save(article));
    }
}
