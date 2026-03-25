package com.example.lms.repository;

import com.example.lms.model.Enrollment;
import com.example.lms.model.User;
import com.example.lms.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    Optional<Enrollment> findByStudentAndCourse(User student, Course course);
    java.util.List<Enrollment> findByStudent(User student);
}
