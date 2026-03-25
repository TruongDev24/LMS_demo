package com.example.lms.model;

import com.example.lms.model.enums.CourseStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "courses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"teacher", "enrollments", "modules", "reviews"})
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String thumbnailUrl;
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CourseStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private User teacher;
    
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<Module> modules;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<Enrollment> enrollments;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<Review> reviews;
}
