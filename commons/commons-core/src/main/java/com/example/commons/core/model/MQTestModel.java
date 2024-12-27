package com.example.commons.core.model;

import com.alibaba.fastjson.JSONObject;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class MQTestModel extends BaseConvert {

    private JSONObject jsonObject;
    private int id;

}
