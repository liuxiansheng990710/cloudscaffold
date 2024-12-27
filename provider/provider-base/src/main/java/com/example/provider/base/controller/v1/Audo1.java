package com.example.provider.base.controller.v1;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class Audo1 extends Audo {

    @Schema(description = "年龄")
    private String age;
}
