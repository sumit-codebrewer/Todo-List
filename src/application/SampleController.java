package application;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import datamodel.TodoData;
import datamodel.TodoItem;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;

public class SampleController {
	private List<TodoItem> todoItems;
	
	@FXML
	private ListView<TodoItem> todoListView;
	@FXML
	private TextArea itemsDetailsTextArea;
	@FXML
	private Label deadlineLabel;
	@FXML
	private BorderPane mainBorderPane;
	
	public void initialize() {
//		*Removing hard coded values used for testing of application*
//		TodoItem item1=new TodoItem("Mail birthday card","Buy a 20th birthday card for Sumit", 
//				LocalDate.of(2021,Month.SEPTEMBER,30));
//		TodoItem item2=new TodoItem("Doctor's Appointment","See Dr. Smith at 123 Main Street,Bring paperwork", 
//				LocalDate.of(2021,Month.OCTOBER,19));
//		TodoItem item3=new TodoItem("Finish design proposal for Client","I promised Mike that I will submit design Mockups by Friday"
//				+"2nd November",LocalDate.of(2021,Month.NOVEMBER, 2)); 
//		TodoItem item4=new TodoItem("Pickup Doug at the train station","Doug's arriving on November 17 at 5:00 pm by train", 
//				LocalDate.of(2021,Month.NOVEMBER,17));
//		TodoItem item5=new TodoItem("Pick up dry cleaned clothes","The clothes shoud be ready by Thursday", 
//				LocalDate.of(2021,Month.DECEMBER,5));
//		
//		todoItems=new ArrayList<TodoItem>();
//		todoItems.add(item1);
//		todoItems.add(item2);
//		todoItems.add(item3);
//		todoItems.add(item4);
//		todoItems.add(item5);
//		
//		TodoData.getInstance().setTodoItems(todoItems);
		
		todoListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TodoItem>() {
			@Override
			public void changed(ObservableValue<? extends TodoItem> observable,TodoItem oldValue,TodoItem newValue) {
				if(newValue!=null) {
					TodoItem item=todoListView.getSelectionModel().getSelectedItem();
					itemsDetailsTextArea.setText(item.getDetails());
					DateTimeFormatter df=DateTimeFormatter.ofPattern("MMMM d,yyyy");
					deadlineLabel.setText(df.format(item.getDeadline()));
				}
			}
		});
		todoListView.getItems().setAll(TodoData.getInstance().getTodoItems());
		todoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		todoListView.getSelectionModel().selectFirst();;
		
	}
	
	@FXML
	public void showItemDialog() {
		Dialog<ButtonType> dialog=new Dialog<>();
		dialog.initOwner(mainBorderPane.getScene().getWindow());
		try {
			Parent root=FXMLLoader.load(getClass().getResource("TodoItemDialog.fxml"));
			dialog.getDialogPane().setContent(root); 
		}
		catch(IOException e) {
			System.out.println("Could not load the dialog");
			e.printStackTrace();
		}
	}
	
	@FXML
	public void handleClickListView() {
		TodoItem item=todoListView.getSelectionModel().getSelectedItem();
		itemsDetailsTextArea.setText(item.getDetails());
		deadlineLabel.setText(item.getDeadline().toString());
//		*Implementing a different way to handle events*
//		System.out.println("The selected item is "+item);
//		StringBuilder sb=new StringBuilder(item.getDetails());
//		sb.append("\n\n\n\n");
//		sb.append("Due: ");
//		sb.append(item.getDeadline().toString());
//		itemsDetailsTextArea.setText(sb.toString());
	}
	
}
