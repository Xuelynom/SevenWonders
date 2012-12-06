package fr.mathieujjava.sevenwonders.controller;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import fr.mathieujjava.sevenwonders.Action;
import fr.mathieujjava.sevenwonders.BotService;
import fr.mathieujjava.sevenwonders.Carte;
import fr.mathieujjava.sevenwonders.ChoixAchatRessources;
import fr.mathieujjava.sevenwonders.Joueur;
import fr.mathieujjava.sevenwonders.Partie;
import fr.mathieujjava.sevenwonders.PartieManager;
import fr.mathieujjava.sevenwonders.enums.TypeAction;
import fr.mathieujjava.sevenwonders.form.ActionForm;

/**
 * Handles requests for the application home page.
 */
@Controller
@Scope(value = "request")
public class PartieController {
  private static final Logger logger = LoggerFactory
      .getLogger(PartieController.class);

  @Autowired
  private PartieManager partieManager;

  @Autowired
  private BotService botService;

  @ModelAttribute("actionForm")
  public ActionForm initActionForm() {
    logger.debug("INIT Action FORM");
    ActionForm actionForm = new ActionForm();
    actionForm.setTypeAction(TypeAction.Joue);
    return actionForm;
  }

  @RequestMapping(value = "/partie.action", method = RequestMethod.POST)
  public ModelAndView submitAction(HttpSession session,
      @ModelAttribute("action") ActionForm actionForm, Errors errors,
      Model model) throws Exception {
    logger.debug("Action !!!!!!!!!!!!!! : "
        + actionForm.getTypeAction() + " " + actionForm.getNumCarte() + "]");
    Partie partie = (Partie) session.getAttribute("partie");

    Joueur joueur = partie.getJoueur(1);// actionForm.getNumJoueur());
    TypeAction typeAction = actionForm.getTypeAction();
    Carte carte = null;
    if (actionForm.getNumCarte() != null) {
      carte = joueur.getMain().get(actionForm.getNumCarte());
    } else {
      errors.reject("numCarte", "blabla");
      return new ModelAndView("partie", "partie", partie);
    }
    ChoixAchatRessources car = null;
    if (typeAction == TypeAction.Joue || typeAction == TypeAction.Merveille) {
      car = botService.getCoutTotal(partie, joueur, carte);
    }
    if (typeAction == TypeAction.Defausse || typeAction == TypeAction.Special || car != null) {
      Action actionJoueur = new Action(joueur, typeAction, carte, car);
      logger.debug("Action : " + actionJoueur);
      logger.debug("Partie avant : " + partie);
      partieManager.effectueActionJoueurEtBots(partie, actionJoueur);
      partieManager.finitTour(partie);
      //partie.rotationCartes(true);
      //partie.incrementeTour();
    } else {
      logger.error("____________Pas possible de jouer cette carte");
      errors.reject("numCarte", "blabla");
    }
    logger.debug("Partie apr�s : " + partie);
    if (partie.getAge() == 4) {
      ModelAndView mav = new ModelAndView("resultat", "partie", partie);
      HashMap<Joueur, List<Integer>> resultat = partieManager.comptePoints(partie);
      
      mav.addObject("resultat", resultat);
      return mav;
    } else {
      return new ModelAndView("partie", "partie", partie);
    }
  }

}
