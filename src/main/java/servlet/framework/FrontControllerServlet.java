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
                Class<?> clazz = Class.forName(mapping.getClassName());
                Object controllerInstance = clazz.getDeclaredConstructor().newInstance();
                
                // --- Étape 2.1 : Gestion dynamique des paramètres de la méthode ---
                Method[] methods = clazz.getDeclaredMethods();
                Method methodToInvoke = null;
                for (Method m : methods) {
                    if (m.getName().equals(mapping.getMethod())) {
                        methodToInvoke = m;
                        break;
                    }
                }

                Object result;
                // Si la méthode demande l'objet HttpServletRequest en paramètre, on lui donne
                if (methodToInvoke.getParameterCount() == 1 && methodToInvoke.getParameterTypes()[0].equals(HttpServletRequest.class)) {
                    result = methodToInvoke.invoke(controllerInstance, request);
                } else {
                    result = methodToInvoke.invoke(controllerInstance);
                }
                // -----------------------------------------------------------------
                
                if (result instanceof ModelAndView) {
                    ModelAndView mv = (ModelAndView) result;
                    
                    if (mv.getView() == null || mv.getView().trim().isEmpty()) {
                        response.setContentType("text/plain;charset=UTF-8");
                        PrintWriter out = response.getWriter();
                        out.println("[Framework - Test de variable ModelAndView (Base de données Spring)]");
                        out.println("Voici les variables récupérées via Spring Service : " + mv.getModel());
                    } else {
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
                    out.println("Le contrôleur n'a pas retourné un ModelAndView. Résultat : " + result);
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