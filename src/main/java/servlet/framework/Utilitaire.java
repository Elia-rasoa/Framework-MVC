package servlet.framework;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import jakarta.servlet.ServletContext;
import servlet.annotations.UrlMapping;

public class Utilitaire {

    public static Map<UrlKey, Mapping> scanControllersAndUrls(String packageName, 
        Class<? extends Annotation> controllerAnnotation, 
        ServletContext context) throws Exception {

        Map<UrlKey, Mapping> urlMap = new HashMap<>();
        
        String path = "/WEB-INF/classes/" + packageName.replace('.', '/') + "/";
        Set<String> resourcePaths = context.getResourcePaths(path);
        
        if (resourcePaths != null) {
            for (String resourcePath : resourcePaths) {
                if (resourcePath.endsWith(".class")) {
                    String fileName = resourcePath.substring(resourcePath.lastIndexOf('/') + 1);
                    String className = packageName + "." + fileName.substring(0, fileName.length() - 6);
                    
                    try {
                        Class<?> clazz = Class.forName(className);
                        
                        if (clazz.isAnnotationPresent(controllerAnnotation)) {
                            for (Method method : clazz.getDeclaredMethods()) {
                                if (method.isAnnotationPresent(UrlMapping.class)) {
                                    UrlMapping urlAnno = method.getAnnotation(UrlMapping.class);
                                    
                                    UrlKey key = new UrlKey(urlAnno.value(), urlAnno.method());
                                    
                                    // VÉRIFICATION D'UNICITÉ : Détection de doublons (Même URL + Même Méthode HTTP)
                                    if (urlMap.containsKey(key)) {
                                        Mapping duplicate = urlMap.get(key);
                                        throw new Exception("L'URL '" + key.getUrl() + "' avec la méthode '" + key.getHttpMethod() + 
                                            "' est déjà associée à " + duplicate.getClassName() + "." + duplicate.getMethod() + "(). " +
                                            "Impossible de l'associer aussi à " + clazz.getName() + "." + method.getName() + "().");
                                    }
                                    
                                    urlMap.put(key, new Mapping(clazz.getName(), method.getName()));
                                }
                            }
                        }
                    } catch (ClassNotFoundException e) {
                        // Ignorer et passer à la classe suivante
                    }
                }
            }
        }
        return urlMap;
    }
}