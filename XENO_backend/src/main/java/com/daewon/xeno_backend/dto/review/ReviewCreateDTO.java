package com.daewon.xeno_backend.dto.review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewCreateDTO {

    private long orderId;


    private String text;

    private int star;

    private LocalDateTime createAt;


}