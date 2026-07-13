package servlet.framework;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Map;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FrontControllerServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. On lit les configurations dans le web.xml du projet de test
        String prefix = this.getServletContext().getInitParameter("viewPrefix");
        String suffix = this.getServletContext().getInitParameter("viewSuffix");
        
        String contextPath = request.getContextPath();
        String requestURI = request.getRequestURI();
        String urlTapee = requestURI.substring(contextPath.length());
        String methodeHttpTapee = request.getMethod(); 

        @SuppressWarnings("unchecked")
        Map<UrlKey, Mapping> urlMappingMap = (Map<UrlKey, Mapping>) this.getServletContext().getAttribute("urlMappingTable");

        UrlKey cleRecherche = new UrlKey(urlTapee, methodeHttpTapee);

        if (urlMappingMap != null && urlMappingMap.containsKey(cleRecherche)) {
            Mapping mapping = urlMappingMap.get(cleRecherche);
            
            try {
                // 2. Instanciation du contrôleur de test par réflexion
                Class<?> clazz = Class.forName(mapping.getClassName());
                Object controllerInstance = clazz.getDeclaredConstructor().newInstance();
                Method methodToInvoke = clazz.getDeclaredMethod(mapping.getMethod());
                
                // 3. Exécution de la méthode qui renvoie le ModelAndView
                Object result = methodToInvoke.invoke(controllerInstance);
                
                if (result instanceof ModelAndView) {
                    ModelAndView mv = (ModelAndView) result;
                    
                    // CAS 1 : La vue est null ou vide (On teste UNIQUEMENT avec nos variables)
                    if (mv.getView() == null || mv.getView().trim().isEmpty()) {
                        response.setContentType("text/plain;charset=UTF-8");
                        PrintWriter out = response.getWriter();
                        out.println("[Framework - Test de variable ModelAndView]");
                        out.println("Aucune page demandée (la vue est null).");
                        out.println("Voici les variables stockées dans le modèle : " + mv.getModel());
                    } 
                    // CAS 2 : La vue est renseignée (On gère le fichier d'affichage réel)
                    else {
                        String cheminCompletJSP = prefix + mv.getView() + suffix;
                        
                        if (mv.getModel() != null) {
                            for (Map.Entry<String, Object> entry : mv.getModel().entrySet()) {
                                request.setAttribute(entry.getKey(), entry.getValue());
                            }
                        }
                        
                        RequestDispatcher dispatcher = request.getRequestDispatcher(cheminCompletJSP);
                        dispatcher.forward(request, response);
                    }
                } else {
                    response.setContentType("text/plain;charset=UTF-8");
                    PrintWriter out = response.getWriter();
                    out.println("Le contrôleur n'a pas retourné un ModelAndView.");
                }

            } catch (Exception e) {
                response.setContentType("text/plain;charset=UTF-8");
                PrintWriter out = response.getWriter();
                out.println("❌ Erreur d'exécution : " + e.getMessage());
                e.printStackTrace(out);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.setContentType("text/plain;charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("ERREUR 404 : Route introuvable.");
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