package servlet.framework;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import servlet.annotations.Controller;

public class FrontControllerServlet extends HttpServlet {

    // Attribut demandé pour stocker les noms des contrôleurs trouvés
    private List<String> listController = new ArrayList<>();

    public void init() throws ServletException {
        System.out.println("[Framework] Initialisation du FrontControllerServlet...");
        this.listController.clear();

        // Récupération du nom du package à scanner depuis le fichier web.xml
        String packageToScan = getInitParameter("packageScan");

        if (packageToScan != null && !packageToScan.trim().isEmpty()) {
            // Appel de la méthode utilitaire en lui passant le ServletContext
            List<Class<?>> controllersFound = Utilitaire.findAnnotatedClasses(packageToScan, Controller.class, this.getServletContext());
            
            for (Class<?> clazz : controllersFound) {
                this.listController.add(clazz.getName());
            }
        }
        System.out.println("[Framework] Scan terminé. Contrôleurs stockés : " + this.listController.size());
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        String urlTapee = request.getRequestURL().toString();
        out.println("URL demandee : " + urlTapee);
        out.println("----------------------------------------");
        out.println("Liste des contrôleurs détectés par le Framework :");
        
        if (this.listController.isEmpty()) {
            out.println("Aucun contrôleur trouvé.");
        } else {
            for (String controllerName : this.listController) {
                out.println("- " + controllerName);
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