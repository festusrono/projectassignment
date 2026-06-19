package com.smoothcode.projectassigment;

import com.smoothcode.projectassigment.model.Project;
import com.smoothcode.projectassigment.model.Student;
import com.smoothcode.projectassigment.repository.ProjectRepository;
import com.smoothcode.projectassigment.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ExpressionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "${SPRING_ORIGINS:*}")
@RequestMapping("/students")
public class StudentController {
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @GetMapping("/")
    public ResponseEntity<List<Student>> getAllStudents() {
        return ResponseEntity.ok(studentRepository.findAll());
    }

    public Student retrieveStudentById(int id ) {
        return studentRepository.findById(id).orElseThrow(
                ()-> new ExpressionException("Student with id "+id+" is not found")
        );
    }
    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable int id) {
        return ResponseEntity.of(studentRepository.findById(id));
    }
    @PostMapping("/")
    public ResponseEntity<Student> createStudent(@RequestBody Student studentDetails) {
        Student savedStudent = studentRepository.save(studentDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedStudent);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable int id, @RequestBody Student studentDetails) {
        Student student = retrieveStudentById(id);
        student.setName(studentDetails.getName());
        student.setAverage(studentDetails.getAverage());
        return ResponseEntity.ok(studentRepository.save(student));
    }
}
