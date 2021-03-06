package fr.mathieujjava.sevenwonders;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fr.mathieujjava.sevenwonders.enums.Medaille;
import fr.mathieujjava.sevenwonders.enums.Ressource;
import fr.mathieujjava.sevenwonders.enums.TypeAction;
import fr.mathieujjava.sevenwonders.enums.TypeCarte;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/conf-context.xml", "/cartes-context.xml", "/merveilles-context.xml"})
public class PartieManagerImplTest {
  @Resource(name="listeMerveilles")
  List<Merveille> listeMerveilles;
  
  private Partie partie;

  private Joueur joueurA, joueurB, joueurC, joueurD;
  
  private Carte carteScierie, carteUniversite;

  @Autowired
  private PartieManager partieManager;

  @Before
  public void setUp() {
    //partieManager = new PartieManagerImpl();
    Merveille merveilleA =listeMerveilles.get(0);
    partie = new Partie();
    partie.addJoueur(joueurA = new Joueur(merveilleA, true));
    partie.addJoueur(joueurB = new Joueur(merveilleA, true));
    partie.addJoueur(joueurC = new Joueur(merveilleA, true));
    partie.addJoueur(joueurD = new Joueur(merveilleA, true));
    joueurA.getMain().add(carteScierie = new Carte(TypeCarte.MatierePremiere, "Sawmill", "Scierie", new Cout(1), null, "Fournit 2 bois"));
    joueurA.getMain().add(new Carte(TypeCarte.Science, "Library", "Bibliothèque", new Cout(0, Ressource.Pierre, Ressource.Pierre, Ressource.Tissu), null, "Ecriture"));
    carteUniversite = new Carte(TypeCarte.Science, "University", "Université", new Cout(0, Ressource.Bois, Ressource.Bois, Ressource.Papyrus, Ressource.Verre), null, "Ecriture");
  }

  @Test
  public void testDefausseReussie() throws Exception {
    Action action = new Action(joueurA, TypeAction.Defausse, carteScierie);
    
    // on v�rifie la configuration de d�part
    assertEquals(0, partie.getDefausse().size());
    assertEquals(3, joueurA.getNombrePieces().intValue());
    assertEquals(2, joueurA.getMain().size());
    partieManager.effectueAction(partie, action);
    // le joueur gagne 3 pi�ces, et met une carte en d�fausse
    assertEquals(6, joueurA.getNombrePieces().intValue());
    assertEquals(1, partie.getDefausse().size());
    assertEquals(carteScierie, partie.getDefausse().get(0));
    assertEquals(1, joueurA.getMain().size());
  }
  
  @Test(expected=Exception.class)
  public void testDefausseImpossible() throws Exception {
    Action action = new Action(joueurA, TypeAction.Defausse, carteUniversite);
    partieManager.effectueAction(partie, action);
  }
  
  @Test
  public void testPaieCout1() throws Exception {
    ChoixAchatRessources choix = new ChoixAchatRessources();
    partieManager.paieCout(partie,  joueurA, choix);
    assertEquals(3, joueurA.getNombrePieces().intValue());
  }
  
  @Test
  public void testPaieCout2() throws Exception {
    ChoixAchatRessources choix = new ChoixAchatRessources();
    choix.setPiecesPourBanque(2);
    partieManager.paieCout(partie,  joueurA, choix);
    assertEquals(1, joueurA.getNombrePieces().intValue());
  }
  
  @Test
  public void testPaieCout3() throws Exception {
    ChoixAchatRessources choix = new ChoixAchatRessources();
    choix.setPiecesPourDroite(2);
    partieManager.paieCout(partie,  joueurA, choix);
    assertEquals(1, joueurA.getNombrePieces().intValue());
    assertEquals(5, joueurB.getNombrePieces().intValue());
    assertEquals(3, joueurD.getNombrePieces().intValue());
  }
  
  @Test
  public void testPaieCout4() throws Exception {
    // 10 pi�ces en tout
    joueurA.modifieNombrePieces(7);
    ChoixAchatRessources choix = new ChoixAchatRessources();
    choix.setPiecesPourDroite(2);
    choix.setPiecesPourGauche(2);
    partieManager.paieCout(partie,  joueurA, choix);
    assertEquals(6, joueurA.getNombrePieces().intValue());
    assertEquals(5, joueurB.getNombrePieces().intValue());
    assertEquals(5, joueurD.getNombrePieces().intValue());
  }

  @Test
  public void testPaieCout() throws Exception {
    // 14 po en tout
    joueurA.modifieNombrePieces(11);
    ChoixAchatRessources choix = new ChoixAchatRessources();
    choix.setPiecesPourBanque(1);
    choix.setPiecesPourDroite(4);
    choix.setPiecesPourGauche(6);
    // 14 3 3 3 po deviennent 3 9 3 7 po
    partieManager.paieCout(partie,  joueurA, choix);
    assertEquals(3, joueurA.getNombrePieces().intValue());
    assertEquals(7, joueurB.getNombrePieces().intValue());
    assertEquals(3, joueurC.getNombrePieces().intValue());
    assertEquals(9, joueurD.getNombrePieces().intValue());
  }
  
  @Test
  public void testCalculPuissanceMilitaire1() {
    assertEquals(0, partieManager.calculePuissanceMilitaire(partie, joueurA));
  }
  
  @Test
  public void testCalculPuissanceMilitaire2() {
    joueurA.ajouteMedaille(Medaille.Defaite);
    assertEquals(-1, partieManager.calculePuissanceMilitaire(partie, joueurA));
  }

  @Test
  public void testCalculPuissanceMilitaire3() {
    joueurA.ajouteMedaille(Medaille.VictoireII);
    joueurA.ajouteMedaille(Medaille.VictoireIII);
    assertEquals(8, partieManager.calculePuissanceMilitaire(partie, joueurA));
  }

  @Test
  public void testCalculPuissanceMilitaire() {
    Joueur mockedJoueur = Mockito.mock(Joueur.class);
    Merveille mockedMerveille = Mockito.mock(Merveille.class);
    mockedJoueur.ajouteMedaille(Medaille.Defaite);
    Mockito.when(mockedMerveille.getNomEn()).thenReturn("Rhodes Colossus");
    Mockito.when(mockedJoueur.getMerveille()).thenReturn(mockedMerveille);
    Mockito.when(mockedJoueur.getEtageMerveille()).thenReturn(2);
    assertEquals(2, partieManager.calculePuissanceMilitaire(partie, mockedJoueur));
  }
  
  @Test
  public void testDistribution() {
    Partie partie = new Partie();
    partieManager.initPartie(partie, 3);
    assertEquals(7, partie.getJoueur(0).getMain().size());
    assertEquals(7, partie.getJoueur(1).getMain().size());
    assertEquals(7, partie.getJoueur(2).getMain().size());
    
    partie = new Partie();
    partieManager.initPartie(partie, 5);
    assertEquals(7, partie.getJoueur(0).getMain().size());
    assertEquals(7, partie.getJoueur(1).getMain().size());
    assertEquals(7, partie.getJoueur(2).getMain().size());
    assertEquals(7, partie.getJoueur(3).getMain().size());
    assertEquals(7, partie.getJoueur(4).getMain().size());
    
    partie = new Partie();
    partieManager.initPartie(partie, 7);
    assertEquals(7, partie.getJoueur(0).getMain().size());
    assertEquals(7, partie.getJoueur(1).getMain().size());
    assertEquals(7, partie.getJoueur(2).getMain().size());
    assertEquals(7, partie.getJoueur(3).getMain().size());
    assertEquals(7, partie.getJoueur(4).getMain().size());
    assertEquals(7, partie.getJoueur(5).getMain().size());
    assertEquals(7, partie.getJoueur(6).getMain().size());
    
  }
}
