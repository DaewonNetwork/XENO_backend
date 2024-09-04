package com.daewon.xeno_backend.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BrandApprovalDTO {

    private Long Id;
    private String email;
    private String brandName;
    private String status;
}
