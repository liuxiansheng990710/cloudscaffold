package com.example.model.auth.dto;

import java.util.Date;

import com.example.commons.core.model.BaseConvert;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UserDTO extends BaseConvert {

    private Long uid;
    private Date createTime;

}
