package com.shvk.security.controller;

import com.shvk.security.model.Student;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("v1")
public class StudentController {
    private List<Student> students = new ArrayList<>();

    @GetMapping("/students")
    public List<Student> students() {
        Student student1 = new Student(1, "shouvik", "paul");
        Student student2 = new Student(2, "subir", "ghosh");

        students.add(student1);
        students.add(student2);

        return students;
    }

    @PostMapping("/students")
    public Student createStudent(@RequestBody Student student){
        students.add(student);
        return student;
    }
}
