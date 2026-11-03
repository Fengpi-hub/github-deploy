package com.example.fruitblog;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

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

    /* ============ 查（公开） ============ */

    /** 首页：返回全部文章（按时间倒序） */
    @GetMapping
    public List<Article> list() {
        return repo.findAllByOrderByCreatedAtDesc();
    }

    /** 详情：按 id 返回单篇文章（公开） */
    @GetMapping("/{id}")
    public Article detail(@PathVariable Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "文章不存在"));
    }

    /* ============ 增（需登录） ============ */

    /**
     * 发布文章：表单包含 title、content，以及可选的多张图片 images（二进制）。
     * 作者取当前登录用户。
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Article> create(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "images", required = false) MultipartFile[] images) {

        requireLogin();
        if (title == null || title.trim().isEmpty()
                || content == null || content.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Article article = new Article();
        article.setTitle(title.trim());
        article.setContent(content.trim());
        article.setAuthor(currentUsername());
        article.setImages(saveImages(images));

        return ResponseEntity.ok(repo.save(article));
    }

    /* ============ 改（需登录 + 作者本人） ============ */

    /**
     * 更新文章：支持修改标题/正文，以及替换或清除图片。
     * 表单字段：title、content、可选 images（新图，会替换旧图）、可选 clearImages=true（清空图片）。
     */
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Article> update(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "images", required = false) MultipartFile[] images,
            @RequestParam(value = "clearImages", required = false) String clearImages) {

        requireLogin();
        Article article = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "文章不存在"));
        ensureOwner(article);

        if (title == null || title.trim().isEmpty()
                || content == null || content.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        article.setTitle(title.trim());
        article.setContent(content.trim());

        boolean clear = "true".equalsIgnoreCase(clearImages);
        if (clear) {
            deleteImageFiles(article.getImages());
            article.setImages("");
        } else if (images != null && images.length > 0) {
            deleteImageFiles(article.getImages());
            article.setImages(saveImages(images));
        }
        // 否则保留原有图片

        return ResponseEntity.ok(repo.save(article));
    }

    /* ============ 删（需登录 + 作者本人） ============ */

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        requireLogin();
        Article article = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "文章不存在"));
        ensureOwner(article);

        deleteImageFiles(article.getImages());
        repo.delete(article);
        return ResponseEntity.noContent().build();
    }

    /* ============ 内部辅助 ============ */

    private void requireLogin() {
        if (currentUsername() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "请先登录");
        }
    }

    /** 当前登录用户名；未登录返回 null */
    private String currentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return null;
        }
        return auth.getName();
    }

    private void ensureOwner(Article article) {
        String me = currentUsername();
        if (me == null || !me.equals(article.getAuthor())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "只能修改/删除自己的文章");
        }
    }

    /** 保存上传的图片，返回逗号分隔的访问地址；无图返回 null */
    private String saveImages(MultipartFile[] images) {
        if (images == null || images.length == 0) {
            return null;
        }
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
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "图片保存失败");
        }
        return urls.isEmpty() ? null : String.join(",", urls);
    }

    /** 删除文章关联的物理图片文件 */
    private void deleteImageFiles(String images) {
        if (images == null || images.isBlank()) {
            return;
        }
        for (String url : images.split(",")) {
            if (url == null || url.isBlank()) continue;
            try {
                String name = url.substring(url.lastIndexOf('/') + 1);
                Files.deleteIfExists(Paths.get(uploadDir).resolve(name));
            } catch (IOException ignored) {
                // 删除失败不影响主流程
            }
        }
    }
}
