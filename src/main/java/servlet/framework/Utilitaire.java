package servlet.framework;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import jakarta.servlet.ServletContext;
import servlet.annotations.UrlMapping;

public class Utilitaire {

    public static Map<String, Mapping> scanControllersAndUrls(String packageName, Class<? extends Annotation> controllerAnnotation, ServletContext context) {
        Map<String, Mapping> urlMap = new HashMap<>();
        try {
            String path = "/WEB-INF/classes/" + packageName.replace('.', '/') + "/";
            Set<String> resourcePaths = context.getResourcePaths(path);
            
            if (resourcePaths != null) {
                for (String resourcePath : resourcePaths) {
                    if (resourcePath.endsWith(".class")) {
                        String fileName = resourcePath.substring(resourcePath.lastIndexOf('/') + 1);
                        String className = packageName + "." + fileName.substring(0, fileName.length() - 6);
                        
                        try {
                            Class<?> clazz = Class.forName(className);
                            
                            // 1. On vérifie si la classe est bien un @Controller
                            if (clazz.isAnnotationPresent(controllerAnnotation)) {
                                
                                // 2. On scanne toutes les méthodes de ce contrôleur
                                for (Method method : clazz.getDeclaredMethods()) {
                                    // 3. Si la méthode possède @UrlMapping, on l'enregistre
                                    if (method.isAnnotationPresent(UrlMapping.class)) {
                                        UrlMapping urlAnno = method.getAnnotation(UrlMapping.class);
                                        String urlValue = urlAnno.value();
                                        
                                        // On stocke l'URL et son Mapping associé
                                        urlMap.put(urlValue, new Mapping(clazz.getName(), method.getName()));
                                    }
                                }
                            }
                        } catch (ClassNotFoundException e) {
                        
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[Framework Utilitaire] Erreur de scan : " + e.getMessage());
        }
        return urlMap;
    }
}