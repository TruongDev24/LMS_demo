package com.example.lms.seeder;

import com.example.lms.model.*;
import com.example.lms.model.Module;
import com.example.lms.model.enums.CourseStatus;
import com.example.lms.model.enums.Role;
import com.example.lms.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final ModuleRepository moduleRepository;
    private final LessonRepository lessonRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ReviewRepository reviewRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            User admin = userRepository.save(User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .fullName("System Admin")
                    .role(Role.ADMIN)
                    .build());

            User teacher = userRepository.save(User.builder()
                    .username("teacher")
                    .password(passwordEncoder.encode("teacher123"))
                    .fullName("John Doe - Senior Teacher")
                    .role(Role.TEACHER)
                    .build());

            User student = userRepository.save(User.builder()
                    .username("student")
                    .password(passwordEncoder.encode("student123"))
                    .fullName("Alice Student")
                    .role(Role.STUDENT)
                    .build());

            Course course = courseRepository.save(Course.builder()
                    .title("Spring Boot Masterclass")
                    .description("Xây dựng dứng dụng full-stack với Spring Boot.")
                    .price(new BigDecimal("99.99"))
                    .thumbnailUrl("https://placehold.co/600x400")
                    .status(CourseStatus.PUBLISHED)
                    .teacher(teacher)
                    .build());

            Module mod1 = moduleRepository.save(Module.builder()
                    .title("Giới thiệu")
                    .orderNum(1)
                    .course(course)
                    .build());

            Module mod2 = moduleRepository.save(Module.builder()
                    .title("Kiến thức cốt lõi")
                    .orderNum(2)
                    .course(course)
                    .build());

            lessonRepository.saveAll(Arrays.asList(
                    Lesson.builder().title("Sillabus & Rules").content("Đọc tài liệu.").orderNum(1).module(mod1).build(),
                    Lesson.builder().title("Cài đặt môi trường").videoUrl("https://www.youtube.com/embed/dQw4w9WgXcQ").orderNum(2).module(mod1).build(),
                    Lesson.builder().title("DI và IoC").content("Dependency Injection.").orderNum(1).module(mod2).build(),
                    Lesson.builder().title("JPA & Hibernate").content("Kết nối Database.").orderNum(2).module(mod2).build()
            ));

            enrollmentRepository.save(Enrollment.builder()
                    .student(student)
                    .course(course)
                    .enrolledAt(LocalDateTime.now())
                    .isCompleted(false)
                    .build());

            reviewRepository.save(Review.builder()
                    .student(student)
                    .course(course)
                    .rating(5)
                    .comment("Khóa học rất tuyệt vời!")
                    .createdAt(LocalDateTime.now())
                    .build());

            System.out.println("============== PHASE 1 SEEDER ===============");
            System.out.println("Data seeded successfully!");
            System.out.println("Admin: admin / admin123");
            System.out.println("Teacher: teacher / teacher123");
            System.out.println("Student: student / student123");
            System.out.println("=============================================");
        }
    }
}
