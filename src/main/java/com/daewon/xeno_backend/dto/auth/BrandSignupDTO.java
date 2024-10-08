package com.daewon.xeno_backend.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BrandSignupDTO {
    private Long brandId;
    private String brandName;
    private String companyId;
}
