package servlet.framework;

import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import servlet.annotations.Controller;

@WebListener
public class FrameworkListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("[Framework] Initialisation via FrameworkListener...");
        ServletContext context = sce.getServletContext();

        // Récupère le paramètre global du contexte (ex: configuré dans le web.xml)
        String packageToScan = context.getInitParameter("packageScan");
        Map<String, Mapping> urlMappingMap = new HashMap<>();

        if (packageToScan != null && !packageToScan.trim().isEmpty()) {
            // Utilisation de votre classe utilitaire
            urlMappingMap = Utilitaire.scanControllersAndUrls(packageToScan, Controller.class, context);
        }

        System.out.println("[Framework] Listener terminé. Nombre d'URLs chargées : " + urlMappingMap.size());

        // On stocke la Map dans le contexte de l'application pour que la Servlet puisse y accéder
        context.setAttribute("urlMappingTable", urlMappingMap);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("[Framework] Arrêt de l'application.");
    }
}