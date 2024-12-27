package com.example.commons.core.modelmapper.jdk8;

import org.modelmapper.ModelMapper;
import org.modelmapper.Module;

/**
 * <p>
 * jkd8自定义映射器
 * <P>主要处理Optional与Object相互转换</P>
 * <p>
 *
 * @author : 21
 * @since : 2023/11/2 15:04
 */
public class Jdk8Module implements Module {

    @Override
    public void setupModule(ModelMapper modelMapper) {
        modelMapper.getConfiguration().getConverters().add(0, new FromOptionalConverter());
        modelMapper.getConfiguration().getConverters().add(0, new ToOptionalConverter());
    }
}
