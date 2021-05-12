package gui;

import java.net.URL;
import java.util.ResourceBundle;

import application.Main;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import mode.entities.Plans;

public class PlansListController implements Initializable
{
	@FXML
	private TableView<Plans> tableViewPlan;
	
	@FXML
	private TableColumn<Plans, Integer> tableColumnId;
	
	@FXML
	private TableColumn<Plans, String> tableColumnName;
	
	@FXML
	private Button btNew;
	
	@FXML
	public void onBtNewAction()
	{
		System.out.println("Bt new pressed");
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) 
	{
		initializeNodes();
	}

	private void initializeNodes() 
	{
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewPlan.prefHeightProperty().bind(stage.heightProperty());
		
	}
}
