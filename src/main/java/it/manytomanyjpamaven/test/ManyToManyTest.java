package it.manytomanyjpamaven.test;

import java.util.Date;
import java.util.List;

import it.manytomanyjpamaven.dao.EntityManagerUtil;
import it.manytomanyjpamaven.model.Ruolo;
import it.manytomanyjpamaven.model.StatoUtente;
import it.manytomanyjpamaven.model.Utente;
import it.manytomanyjpamaven.service.MyServiceFactory;
import it.manytomanyjpamaven.service.RuoloService;
import it.manytomanyjpamaven.service.UtenteService;

public class ManyToManyTest {

	public static void main(String[] args) {
		UtenteService utenteServiceInstance = MyServiceFactory.getUtenteServiceInstance();
		RuoloService ruoloServiceInstance = MyServiceFactory.getRuoloServiceInstance();
		

		// ora passo alle operazioni CRUD
		try {

			// inizializzo i ruoli sul db
			initRuoli(ruoloServiceInstance);

			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");

			testInserisciNuovoUtente(utenteServiceInstance);
			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");

			testRimuoviRuoloDaUtente(ruoloServiceInstance, utenteServiceInstance);
			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");

			testCollegaUtenteARuoloEsistente(ruoloServiceInstance, utenteServiceInstance);
			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");

			testModificaStatoUtente(utenteServiceInstance);
			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");

			testRimuoviRuolo(ruoloServiceInstance);
			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");
			
			testFindAllByDataCreazione(utenteServiceInstance);
			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");
			
			testContaAdmin(utenteServiceInstance);
			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");
			
			testFindAllByPasswordMinoreDiOtto(utenteServiceInstance);
			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");
			
			testCheckSeAdminTraUtentiDisabilitati(ruoloServiceInstance, utenteServiceInstance);
			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");
			
			testFindDescrizioneRuoloConUtente(ruoloServiceInstance, utenteServiceInstance);
			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// questa è necessaria per chiudere tutte le connessioni quindi rilasciare il
			// main
			EntityManagerUtil.shutdown();
		}

	}

	private static void initRuoli(RuoloService ruoloServiceInstance) throws Exception {
		if (ruoloServiceInstance.cercaPerDescrizioneECodice("Administrator", "ROLE_ADMIN") == null) {
			ruoloServiceInstance.inserisciNuovo(new Ruolo("Administrator", "ROLE_ADMIN"));
		}

		if (ruoloServiceInstance.cercaPerDescrizioneECodice("Classic User", "ROLE_CLASSIC_USER") == null) {
			ruoloServiceInstance.inserisciNuovo(new Ruolo("Classic User", "ROLE_CLASSIC_USER"));
		}
	}

	private static void testInserisciNuovoUtente(UtenteService utenteServiceInstance) throws Exception {
		System.out.println(".......testInserisciNuovoUtente inizio.............");

		Utente utenteNuovo = new Utente("pippo.rossi", "xxx", "pippo", "rossi", new Date());
		utenteServiceInstance.inserisciNuovo(utenteNuovo);
		if (utenteNuovo.getId() == null)
			throw new RuntimeException("testInserisciNuovoUtente fallito ");

		System.out.println(".......testInserisciNuovoUtente fine: PASSED.............");
	}

	private static void testCollegaUtenteARuoloEsistente(RuoloService ruoloServiceInstance,
			UtenteService utenteServiceInstance) throws Exception {
		System.out.println(".......testCollegaUtenteARuoloEsistente inizio.............");

		Ruolo ruoloEsistenteSuDb = ruoloServiceInstance.cercaPerDescrizioneECodice("Administrator", "ROLE_ADMIN");
		if (ruoloEsistenteSuDb == null)
			throw new RuntimeException("testCollegaUtenteARuoloEsistente fallito: ruolo inesistente ");

		// mi creo un utente inserendolo direttamente su db
		Utente utenteNuovo = new Utente("mario.bianchi", "JJJ", "mario", "bianchi", new Date());
		utenteServiceInstance.inserisciNuovo(utenteNuovo);
		if (utenteNuovo.getId() == null)
			throw new RuntimeException("testInserisciNuovoUtente fallito: utente non inserito ");

		utenteServiceInstance.aggiungiRuolo(utenteNuovo, ruoloEsistenteSuDb);
		// per fare il test ricarico interamente l'oggetto e la relazione
		Utente utenteReloaded = utenteServiceInstance.caricaUtenteSingoloConRuoli(utenteNuovo.getId());
		if (utenteReloaded.getRuoli().size() != 1)
			throw new RuntimeException("testInserisciNuovoUtente fallito: ruoli non aggiunti ");

		System.out.println(".......testCollegaUtenteARuoloEsistente fine: PASSED.............");
	}

	private static void testRimuoviRuoloDaUtente(RuoloService ruoloServiceInstance, UtenteService utenteServiceInstance)
			throws Exception {
		System.out.println(".......testRimuoviRuoloDaUtente inizio.............");

		Ruolo ruoloEsistenteSuDb = ruoloServiceInstance.cercaPerDescrizioneECodice("Administrator", "ROLE_ADMIN");
		if (ruoloEsistenteSuDb == null)
			throw new RuntimeException("testRimuoviRuoloDaUtente fallito: ruolo inesistente ");

		// mi creo un utente inserendolo direttamente su db
		Utente utenteNuovo = new Utente("carlo.conti", "aaa", "carlo", "conti", new Date());
		utenteServiceInstance.inserisciNuovo(utenteNuovo);
		if (utenteNuovo.getId() == null)
			throw new RuntimeException("testRimuoviRuoloDaUtente fallito: utente non inserito ");
		utenteServiceInstance.aggiungiRuolo(utenteNuovo, ruoloEsistenteSuDb);
		
		utenteServiceInstance.rimuoviRuolo(utenteNuovo, ruoloEsistenteSuDb);
		// per fare il test ricarico interamente l'oggetto e la relazione
		Utente utenteReloaded = utenteServiceInstance.caricaUtenteSingoloConRuoli(utenteNuovo.getId());
		if (utenteReloaded.getRuoli().size() != 1)
			throw new RuntimeException("testRimuoviRuoloDaUtente fallito: ruolo non rimosso ");

		System.out.println(".......testRimuoviRuoloDaUtente fine: PASSED.............");
	}

	private static void testModificaStatoUtente(UtenteService utenteServiceInstance) throws Exception {
		System.out.println(".......testModificaStatoUtente inizio.............");

		// mi creo un utente inserendolo direttamente su db
		Utente utenteNuovo = new Utente("mario1.bianchi1", "JJJ", "mario1", "bianchi1", new Date());
		utenteServiceInstance.inserisciNuovo(utenteNuovo);
		if (utenteNuovo.getId() == null)
			throw new RuntimeException("testModificaStatoUtente fallito: utente non inserito ");

		// proviamo a passarlo nello stato ATTIVO ma salviamoci il vecchio stato
		StatoUtente vecchioStato = utenteNuovo.getStato();
		utenteNuovo.setStato(StatoUtente.ATTIVO);
		utenteServiceInstance.aggiorna(utenteNuovo);

		if (utenteNuovo.getStato().equals(vecchioStato))
			throw new RuntimeException("testModificaStatoUtente fallito: modifica non avvenuta correttamente ");

		System.out.println(".......testModificaStatoUtente fine: PASSED.............");
	}

	private static void testRimuoviRuolo(RuoloService ruoloServiceInstance)
			throws Exception {

		System.out.println(".......testRimuovi inizio.............");
		Ruolo ruoloDaRimuovere=new Ruolo("Project manager", "PRJMNG");
		ruoloServiceInstance.inserisciNuovo(ruoloDaRimuovere);
		
		if (ruoloDaRimuovere == null)
			throw new RuntimeException("testRimuoviRuolo fallito: ruolo inesistente ");
		
		ruoloServiceInstance.rimuovi(ruoloDaRimuovere);
		
		if(ruoloServiceInstance.listAll().size()==3)
			throw new RuntimeException("testRimuovi fallito");
		
		System.out.println(".......testRimuovi fine: PASSED.............");
	}
	
	private static void testFindAllByDataCreazione(UtenteService utenteServiceInstance)throws Exception{
		System.out.println(".......testFindAllByDataCreazionePrimaDi inizio.............");
		
		Utente utenteNuovo = new Utente("tottigol", "JJJ", "fra", "totti", new Date("2021/06/23"));
		utenteServiceInstance.inserisciNuovo(utenteNuovo);
		
		if(utenteServiceInstance.findAllByCreazione().size()!=1)
			throw new RuntimeException("testFindAllByDataCreazionePrimaDi fallito ");
		
		utenteServiceInstance.rimuovi(utenteServiceInstance.caricaSingoloElemento(utenteNuovo.getId()));
		System.out.println(".......testFindAllByDataCreazionePrimaDi fine: PASSED.............");
	}
	
	private static void testContaAdmin(UtenteService utenteServiceInstance) throws Exception{
		System.out.println(".......testContaAdmin inizio.............");
		
		Long admins=null;
		admins=utenteServiceInstance.countAdmin();
		if(admins==null)
			throw new RuntimeException("testContaAdmin fallito: non ci sono admin ");
		
		System.out.println("Admin:"+admins);
		System.out.println(".......testContaAdmin fine: PASSED.............");
	}
	
	private static void testFindAllByPasswordMinoreDiOtto(UtenteService utenteServiceInstance) throws Exception{
		
		System.out.println(".......testfindAllByPasswordMinoreDiOtto inizio.............");
		
		Utente utenteNuovo = new Utente("tarlo.carlo", "carlo", "carlo1", "bianchi1", new Date());
		utenteServiceInstance.inserisciNuovo(utenteNuovo);

		List<Utente> risulatiRicercaConPassword=utenteServiceInstance.findByPasswordMinoreDiOtto();
		if(risulatiRicercaConPassword.size()==0)
			throw new RuntimeException("testfindAllByPasswordMinoreDiOtto fallito: non ci sono admin ");

		System.out.println(".......testfindAllByPasswordMinoreDiOtto fine: PASSED.............");
	}
	
	private static void testCheckSeAdminTraUtentiDisabilitati(RuoloService ruoloServiceInstance, UtenteService utenteServiceInstance)
			throws Exception {
		
		System.out.println(".......testCheckSeAdminTraUtentiDisabilitati inizio.............");
		
		Ruolo ruoloEsistenteSuDb = ruoloServiceInstance.cercaPerDescrizioneECodice("Administrator", "ROLE_ADMIN");
		if (ruoloEsistenteSuDb == null)
			throw new RuntimeException("testCheckSeAdminTraUtentiDisabilitati fallito: ruolo inesistente ");
		
		Utente utenteNuovo = new Utente("filippo.carlo", "Avena", "Filcarl", "Carlo", new Date());
		StatoUtente nuovoStato=StatoUtente.DISABILITATO;
		
		utenteNuovo.setStato(nuovoStato);
		utenteServiceInstance.aggiungiRuolo(utenteNuovo, ruoloEsistenteSuDb);
		utenteServiceInstance.inserisciNuovo(utenteNuovo);
		
		if(!utenteServiceInstance.checkSeAlmenoUnAdminTraUtentiDisabilitati())
			throw new RuntimeException("testCheckSeAdminTraUtentiDisabilitati fallito: ruolo inesistente ");

		System.out.println(".......testCheckSeAdminTraUtentiDisabilitati fine: PASSED.............");
	}
	
	private static void testFindDescrizioneRuoloConUtente(RuoloService ruoloServiceInstance, UtenteService utenteServiceInstance)
			throws Exception {
		System.out.println(".......testFindDescrizioneRuoloConUtente inizio.............");
		
		List<String> risultatoRicercaDescrizioni=ruoloServiceInstance.findDescrizioneRuoloConUtente();
		if(risultatoRicercaDescrizioni.size()==0)
			throw new RuntimeException("testFindDescrizioneRuoloConUtente fallito: record inesistenti ");

		System.out.println("Descrizioni:"+ risultatoRicercaDescrizioni.size());
		System.out.println(".......testFindDescrizioneRuoloConUtente fine: PASSED.............");
	}
}
