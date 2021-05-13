package gui;

import java.net.URL;
import java.util.ResourceBundle;

import db.DbException;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Plans;
import model.services.PlanService;

public class PlanFormController implements Initializable
{
	private Plans entity;
	
	private PlanService service;
	
	@FXML
	private TextField txtId;
	
	@FXML
	private TextField txtName;
	
	@FXML
	private Label labelErrorName;
	
	@FXML
	private Button btSave;
	
	@FXML
	private Button btCancel;
	
	public void setPlan(Plans entity)
	{
		this.entity = entity;
	}
	
	public void setPlanService(PlanService service)
	{
		this.service = service;
	}
	
	@FXML
	public void onBtSaveAction(ActionEvent event) 
	{
		if(entity == null)
		{
			throw new IllegalStateException("Entidade esta null");
		}
		if(service == null)
		{
			throw new IllegalStateException("Service esta nulo");
		}
		try
		{
			entity = getFormData();
			service.saveOrUpdate(entity);
			Utils.currentStage(event).close();
		}
		catch(DbException e)
		{
			Alerts.showAlert("Erro ao salvar no banco", null, e.getMessage(), AlertType.ERROR);
		}
	}
	
	private Plans getFormData() 
	{
		Plans obj = new Plans();
		obj.setId((Utils.tryParseToInt(txtId.getText())));
		obj.setName(txtName.getText());
		return obj;
	}

	@FXML
	public void onBtCancelAction(ActionEvent event) 
	{
		Utils.currentStage(event).close();
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) 
	{
		// TODO Auto-generated method stub
		initializeNodes();
	}
	
	private void initializeNodes()
	{
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 30);
	}
	
	public void updateFormData()
	{
		if(entity==null)
		{
			throw new IllegalStateException("Entidade nula.....");
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
	}

}
