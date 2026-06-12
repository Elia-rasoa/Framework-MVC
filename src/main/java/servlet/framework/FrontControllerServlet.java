package servlet.framework;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FrontControllerServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // On indique au navigateur qu'on va lui envoyer du texte simple
        response.setContentType("text/plain;charset=UTF-8");
        
        // Étape 2 : On récupère l'URL
        String urlTapee = request.getRequestURL().toString();
        
        // Étape 3 : On l'affiche
        PrintWriter out = response.getWriter();
        out.println("URL demandee : " + urlTapee);
    }

    // 2. Le point d'entrée pour les requêtes de type GET (ex: charger une page)
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    // 3. Le point d'entrée pour les requêtes de type POST (ex: soumettre un formulaire)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}