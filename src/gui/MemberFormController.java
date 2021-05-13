package gui;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Member;
import model.entities.Plans;
import model.exceptions.ValidationException;
import model.services.MemberService;
import model.services.PlanService;

public class MemberFormController implements Initializable {
	private Member entity;

	private MemberService service;

	private PlanService planService;

	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

	@FXML
	private TextField txtId;

	@FXML
	private TextField txtName;

	@FXML
	private TextField txtEmail;

	@FXML
	private DatePicker dpBirthDate;

	@FXML
	private TextField txtWeight;

	@FXML
	private ComboBox<Plans> comboBoxPlan;

	@FXML
	private Label labelErrorName;

	@FXML
	private Label labelErrorEmail;

	@FXML
	private Label labelErrorBirthDate;

	@FXML
	private Label labelErrorWeight;

	@FXML
	private Button btSave;

	@FXML
	private Button btCancel;

	private ObservableList<Plans> obsList;

	public void setMember(Member entity) {
		this.entity = entity;
	}

	public void setMemberServices(MemberService service, PlanService planService) {
		this.service = service;
		this.planService = planService;
	}

	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}

	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if (entity == null) {
			throw new IllegalStateException("Entidade esta null");
		}
		if (service == null) {
			throw new IllegalStateException("Service esta nulo");
		}
		try {
			entity = getFormData();
			service.saveOrUpdate(entity);
			notifyDataChangeListeners();
			Utils.currentStage(event).close();
		} catch (ValidationException e) {
			setErrorMessages(e.getErrors());
		} catch (DbException e) {
			Alerts.showAlert("Erro ao salvar no banco", null, e.getMessage(), AlertType.ERROR);
		}
	}

	private void notifyDataChangeListeners() {
		for (DataChangeListener listeners : dataChangeListeners) {
			listeners.onDataChanged();
		}
	}

	private Member getFormData() {
		Member obj = new Member();

		ValidationException exception = new ValidationException("Erro de validation...");

		obj.setId((Utils.tryParseToInt(txtId.getText())));

		if (txtName.getText() == null || txtName.getText().trim().equals("")) {
			exception.addError("name", "Campo nome vazio.");
		}
		
		if (txtEmail.getText() == null || txtEmail.getText().trim().equals("")) {
			exception.addError("email", "Campo email vazio.");
		}
		
		if(dpBirthDate.getValue()==null)
		{
			exception.addError("birthDate", "Campo data nao pode ser vazio.");
		}
		else
		{
			Instant instant = Instant.from(dpBirthDate.getValue().atStartOfDay(ZoneId.systemDefault()));
			obj.setBirthDate(Date.from(instant));
		}
		
		if (txtWeight.getText() == null || txtWeight.getText().trim().equals("")) {
			exception.addError("weight", "Campo peso vazio.");
		}
		
		obj.setWeight(Utils.tryParseToDouble(txtWeight.getText()));
		
		obj.setName(txtName.getText());
		obj.setEmail(txtEmail.getText());
		obj.setPlans(comboBoxPlan.getValue());

		if (exception.getErrors().size() > 0) {
			throw exception;
		}
		return obj;
	}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 30);
		Constraints.setTextFieldDouble(txtWeight);
		Constraints.setTextFieldMaxLength(txtEmail, 40);
		Utils.formatDatePicker(dpBirthDate, "dd/MM/yyyy");
		initializeComboBoxDepartment();
	}

	public void updateFormData() {
		if (entity == null) 
		{
			throw new IllegalStateException("Entidade nula.....");
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
		txtEmail.setText(entity.getEmail());
		Locale.setDefault(Locale.US);
		txtWeight.setText(String.format("%.2f", entity.getWeight()));
		if (entity.getBirthDate() != null) 
		{
			dpBirthDate.setValue(LocalDate.ofInstant(entity.getBirthDate().toInstant(), ZoneId.systemDefault()));
		}
		if(entity.getPlans()==null)
		{
			comboBoxPlan.getSelectionModel().selectFirst();
		}
		else
		{
			comboBoxPlan.setValue(entity.getPlans());
		}
	}

	public void loadAssociatedObjects() {
		if (planService == null) {
			throw new IllegalStateException("PlanService vazio");
		}
		List<Plans> list = planService.findAll();
		obsList = FXCollections.observableArrayList(list);
		comboBoxPlan.setItems(obsList);
	}

	private void setErrorMessages(Map<String, String> errors) 
	{
		Set<String> fields = errors.keySet();

		labelErrorName.setText(fields.contains("name")? errors.get("name") : "" );
		
		labelErrorEmail.setText(fields.contains("email")? errors.get("email") : "" );
		
		labelErrorWeight.setText(fields.contains("weight")? errors.get("weight") : "" );
		
		labelErrorBirthDate.setText(fields.contains("birthDate")? errors.get("birthDate") : "" );
		
	}

	private void initializeComboBoxDepartment() {
		Callback<ListView<Plans>, ListCell<Plans>> factory = lv -> new ListCell<Plans>() {
			@Override
			protected void updateItem(Plans item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getName());
			}
		};
		comboBoxPlan.setCellFactory(factory);
		comboBoxPlan.setButtonCell(factory.call(null));
	}

}
