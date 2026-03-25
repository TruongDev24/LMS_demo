package com.example.lms.controller;

import com.example.lms.model.Course;
import com.example.lms.security.SecurityUtils;
import com.example.lms.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final CourseService courseService;

    @GetMapping("/")
    public String index(Model model) {
        List<Course> courses = courseService.getPublishedCourses();
        model.addAttribute("courses", courses);
        return "index";
    }

    @PostMapping("/enroll/{courseId}")
    public String enroll(@PathVariable Long courseId) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return "redirect:/login";
        }
        try {
            courseService.enrollStudent(userId, courseId);
            return "redirect:/learning/" + courseId;
        } catch (Exception e) {
            return "redirect:/?error=" + e.getMessage();
        }
    }
}
