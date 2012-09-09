package fr.mathieujjava.sevenwonders;

import fr.mathieujjava.sevenwonders.enums.TypeAction;

public class Action {
  Joueur joueur;

  TypeAction typeAction;

  Carte carte;

  ChoixAchatRessources choixAchatRessources;

  public ChoixAchatRessources getChoixAchatRessources() {
    return choixAchatRessources;
  }

  public Action(Joueur joueur, TypeAction typeAction, Carte carte) {
    super();
    this.joueur = joueur;
    this.typeAction = typeAction;
    this.carte = carte;
  }

  public Action(Joueur joueur, TypeAction typeAction, Carte carte, ChoixAchatRessources choixAchatRessources) {
    super();
    this.joueur = joueur;
    this.typeAction = typeAction;
    this.carte = carte;
    this.choixAchatRessources = choixAchatRessources;
  }

  public TypeAction getTypeAction() {
    return typeAction;
  }

  public Carte getCarte() {
    return carte;
  }

  public Joueur getJoueur() {
    return joueur;
  }

}
