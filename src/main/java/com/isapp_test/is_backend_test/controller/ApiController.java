package com.isapp_test.is_backend_test.controller;
import com.isapp_test.is_backend_test.entity.Company;
import com.isapp_test.is_backend_test.entity.User;
import com.isapp_test.is_backend_test.repo.CompanyRepository;
import com.isapp_test.is_backend_test.repo.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
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

    // userテーブルのCRUD処理
    @GetMapping("/users")
    public List<User> users() { return userRepo.findAll(); }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User body) {
        // 主キー手動運用の場合：nullはNGにする（自動採番なら削除）
        // if (body.getId() == null) return ResponseEntity.badRequest().build();
        // if (userRepo.existsById(body.getId())) return ResponseEntity.status(409).build();
        User saved = userRepo.save(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Integer id, @RequestBody User body) {
        if (body.getId() != null && !body.getId().equals(id)) {
            return ResponseEntity.badRequest().build(); // パスとボディ不一致
        }
        return userRepo.findById(id)
            .map(u -> {
                u.setUserName(body.getUserName());
                u.setCompanyId(body.getCompanyId());
                return ResponseEntity.ok(userRepo.save(u));
            })
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        if (!userRepo.existsById(id)) return ResponseEntity.notFound().build();
        userRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }


    // companyテーブルのCRUD処理
    @GetMapping("/companies")
    public List<Company> companies() { return companyRepo.findAll(); }

    @PostMapping("/companies")
    public ResponseEntity<Company> createCompany(@RequestBody Company body) {
        // if (body.getCompanyId() == null) return ResponseEntity.badRequest().build();
        // if (companyRepo.existsById(body.getCompanyId())) return ResponseEntity.status(409).build();
        Company saved = companyRepo.save(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/companies/{id}")
    public ResponseEntity<Company> updateCompany(@PathVariable Integer id, @RequestBody Company body) {
        if (body.getCompanyId() != null && !body.getCompanyId().equals(id)) {
            return ResponseEntity.badRequest().build();
        }
        return companyRepo.findById(id)
            .map(c -> {
                c.setCompanyName(body.getCompanyName());
                c.setCompanyAddress(body.getCompanyAddress());
                return ResponseEntity.ok(companyRepo.save(c));
            })
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/companies/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable Integer id) {
        if (!companyRepo.existsById(id)) return ResponseEntity.notFound().build();
        companyRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
