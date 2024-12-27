package com.example.commons.core.modelmapper.jsr310;

import org.modelmapper.ModelMapper;
import org.modelmapper.Module;

/**
 * <p>
 * JSR-310中Temporal与Object相互转换
 * <P>Temporal 是Java 8日期/时间API（JSR-310规范）的一个基础接口，代表了一个能够获取时间、日期、时区等信息的对象。</P>
 * <P>Instant、LocalDate、LocalTime、LocalDateTime、ZonedDateTime等都是这个接口的实现</P>
 * <p>
 *
 * @author : 21
 * @since : 2023/11/2 15:21
 */
public class Jsr310Module implements Module {

    private final Jsr310ModuleConfig config;

    public Jsr310Module() {
        this(new Jsr310ModuleConfig());
    }

    public Jsr310Module(Jsr310ModuleConfig config) {
        this.config = config;
    }

    @Override
    public void setupModule(ModelMapper modelMapper) {
        modelMapper.getConfiguration().getConverters().add(0, new FromTemporalConverter(config));
        modelMapper.getConfiguration().getConverters().add(0, new ToTemporalConverter(config));
        modelMapper.getConfiguration().getConverters().add(0, new TemporalToTemporalConverter());
    }
}
