package gui;

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
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Member;
import model.services.MemberService;

public class MemberListController implements Initializable, DataChangeListener {
	private MemberService service;

	@FXML
	private TableView<Member> tableViewMember;

	@FXML
	private TableColumn<Member, Integer> tableColumnId;

	@FXML
	private TableColumn<Member, String> tableColumnName;

	@FXML
	private TableColumn<Member, Member> tableColumnEDIT;

	@FXML
	private TableColumn<Member, Member> tableColumnREMOVE;

	@FXML
	private Button btNew;

	private ObservableList<Member> obsList;

	@FXML
	public void onBtNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Member obj = new Member();
		createDialogForm(obj, "/gui/PlanForm.fxml", parentStage);
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	public void setMemberService(MemberService service) {
		this.service = service;
	}

	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));

		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewMember.prefHeightProperty().bind(stage.heightProperty());
	}

	public void updateTableView() {
		if (service == null) {
			throw new IllegalStateException("Service Null...");
		}
		List<Member> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewMember.setItems(obsList);
		initEditButtons();
		initRemoveButtons();
	}

	private void createDialogForm(Member obj, String absoluteName, Stage parentStage) {
//		try {
//			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
//			Pane pane = loader.load();
//
//			MemberFormController controller = loader.getController();
//			controller.setMember(obj);
//			controller.setMemberervice(new Memberervice());
//			controller.subscribeDataChangeListener(this);
//			controller.updateFormData();
//
//			Stage dialogStage = new Stage();
//			dialogStage.setTitle("Entre com os dados do Membero/Treino");
//			dialogStage.setScene(new Scene(pane));
//			dialogStage.setResizable(false);
//			dialogStage.initOwner(parentStage);
//			dialogStage.initModality(Modality.WINDOW_MODAL);
//			dialogStage.showAndWait();
//		} catch (IOException e) {
//			Alerts.showAlert("IO Exception", "Erro ao carregar view", e.getMessage(), AlertType.ERROR);
//		}
	}

	@Override
	public void onDataChanged() {
		updateTableView();
	}

	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Member, Member>() {
			private final Button button = new Button("editar");

			@Override
			protected void updateItem(Member obj, boolean empty) {
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
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Member, Member>() {
			private final Button button = new Button("remover");

			@Override
			protected void updateItem(Member obj, boolean empty) {
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

	private void removeEntity(Member obj) 
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
