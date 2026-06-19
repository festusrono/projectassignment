package com.smoothcode.projectassigment.repository;

import com.smoothcode.projectassigment.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Integer> {
}
