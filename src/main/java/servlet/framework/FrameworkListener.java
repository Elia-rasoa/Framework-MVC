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
        System.out.println("[Framework] Initialisation via FrameworkListener au démarrage...");
        ServletContext context = sce.getServletContext();

        String packageToScan = context.getInitParameter("packageScan");
        Map<UrlKey, Mapping> urlMappingMap = new HashMap<>();

        if (packageToScan != null && !packageToScan.trim().isEmpty()) {
            try {
                urlMappingMap = Utilitaire.scanControllersAndUrls(packageToScan, Controller.class, context);
                System.out.println("[Framework] Scan terminé avec succès. " + urlMappingMap.size() + " URLs chargées.");
            } catch (Exception e) {
                System.err.println(" [FRAMEWORK ERREUR CRITIQUE DE ROUTAGE] : " + e.getMessage());
                // On lève une RuntimeException pour stopper le déploiement de Tomcat si conflit il y a
                throw new RuntimeException(e);
            }
        }

        // On partage la table de routage globale au contexte de l'application
        context.setAttribute("urlMappingTable", urlMappingMap);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("[Framework] Arrêt de l'application.");
    }
}