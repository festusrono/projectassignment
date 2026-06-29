package com.smoothcode.projectassigment.controller;

import com.smoothcode.projectassigment.model.Project;
import com.smoothcode.projectassigment.model.Student;
import com.smoothcode.projectassigment.repository.ProjectRepository;
import com.smoothcode.projectassigment.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ExpressionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "${SPRING_ORIGINS:*}")
@RequestMapping("/students")
public class StudentController {
    private static int maxProjectsPerStudent = 3;
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

    public Project retrieveProjectById(int id ) {
        return projectRepository.findById(id).orElseThrow(
                () -> new ExpressionException("Project with id " + id + " is not found")
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
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStudent(@PathVariable int id) {
        Student student = retrieveStudentById(id);
        studentRepository.delete(student);
        return ResponseEntity.ok("Student Deleted Successfully");
    }
    @GetMapping("/max_project")
    public ResponseEntity<Integer> getMaxProjects() {
        return ResponseEntity.ok(maxProjectsPerStudent);
    }
    @PutMapping("/max_project")
    public ResponseEntity<Integer> updateMaxProjectPerStudent(@RequestBody int maximumNumber) {
        maxProjectsPerStudent = maximumNumber;
        return ResponseEntity.ok(maxProjectsPerStudent);
    }

    @PostMapping("/{student_id}/projects/{project_id}")
    public ResponseEntity<Student> addProjectToStudent(@PathVariable int student_id, @PathVariable int project_id) {
        Student student = retrieveStudentById(student_id);
        Project project = retrieveProjectById(project_id);
        for (Project P : student.getProjects()) {
            if (P.getId() == project.getId()) {
                return ResponseEntity.status(400).body(student);
            }
        }
        if (student.getProjects().size()>=maxProjectsPerStudent){
            return ResponseEntity.badRequest().body(student);
        }
        student.getProjects().add(project);
        return ResponseEntity.status(HttpStatus.CREATED).body(studentRepository.save(student));
    }

    @DeleteMapping("/{student_id}/projects/{project_id}")
    public ResponseEntity<Student> deleteProjectToStudent(@PathVariable int student_id, @PathVariable int project_id) {
        Student student = retrieveStudentById(student_id);
        Project project = retrieveProjectById(project_id);
        student.getProjects().remove(project);
        return ResponseEntity.ok(studentRepository.save(student));
    }

    @GetMapping("{student_id}/availableprojects")
    public ResponseEntity<List<Project>> getStudentAvailableProjects(@PathVariable int student_id) {
        Student student = retrieveStudentById(student_id);
        List<Project> availableProjects = new ArrayList<Project>();
        if (student.getProjects().size()>=maxProjectsPerStudent){
            return ResponseEntity.ok(availableProjects);
        }
        List<Project> allProjects = projectRepository.findAll();
        HashSet<Integer> projectIds = new HashSet<>();
        for (Project p : student.getProjects()) {
            projectIds.add(p.getId());
        }
        for (Project pro : allProjects) {
            if (!projectIds.contains(pro.getId())) {
                availableProjects.add(pro);
            }
        }
        return ResponseEntity.ok(availableProjects);
    }

    @GetMapping("/assignment")
    public ResponseEntity<HashMap<String, String>> assignProjectToStudent(){
        HashMap<String, String> assignList = new HashMap<String, String>();
        HashSet<Integer> projectIds = projectRepository.findAll()
                .stream().
                map( Project::getId)
                .collect(Collectors.toCollection(HashSet::new));
        List<Student> listStudent = studentRepository.findAll();
        listStudent.sort( (Student s1, Student s2) -> Double.compare(s2.getAverage(), s1.getAverage()) );
        for (Student s : listStudent) {
            for (Project p : s.getProjects()) {
                assignList.put(s.getName(), p.getName());
                projectIds.remove(p.getId());
                break;
            }
        }
        return ResponseEntity.ok(assignList);
    }
}
