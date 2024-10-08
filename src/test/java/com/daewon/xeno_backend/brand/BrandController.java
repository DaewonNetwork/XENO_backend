package com.daewon.xeno_backend.brand;

import com.daewon.xeno_backend.domain.auth.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootTest
@RequestMapping("/brandRegister")
public class BrandController {
    @Autowired
    private BrandService brandService;

    @PostMapping("/register/brand")
    public ResponseEntity<?> registerBrand(@RequestBody BrandDTO dto) {
        Users registeredUser = brandService.registerBrandUser(dto);
        return ResponseEntity.ok(registeredUser);
    }
}
