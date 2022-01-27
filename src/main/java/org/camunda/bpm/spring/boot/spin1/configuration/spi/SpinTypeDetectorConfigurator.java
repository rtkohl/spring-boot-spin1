package org.camunda.bpm.spring.boot.spin1.configuration.spi;

import org.camunda.spin.impl.json.jackson.format.JacksonJsonDataFormat;
import org.camunda.spin.impl.json.jackson.format.MapJacksonJsonTypeDetector;
import org.camunda.spin.spi.DataFormatConfigurator;

public class SpinTypeDetectorConfigurator implements DataFormatConfigurator<JacksonJsonDataFormat> {

    @Override
    public Class<JacksonJsonDataFormat> getDataFormatClass() {
        return JacksonJsonDataFormat.class;
    }

    @Override
    public void configure(JacksonJsonDataFormat dataFormat) {
        dataFormat.addTypeDetector(MapJacksonJsonTypeDetector.INSTANCE);
    }
}
