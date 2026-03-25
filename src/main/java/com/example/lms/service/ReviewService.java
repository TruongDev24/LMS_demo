package com.example.lms.service;

import com.example.lms.exception.ResourceNotFoundException;
import com.example.lms.exception.UnauthorizedActionException;
import com.example.lms.model.Course;
import com.example.lms.model.Review;
import com.example.lms.model.User;
import com.example.lms.repository.CourseRepository;
import com.example.lms.repository.ReviewRepository;
import com.example.lms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CourseService courseService;

    @Transactional
    public Review writeReview(Long courseId, Long studentId, Integer rating, String comment) {
        if (!courseService.isStudentEnrolled(studentId, courseId)) {
            throw new UnauthorizedActionException("You must be enrolled to submit a review.");
        }
        
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Review review = Review.builder()
                .rating(rating)
                .comment(comment)
                .course(course)
                .student(student)
                .createdAt(LocalDateTime.now())
                .build();

        return reviewRepository.save(review);
    }
}
