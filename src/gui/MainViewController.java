package gui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;

public class MainViewController implements Initializable
{
	@FXML
	private MenuItem menuItemMember;
	@FXML
	private MenuItem menuItemPlan;
	@FXML
	private MenuItem menuItemAbout;
	
	@Override
	public void initialize(URL uri, ResourceBundle arg1) 
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
		System.out.println("Plan Pressed");
	}
	
	@FXML
	public void onMenuItemAboutAction() 
	{
		System.out.println("About Pressed");
	}
	
}
