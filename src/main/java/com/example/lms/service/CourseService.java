package com.example.lms.service;

import com.example.lms.exception.RegistrationException;
import com.example.lms.exception.ResourceNotFoundException;
import com.example.lms.exception.UnauthorizedActionException;
import com.example.lms.model.Course;
import com.example.lms.model.Enrollment;
import com.example.lms.model.User;
import com.example.lms.model.enums.CourseStatus;
import com.example.lms.repository.CourseRepository;
import com.example.lms.repository.EnrollmentRepository;
import com.example.lms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;

    // --- Student / Public Features ---
    public List<Course> getPublishedCourses() {
        return courseRepository.findByStatus(CourseStatus.PUBLISHED);
    }

    @Transactional
    public void enrollStudent(Long studentId, Long courseId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        if (course.getStatus() == CourseStatus.DRAFT) {
            throw new RegistrationException("Cannot enroll in a DRAFT course");
        }
        enrollmentRepository.findByStudentAndCourse(student, course)
                .ifPresent(e -> {
                    throw new RegistrationException("Student is already enrolled in this course");
                });

        Enrollment newEnrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .enrolledAt(LocalDateTime.now())
                .isCompleted(false)
                .build();
        enrollmentRepository.save(newEnrollment);
    }
    
    public boolean isStudentEnrolled(Long studentId, Long courseId) {
        User student = userRepository.findById(studentId).orElse(null);
        Course course = courseRepository.findById(courseId).orElse(null);
        if (student == null || course == null) return false;
        return enrollmentRepository.findByStudentAndCourse(student, course).isPresent();
    }

    public Course getCourseById(Long id) {
        return courseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Course not found"));
    }

    public List<Course> getStudentEnrolledCourses(Long studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        return enrollmentRepository.findByStudent(student).stream()
                .map(Enrollment::getCourse)
                .toList();
    }

    // --- Teacher Features ---
    public List<Course> getTeacherCourses(Long teacherId) {
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));
        return courseRepository.findByTeacher(teacher);
    }

    @Transactional
    public Course createCourse(Course course, Long teacherId) {
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));
        course.setTeacher(teacher);
        return courseRepository.save(course);
    }

    @Transactional
    public Course updateCourse(Long courseId, Course updatedInfo, Long teacherId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        if (!course.getTeacher().getId().equals(teacherId)) {
            throw new UnauthorizedActionException("You can only edit your own courses.");
        }
        course.setTitle(updatedInfo.getTitle());
        course.setDescription(updatedInfo.getDescription());
        course.setPrice(updatedInfo.getPrice());
        course.setThumbnailUrl(updatedInfo.getThumbnailUrl());
        course.setStatus(updatedInfo.getStatus());
        return courseRepository.save(course);
    }

    @Transactional
    public void deleteCourse(Long courseId, Long teacherId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        if (!course.getTeacher().getId().equals(teacherId)) {
            throw new UnauthorizedActionException("You can only delete your own courses.");
        }
        if (course.getEnrollments() != null && !course.getEnrollments().isEmpty()) {
            throw new UnauthorizedActionException("Cannot delete course because students are already enrolled.");
        }
        courseRepository.delete(course);
    }

    // --- Admin Features ---
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }
    
    public long getTotalCourses() {
        return courseRepository.count();
    }
}
