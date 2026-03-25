package com.example.lms.controller;

import com.example.lms.security.SecurityUtils;
import com.example.lms.service.CourseService;
import com.example.lms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final CourseService courseService;
    private final UserService userService;

    // Smart Router: Điều hướng thông minh sau khi Login dựa trên Role
    @GetMapping("/dashboard")
    public String dashboardRouter() {
        var user = SecurityUtils.getCurrentUserDetails();
        if (user == null) return "redirect:/login";

        boolean isAdmin = user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) {
            return "redirect:/admin/dashboard"; // Trả về giao diện thống kê cũ cho Admin
        }
        
        boolean isTeacher = user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER"));
        if (isTeacher) {
            return "redirect:/teacher/courses"; // Trả về Studio cho Teacher
        }
        
        // Học viên thì trở về Khu vực Học tập cá nhân (Dashboard của sinh viên)
        return "redirect:/student/my-courses";
    }

    // Giao diện Khóa học của tôi (Dành cho Student)
    @GetMapping("/student/my-courses")
    public String studentDashboard(Model model) {
        long studentId = SecurityUtils.getCurrentUserId();
        model.addAttribute("myCourses", courseService.getStudentEnrolledCourses(studentId));
        return "student_dashboard";
    }

    // Giao diện thống kê chuyên dụng cho khoang Quản trị Admin
    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
        long totalCourses = courseService.getTotalCourses();
        long totalStudents = userService.getTotalUsers(); 
        long estimatedRevenue = 45000; // Mock Data
        
        model.addAttribute("totalCourses", totalCourses);
        model.addAttribute("totalStudents", totalStudents);
        model.addAttribute("estimatedRevenue", String.format("$%,d", estimatedRevenue));
        
        // Render template cũ "dashboard.html" cho Admin
        return "dashboard";
    }
}
