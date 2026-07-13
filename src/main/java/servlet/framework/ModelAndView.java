package servlet.framework;

import java.util.HashMap;
import java.util.Map;

public class ModelAndView {
    private String view; // Le nom ou chemin de la vue (ex: "fiche-employe")
    private Map<String, Object> model; // Les données à envoyer à la vue

    public ModelAndView() {
        this.model = new HashMap<>();
    }

    public ModelAndView(String view) {
        this.view = view;
        this.model = new HashMap<>();
    }

    public String getView() { return view; }
    public void setView(String view) { this.view = view; }

    public Map<String, Object> getModel() { return model; }

    // Méthode utilitaire pour ajouter facilement une donnée
    public void addObject(String attributeName, Object attributeValue) {
        this.model.put(attributeName, attributeValue);
    }
}