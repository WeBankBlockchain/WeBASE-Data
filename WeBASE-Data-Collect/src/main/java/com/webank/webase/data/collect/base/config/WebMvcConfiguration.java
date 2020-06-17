package com.webank.webase.data.collect.base.config;

import com.webank.webase.data.collect.solc.SolcService;
import java.io.File;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

// @Configuration
public class WebMvcConfiguration extends WebMvcConfigurationSupport {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // web request uri of "/solcjs/**", return the files in /solcjs/xx.js
        File fileDir = new File(SolcService.SOLC_DIR_PATH);
        String path = fileDir.getAbsolutePath();
        registry.addResourceHandler("/solcjs/**")
                .addResourceLocations("file:/" + path + File.separator);
        super.addResourceHandlers(registry);
    }
}
