package com.example.lms.repository;

import com.example.lms.model.Course;
import com.example.lms.model.enums.CourseStatus;
import com.example.lms.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    
    @EntityGraph(attributePaths = {"teacher"})
    List<Course> findAll();

    @EntityGraph(attributePaths = {"teacher"})
    List<Course> findByStatus(CourseStatus status);

    @EntityGraph(attributePaths = {"teacher"})
    List<Course> findByTeacher(User teacher);
}
