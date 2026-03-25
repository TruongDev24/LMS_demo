package com.example.lms.service;

import com.example.lms.exception.ResourceNotFoundException;
import com.example.lms.exception.UnauthorizedActionException;
import com.example.lms.model.Course;
import com.example.lms.model.Module;
import com.example.lms.repository.CourseRepository;
import com.example.lms.repository.ModuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ModuleService {

    private final ModuleRepository moduleRepository;
    private final CourseRepository courseRepository;

    @Transactional
    public Module createModule(Long courseId, Module module, Long teacherId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        if (!course.getTeacher().getId().equals(teacherId)) {
            throw new UnauthorizedActionException("Not your course");
        }
        module.setCourse(course);
        return moduleRepository.save(module);
    }

    @Transactional
    public Module updateModule(Long moduleId, Module updatedInfo, Long teacherId) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module not found"));
        if (!module.getCourse().getTeacher().getId().equals(teacherId)) {
            throw new UnauthorizedActionException("Not your course");
        }
        module.setTitle(updatedInfo.getTitle());
        module.setOrderNum(updatedInfo.getOrderNum());
        return moduleRepository.save(module);
    }

    @Transactional
    public void deleteModule(Long moduleId, Long teacherId) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module not found"));
        if (!module.getCourse().getTeacher().getId().equals(teacherId)) {
            throw new UnauthorizedActionException("Not your course");
        }
        moduleRepository.delete(module);
    }
}
