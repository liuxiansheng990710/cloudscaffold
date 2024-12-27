package com.example.commons.core.modelmapper.json;

import org.modelmapper.ModelMapper;
import org.modelmapper.Module;

/**
 * <p>
 * fastJson实体转换
 * <p>
 *
 * @author : 21
 * @since : 2023/11/2 15:07
 */
public class FastJsonModule implements Module {

    @Override
    public void setupModule(ModelMapper modelMapper) {
        modelMapper.getConfiguration().getConverters().add(0, new JSONObjectToJSONObjectConverter());
        modelMapper.getConfiguration().getConverters().add(0, new JSONArrayToJSONArrayConverter());
    }
}
