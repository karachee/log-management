package com.karachee.lms.configuration;

import com.karachee.lms.ApiConfiguration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

public class ServletConfiguration implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        servletContext.addListener(new ContextLoaderListener(context));
        context.register(ApiConfiguration.class);
        context.setServletContext(servletContext);

        servletContext.addFilter("SimpleCorsFilter", SimpleCorsFilter.class).addMappingForUrlPatterns(null, false, "/lms/v1/*");

        ServletRegistration.Dynamic dispatcher = servletContext.addServlet("dispatcher", new DispatcherServlet(context));
        dispatcher.setLoadOnStartup(1);
        dispatcher.setInitParameter("dispatchOptionsRequest", "true");
        dispatcher.addMapping("/lms/v1/*");
    }
}
