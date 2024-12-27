package com.example.provider.base.controller.v1;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class Audo2 extends Audo {
    @Schema(description = "姓名")
    private String name;
}
