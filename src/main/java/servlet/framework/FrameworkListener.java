package servlet.framework;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import servlet.annotations.Controller;
import servlet.annotations.UrlMapping;

public class FrameworkListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            // 1. Récupération et sauvegarde du contexte Spring
            WebApplicationContext springContext = WebApplicationContextUtils
                    .getRequiredWebApplicationContext(sce.getServletContext());
            sce.getServletContext().setAttribute("springContext", springContext);
            System.out.println("[Framework] Pont établi avec le contexte Spring avec succès !");

            // 2. Scan de ton package de contrôleurs habituel
            Map<UrlKey, Mapping> urlMappingTable = new HashMap<>();
            String packageScan = sce.getServletContext().getInitParameter("packageScan");
            
            if (packageScan != null) {
                String path = packageScan.replace('.', '/');
                ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                URL resource = classLoader.getResource(path);
                
                if (resource != null) {
                    File directory = new File(resource.getFile());
                    if (directory.exists()) {
                        for (File file : directory.listFiles()) {
                            if (file.getName().endsWith(".class")) {
                                String className = packageScan + "." + file.getName().replace(".class", "");
                                Class<?> clazz = Class.forName(className);
                                
                                if (clazz.isAnnotationPresent(Controller.class)) {
                                    for (Method method : clazz.getDeclaredMethods()) {
                                        if (method.isAnnotationPresent(UrlMapping.class)) {
                                            UrlMapping urlMapping = method.getAnnotation(UrlMapping.class);
                                            UrlKey key = new UrlKey(urlMapping.value(), urlMapping.method());
                                            Mapping mapping = new Mapping(className, method.getName());
                                            urlMappingTable.put(key, mapping);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            sce.getServletContext().setAttribute("urlMappingTable", urlMappingTable);
            System.out.println("[Framework] Scan terminé. " + urlMappingTable.size() + " route(s) chargée(s).");
            
        } catch (Exception e) {
            System.out.println("[Framework] ❌ Erreur d'initialisation du framework : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {}
}