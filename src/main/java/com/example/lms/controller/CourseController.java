package com.example.lms.controller;

import com.example.lms.model.Course;
import com.example.lms.model.enums.CourseStatus;
import com.example.lms.security.SecurityUtils;
import com.example.lms.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;

@Controller
@RequestMapping("/teacher/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    public String listTeacherCourses(Model model) {
        model.addAttribute("courses", courseService.getTeacherCourses(SecurityUtils.getCurrentUserId()));
        return "teacher_dashboard";
    }

    @PostMapping
    public String createCourse(@RequestParam String title, 
                               @RequestParam String description,
                               @RequestParam BigDecimal price,
                               RedirectAttributes redirectAttributes) {
        Course c = Course.builder()
                .title(title)
                .description(description)
                .price(price)
                .thumbnailUrl("https://placehold.co/600x400")
                .status(CourseStatus.DRAFT) // Defaullt is Draft
                .build();
        courseService.createCourse(c, SecurityUtils.getCurrentUserId());
        redirectAttributes.addFlashAttribute("successMessage", "Đã tạo Khóa học mới thành công!");
        return "redirect:/teacher/courses";
    }

    @GetMapping("/{id}")
    public String manageCourse(@PathVariable Long id, Model model) {
        Course course = courseService.getTeacherCourses(SecurityUtils.getCurrentUserId())
                .stream().filter(c -> c.getId().equals(id)).findFirst()
                .orElseThrow(() -> new RuntimeException("Course not found or unauthorized"));
        model.addAttribute("course", course);
        return "teacher_course_detail";
    }

    @PostMapping("/{id}/toggle-status")
    public String toggleStatus(@PathVariable Long id, RedirectAttributes rattrs) {
        Course course = courseService.getTeacherCourses(SecurityUtils.getCurrentUserId())
                .stream().filter(c -> c.getId().equals(id)).findFirst()
                .orElseThrow(() -> new RuntimeException("Course not found"));
        course.setStatus(course.getStatus() == CourseStatus.DRAFT ? CourseStatus.PUBLISHED : CourseStatus.DRAFT);
        courseService.updateCourse(id, course, SecurityUtils.getCurrentUserId());
        rattrs.addFlashAttribute("successMessage", "Đã cập nhật trạng thái Khóa học!");
        return "redirect:/teacher/courses/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteCourse(@PathVariable Long id, RedirectAttributes rattrs) {
        try {
            courseService.deleteCourse(id, SecurityUtils.getCurrentUserId());
            rattrs.addFlashAttribute("successMessage", "Đã xóa vĩnh viễn khóa học khỏi hệ thống!");
            return "redirect:/teacher/courses";
        } catch (Exception e) {
            rattrs.addFlashAttribute("error", e.getMessage());
            return "redirect:/teacher/courses/" + id;
        }
    }
}
