package DevisOracle;
	
import DevisOracle.catalogue.readCatalog;
import DevisOracle.catalogue.readReglesMetier;
import DevisOracle.configuration.readProperties;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;

import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DevisOracleMain extends Application {
	static final Logger log = LogManager.getLogger(DevisOracleMain.class.getName());
	private Parent rootNode;

	@Override
	public void init() throws Exception {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Main.fxml"));
		rootNode = fxmlLoader.load();
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			// lecture des propriétées
			readProperties.getInstance();

			// Affichage de l'interface graphique
			// TabPane rootNode = (TabPane)FXMLLoader.load(getClass().getResource("fxml/Main.fxml"));
			Scene scene = new Scene(rootNode,1500,680);
			scene.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());

			// icone
			primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/hawk_icon.png")));
			primaryStage.setTitle("Faucon Luisant v1.3.5 - 25/11/2020 - Gestion Devis UGAP Oracle");

			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			primaryStage.show();

			TimeUnit.SECONDS.sleep(1);
			// affichage des propriétés

			// lecture des règles business
			try {
				readCatalog.getInstance().getCatalogParRefConstructeur();
				readReglesMetier.getInstance().getHmRegles();
			}

			catch (NullPointerException NPE) {
				//System.out.println("	NULL cell");
				Alert errorAlert = new Alert(Alert.AlertType.ERROR);
				errorAlert.setHeaderText("Règles de métier invalides");
				errorAlert.setContentText("Veuillez revoir la configuration et vérifier le fichier de règles. Relancez ensuite l'application.");
				errorAlert.showAndWait();
				NPE.printStackTrace();
			} catch (Exception e){
				Alert errorAlert = new Alert(Alert.AlertType.ERROR);
				errorAlert.setHeaderText("Règles de métier invalides");
				errorAlert.setContentText("Veuillez revoir la configuration et vérifier le fichier de règles. Relancez ensuite l'application.");
				errorAlert.showAndWait();
				e.printStackTrace();
				log.debug("Main class error", e);
				// System.exit(-1);
			}

		} catch(Exception e) {
			e.printStackTrace();
			log.debug("Main class error", e);
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}

