package com.isapp_test.is_backend_test.controller;

import com.isapp_test.is_backend_test.entity.Company;
import com.isapp_test.is_backend_test.entity.User;
import com.isapp_test.is_backend_test.repo.CompanyRepository;
import com.isapp_test.is_backend_test.repo.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiController {
    private final UserRepository userRepo;
    private final CompanyRepository companyRepo;

    public ApiController(UserRepository userRepo, CompanyRepository companyRepo) {
        this.userRepo = userRepo;
        this.companyRepo = companyRepo;
    }

    @GetMapping("/users")
    public List<User> users() { return userRepo.findAll(); }

    @GetMapping("/companies")
    public List<Company> companies() { return companyRepo.findAll(); }
}
