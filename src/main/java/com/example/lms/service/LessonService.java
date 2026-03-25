package com.example.lms.service;

import com.example.lms.exception.ResourceNotFoundException;
import com.example.lms.exception.UnauthorizedActionException;
import com.example.lms.model.Lesson;
import com.example.lms.model.Module;
import com.example.lms.repository.LessonRepository;
import com.example.lms.repository.ModuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;
    private final ModuleRepository moduleRepository;
    private final CourseService courseService;

    // View Lesson Detail -> Requirs Enrollment for Student, or Ownership for Teacher
    public Lesson getLessonContent(Long lessonId, Long currentUserId, boolean isTeacher) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found"));
        
        Long courseId = lesson.getModule().getCourse().getId();
        Long teacherId = lesson.getModule().getCourse().getTeacher().getId();

        if (isTeacher) {
            if (!teacherId.equals(currentUserId)) {
                throw new UnauthorizedActionException("Not your lesson");
            }
        } else {
            // Student
            if (!courseService.isStudentEnrolled(currentUserId, courseId)) {
                throw new UnauthorizedActionException("You must enroll in this course to view lessons.");
            }
        }
        return lesson;
    }

    @Transactional
    public Lesson createLesson(Long moduleId, Lesson lesson, Long teacherId) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module not found"));
        if (!module.getCourse().getTeacher().getId().equals(teacherId)) {
            throw new UnauthorizedActionException("Not your course");
        }
        lesson.setModule(module);
        return lessonRepository.save(lesson);
    }

    @Transactional
    public Lesson updateLesson(Long lessonId, Lesson updatedInfo, Long teacherId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found"));
        if (!lesson.getModule().getCourse().getTeacher().getId().equals(teacherId)) {
            throw new UnauthorizedActionException("Not your course");
        }
        lesson.setTitle(updatedInfo.getTitle());
        lesson.setContent(updatedInfo.getContent());
        lesson.setVideoUrl(updatedInfo.getVideoUrl());
        lesson.setOrderNum(updatedInfo.getOrderNum());
        return lessonRepository.save(lesson);
    }

    @Transactional
    public void deleteLesson(Long lessonId, Long teacherId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found"));
        if (!lesson.getModule().getCourse().getTeacher().getId().equals(teacherId)) {
            throw new UnauthorizedActionException("Not your course");
        }
        lessonRepository.delete(lesson);
    }
}
