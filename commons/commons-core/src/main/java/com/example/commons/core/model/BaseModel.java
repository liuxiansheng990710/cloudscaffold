package com.example.commons.core.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 主键自增父类，使用assign_id（19位）
 * <p>
 *
 * @author : 21
 * @since : 2023/9/25 15:17
 */

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BaseModel extends BaseConvert {

    private static final long serialVersionUID = -4333029898887754221L;

    public static final String ID = "id";

    public BaseModel(Long id) {
        this.id = id;
    }

    protected Long id;
}
