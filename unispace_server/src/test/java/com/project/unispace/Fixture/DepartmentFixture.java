package com.project.unispace.Fixture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.project.unispace.domain.university.entity.Department;
import com.project.unispace.domain.university.entity.College;
import com.project.unispace.domain.university.entity.DepartmentAffiliationType;
import com.project.unispace.domain.university.repository.DepartmentRepository;

@Component
public class DepartmentFixture {

    private final DepartmentRepository departmentRepository;

    @Autowired
    public DepartmentFixture(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public Department createDepartment(College college) {
        Department department = Department.createCollegeDepartment("전자공학과", false, DepartmentAffiliationType.COLLEGE, college);
        return departmentRepository.save(department);
    }
}
