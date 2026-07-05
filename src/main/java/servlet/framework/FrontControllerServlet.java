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
        
        // 1. Récupération de l'URL demandée et de la méthode HTTP (GET, POST, etc.)
        String contextPath = request.getContextPath();
        String requestURI = request.getRequestURI();
        String urlTapee = requestURI.substring(contextPath.length());
        String methodeHttpTapee = request.getMethod(); // "GET" ou "POST"
        
        out.println("Requête reçue : [" + methodeHttpTapee + "] " + urlTapee);
        out.println("------------------------------------------------------------------");
        
        // 2. Récupération de la table construite par le Listener
        @SuppressWarnings("unchecked")
        Map<UrlKey, Mapping> urlMappingMap = (Map<UrlKey, Mapping>) this.getServletContext().getAttribute("urlMappingTable");

        if (urlMappingMap == null) {
            out.println("ERREUR INTERNE : La table de routage n'a pas été initialisée par le Listener.");
            return;
        }

        // 3. Création de la clé correspondante pour la recherche dans la Map
        UrlKey cleRecherche = new UrlKey(urlTapee, methodeHttpTapee);

        if (urlMappingMap.containsKey(cleRecherche)) {
            Mapping mapping = urlMappingMap.get(cleRecherche);
            
            out.println("[Framework] Correspondance trouvée !");
            out.println("Contrôleur ciblé  : " + mapping.getClassName());
            out.println("Méthode à appeler : " + mapping.getMethod() + "()");
            
        } else {
            // Gestion de l'erreur 404 si la clé n'existe pas
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.println("ERREUR 404 : La route '" + urlTapee + "' pour la méthode [" + methodeHttpTapee + "] n'existe pas.");
            out.println("------------------------------------------------------------------");
            out.println("Liste des URLs disponibles dans votre projet :");
            
            if (urlMappingMap.isEmpty()) {
                out.println(" -> Aucune URL n'a été configurée.");
            } else {
                for (Map.Entry<UrlKey, Mapping> entry : urlMappingMap.entrySet()) {
                    UrlKey key = entry.getKey();
                    Mapping map = entry.getValue();
                    out.println(" * [" + key.getHttpMethod() + "] " + key.getUrl() + "  =>  " + map.getClassName() + "." + map.getMethod() + "()");
                }
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }
}