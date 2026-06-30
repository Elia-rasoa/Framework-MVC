package servlet.framework;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FrontControllerServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        // Extraction de l'URL relative
        String contextPath = request.getContextPath();
        String requestURI = request.getRequestURI();
        String urlTapee = requestURI.substring(contextPath.length());
        
        out.println("URL demandée : " + urlTapee);
        out.println("------------------------------------------------------------------");
        
        // Récupération de la table de mapping stockée par le Listener dans le ServletContext
        @SuppressWarnings("unchecked")
        Map<String, Mapping> urlMappingMap = (Map<String, Mapping>) this.getServletContext().getAttribute("urlMappingTable");

        // Sécurité au cas où le listener n'aurait rien partagé
        if (urlMappingMap == null) {
            out.println("ERREUR INTERNE : La table de routage du framework n'est pas initialisée.");
            return;
        }

        // Vérification de l'existence de l'URL
        if (urlMappingMap.containsKey(urlTapee)) {
            Mapping mapping = urlMappingMap.get(urlTapee);
            
            out.println("[Framework] URL Associée avec succès !");
            out.println("Contrôleur : " + mapping.getClassName());
            out.println("Méthode invoquée : " + mapping.getMethod() + "()");
            
        } else {
            // Gestion de l'erreur 404
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.println("ERREUR 404 : L'URL '" + urlTapee + "' n'existe pas dans ce framework.");
            out.println("------------------------------------------------------------------");
            out.println("Liste des URLs disponibles dans votre projet de test :");
            
            if (urlMappingMap.isEmpty()) {
                out.println(" -> Aucune URL n'a été configurée dans les contrôleurs.");
            } else {
                for (Map.Entry<String, Mapping> entry : urlMappingMap.entrySet()) {
                    out.println(" * URL : " + entry.getKey() + "  =>  Méthode : " + entry.getValue().getClassName() + "." + entry.getValue().getMethod() + "()");
                }
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}