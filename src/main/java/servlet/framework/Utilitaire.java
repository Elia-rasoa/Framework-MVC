package servlet.framework;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import jakarta.servlet.ServletContext;

public class Utilitaire {

    public static List<Class<?>> findAnnotatedClasses(String packageName, Class<? extends Annotation> annotation, ServletContext context) {
        List<Class<?>> annotatedClasses = new ArrayList<>();
        try {
            // Convertit "etu.controller" en "/WEB-INF/classes/etu/controller/"
            String path = "/WEB-INF/classes/" + packageName.replace('.', '/') + "/";
            
            // Demande à Tomcat la liste des fichiers dans ce dossier virtuel
            Set<String> resourcePaths = context.getResourcePaths(path);
            
            if (resourcePaths != null) {
                for (String resourcePath : resourcePaths) {
                    // On n'analyse que les fichiers compilés .class
                    if (resourcePath.endsWith(".class")) {
                        // Extrait le nom du fichier (ex: Test1.class)
                        String fileName = resourcePath.substring(resourcePath.lastIndexOf('/') + 1);
                        // Reconstitue le nom complet qualifié (ex: etu.controller.Test1)
                        String className = packageName + "." + fileName.substring(0, fileName.length() - 6);
                        
                        try {
                            Class<?> clazz = Class.forName(className);
                            // Vérification : est-ce que l'annotation est présente sur la classe ?
                            if (clazz.isAnnotationPresent(annotation)) {
                                annotatedClasses.add(clazz);
                            }
                        } catch (ClassNotFoundException e) {
                            // Ignore si la classe ne peut pas être chargée
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[Framework Utilitaire] Erreur de scan : " + e.getMessage());
        }
        return annotatedClasses;
    }
}