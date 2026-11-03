package com.example.fruitblog;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 首次启动时灌入初始数据（仅在数据库为空时执行）：
 * - 一个管理员账号 admin / admin123
 * - 两篇示例水果文章（作者为 admin）
 * 方便项目克隆下来就能直接体验登录、看文章、做增删改查。
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository,
                      ArticleRepository articleRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.articleRepository = articleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            userRepository.save(admin);

            Article a1 = new Article();
            a1.setTitle("🍎 苹果：一天一苹果，医生远离我");
            a1.setContent("苹果是最常见的水果之一，富含膳食纤维和维生素 C。\n\n口感脆甜，品种繁多，如红富士、嘎啦、蛇果等。\n\n小知识：苹果的果皮也很有营养，洗净后连皮吃更健康。");
            a1.setAuthor("admin");
            articleRepository.save(a1);

            Article a2 = new Article();
            a2.setTitle("🍌 香蕉：方便携带的能量来源");
            a2.setContent("香蕉富含钾元素，能快速补充体力，是健身和早餐的好选择。\n\n它软糯香甜，几乎不用清洗，剥皮即食。\n\n提示：香蕉怕冻，别放进冰箱冷藏哦。");
            a2.setAuthor("admin");
            articleRepository.save(a2);
        }
    }
}
