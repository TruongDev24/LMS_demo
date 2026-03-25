package com.example.lms.controller;

import com.example.lms.model.Course;
import com.example.lms.model.Lesson;
import com.example.lms.security.SecurityUtils;
import com.example.lms.service.CourseService;
import com.example.lms.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/learning")
@RequiredArgsConstructor
public class LearningController {

    private final CourseService courseService;
    private final LessonService lessonService;

    @GetMapping("/{courseId}")
    public String enterWorkspace(@PathVariable Long courseId, Model model) {
        Long userId = SecurityUtils.getCurrentUserId();
        
        boolean isTeacher = SecurityUtils.getCurrentUserDetails().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER"));
                
        if (!isTeacher && !courseService.isStudentEnrolled(userId, courseId)) {
            return "redirect:/?error=Not enrolled";
        }

        Course course = courseService.getCourseById(courseId);
        
        // Fix: Tự động điều hướng Học viên vào bài học số 1 nếu vào thẳng ID tổng
        if(course.getModules() != null && !course.getModules().isEmpty()) {
             var firstMod = course.getModules().get(0);
             if(firstMod.getLessons() != null && !firstMod.getLessons().isEmpty()) {
                  return "redirect:/learning/" + courseId + "/lesson/" + firstMod.getLessons().get(0).getId();
             }
        }

        model.addAttribute("course", course);
        model.addAttribute("courseId", courseId);
        return "learning";
    }

    @GetMapping("/{courseId}/lesson/{lessonId}")
    public String viewLesson(@PathVariable Long courseId, @PathVariable Long lessonId, Model model) {
        Long userId = SecurityUtils.getCurrentUserId();
        boolean isTeacher = SecurityUtils.getCurrentUserDetails().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER") || a.getAuthority().equals("ROLE_ADMIN"));
        
        try {
            Lesson lesson = lessonService.getLessonContent(lessonId, userId, isTeacher);
            Course course = lesson.getModule().getCourse();
            model.addAttribute("lesson", lesson);
            model.addAttribute("course", course);
            model.addAttribute("courseId", courseId); // Set courseId explicitly for wrapper view
            return "learning";
        } catch (Exception e) {
            return "redirect:/?error=" + e.getMessage();
        }
    }
}
