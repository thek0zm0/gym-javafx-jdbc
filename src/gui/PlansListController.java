package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Plans;
import model.services.PlanService;

public class PlansListController implements Initializable, DataChangeListener {
	private PlanService service;

	@FXML
	private TableView<Plans> tableViewPlan;

	@FXML
	private TableColumn<Plans, Integer> tableColumnId;

	@FXML
	private TableColumn<Plans, String> tableColumnName;

	@FXML
	private TableColumn<Plans, Plans> tableColumnEDIT;

	@FXML
	private TableColumn<Plans, Plans> tableColumnREMOVE;

	@FXML
	private Button btNew;

	private ObservableList<Plans> obsList;

	@FXML
	public void onBtNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Plans obj = new Plans();
		createDialogForm(obj, "/gui/PlanForm.fxml", parentStage);
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	public void setPlanService(PlanService service) {
		this.service = service;
	}

	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));

		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewPlan.prefHeightProperty().bind(stage.heightProperty());
	}

	public void updateTableView() {
		if (service == null) {
			throw new IllegalStateException("Service Null...");
		}
		List<Plans> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewPlan.setItems(obsList);
		initEditButtons();
		initRemoveButtons();
	}

	private void createDialogForm(Plans obj, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			PlanFormController controller = loader.getController();
			controller.setPlan(obj);
			controller.setPlanService(new PlanService());
			controller.subscribeDataChangeListener(this);
			controller.updateFormData();

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Entre com os dados do Plano/Treino");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();
		} catch (IOException e) {
			Alerts.showAlert("IO Exception", "Erro ao carregar view", e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void onDataChanged() {
		updateTableView();
	}

	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Plans, Plans>() {
			private final Button button = new Button("editar");

			@Override
			protected void updateItem(Plans obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> createDialogForm(obj, "/gui/PlanForm.fxml", Utils.currentStage(event)));
			}
		});
	}

	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Plans, Plans>() {
			private final Button button = new Button("remover");

			@Override
			protected void updateItem(Plans obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}
		});
	}

	private void removeEntity(Plans obj) 
	{
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "Certeza?");
		
		if(result.get()== ButtonType.OK)
		{
			if(service==null)
			{
				throw new IllegalStateException("Service nulo...");
			}
			try
			{
				service.remove(obj);
				updateTableView();
			}
			catch(DbIntegrityException e)
			{
				Alerts.showAlert("Erro ao remover objeto", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}

}
