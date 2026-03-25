package com.example.lms.controller;

import com.example.lms.model.Lesson;
import com.example.lms.model.Module;
import com.example.lms.security.SecurityUtils;
import com.example.lms.service.LessonService;
import com.example.lms.service.ModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/teacher")
@RequiredArgsConstructor
public class ModuleController {

    private final ModuleService moduleService;
    private final LessonService lessonService;

    @PostMapping("/courses/{courseId}/modules")
    public String createModule(@PathVariable Long courseId, @RequestParam String title, @RequestParam Integer orderNum, RedirectAttributes redirectAttributes) {
        Module m = Module.builder().title(title).orderNum(orderNum).build();
        moduleService.createModule(courseId, m, SecurityUtils.getCurrentUserId());
        redirectAttributes.addFlashAttribute("successMessage", "Đã lưu Chương học thành công!");
        return "redirect:/teacher/courses/" + courseId;
    }

    @PostMapping("/modules/{moduleId}/lessons")
    public String createLesson(@PathVariable Long moduleId, 
                               @RequestParam String title, 
                               @RequestParam String videoUrl, 
                               @RequestParam String content,
                               @RequestParam Integer orderNum,
                               @RequestParam Long courseId,
                               RedirectAttributes redirectAttributes) {
        Lesson l = Lesson.builder()
                .title(title)
                .videoUrl(videoUrl)
                .content(content)
                .orderNum(orderNum)
                .build();
        lessonService.createLesson(moduleId, l, SecurityUtils.getCurrentUserId());
        redirectAttributes.addFlashAttribute("successMessage", "Bài học đã được tạo thành công!");
        return "redirect:/teacher/courses/" + courseId;
    }
}
