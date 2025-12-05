package org.uvhnael.mpbe.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Configuration class to enable AspectJ auto-proxying
 * This ensures that @Aspect beans are properly recognized and applied
 */
@Configuration
@EnableAspectJAutoProxy
public class AopConfig {
}
