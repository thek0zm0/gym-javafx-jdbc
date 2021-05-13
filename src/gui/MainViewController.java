package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import model.services.PlanService;

public class MainViewController implements Initializable
{
	@FXML
	private MenuItem menuItemMember;
	@FXML
	private MenuItem menuItemPlan;
	@FXML
	private MenuItem menuItemAbout;
	
	@Override
	public void initialize(URL url, ResourceBundle rb) 
	{
		
	}
	
	@FXML
	public void onMenuItemMemberAction() 
	{
		System.out.println("Aluns Pressed");
	}
	
	@FXML
	public void onMenuItemPlanAction() 
	{
		loadView("/gui/PlansList.fxml", (PlansListController controller) -> 
		{
			controller.setPlanService(new PlanService());
			controller.updateTableView();
		});
	}
	
	@FXML
	public void onMenuItemAboutAction() 
	{
		loadView("/gui/About.fxml", x->{});
	}
	
	private synchronized <T> void loadView(String absoluteName, Consumer<T> InitializingAction)
	{
		try 
		{
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			VBox newVbox = loader.load();
			
			Scene mainScene = Main.getMainScene();
			VBox mainVBox = (VBox)((ScrollPane) mainScene.getRoot()).getContent();
			Node mainMenu = mainVBox.getChildren().get(0);
			mainVBox.getChildren().clear();
			mainVBox.getChildren().add(mainMenu);
			mainVBox.getChildren().addAll(newVbox.getChildren());
			
			T controller = loader.getController();
			InitializingAction.accept(controller);
		}
		catch (IOException e)
		{
			Alerts.showAlert("IO Exception", "Erro ao carregar view", e.getMessage(), AlertType.ERROR);
		}
	}
	
}
