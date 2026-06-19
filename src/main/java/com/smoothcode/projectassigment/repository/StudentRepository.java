package com.smoothcode.projectassigment.repository;

import com.smoothcode.projectassigment.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Integer> {
}
