package application;

import java.time.LocalDate;

import datamodel.TodoData;
import datamodel.TodoItem;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class DialogController {
	@FXML
	private TextField shortDescriptionField;
	@FXML
	private TextArea detailsArea;
	@FXML
	private DatePicker deadlinePicker;

	public TodoItem processResults() {
		String description = shortDescriptionField.getText().trim();
		String details = detailsArea.getText().trim();
		LocalDate deadlineValue = deadlinePicker.getValue();
		
		//Setting default values to avoid Null Pointer Exception

		TodoItem newItem = new TodoItem(description, details, deadlineValue);
		TodoData.getInstance().addTodoItem(newItem);
		return newItem;
	}

	public void editResults(TodoItem item) {
		shortDescriptionField.setText(item.getShortDescription());
		detailsArea.setText(item.getDetails());
		deadlinePicker.setValue(item.getDeadline());
	}
}
