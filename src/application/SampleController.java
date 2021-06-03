package application;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;

import datamodel.TodoData;
import datamodel.TodoItem;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;

public class SampleController {
	@FXML
	private ListView<TodoItem> todoListView;
	@FXML
	private TextArea itemsDetailsTextArea;
	@FXML
	private Label deadlineLabel;
	@FXML
	private BorderPane mainBorderPane;
	@FXML
	private ContextMenu listContextMenu;
	@FXML
	private ToggleButton filterToggleButton;
	
	private FilteredList<TodoItem> filteredlist;
	
	private Predicate<TodoItem> wantAllItems;
	private Predicate<TodoItem> wantTodaysItems;
	
	public void initialize() {
		listContextMenu = new ContextMenu();
		MenuItem deleteMenuItem = new MenuItem("Delete");
		MenuItem editMenuItem = new MenuItem("Edit");
		deleteMenuItem.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				TodoItem item = todoListView.getSelectionModel().getSelectedItem();
				deleteItem(item);
			}
		});

		editMenuItem.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				editTodoItemDialog();
			}
		});

		listContextMenu.getItems().addAll(deleteMenuItem);
		listContextMenu.getItems().addAll(editMenuItem);
		todoListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TodoItem>() {
			@Override
			public void changed(ObservableValue<? extends TodoItem> observable, TodoItem oldValue, TodoItem newValue) {
				if (newValue != null) {
					TodoItem item = todoListView.getSelectionModel().getSelectedItem();
					itemsDetailsTextArea.setText(item.getDetails());
					DateTimeFormatter df = DateTimeFormatter.ofPattern("MMMM d, yyyy"); // "d M yy");
					deadlineLabel.setText(df.format(item.getDeadline()));
				}
			}
		});
		
		wantAllItems=new Predicate<TodoItem>() {
			@Override
			public boolean test(TodoItem todoitem) {
				return true;
			}
		};
		
		wantTodaysItems=new Predicate<TodoItem>() {
			@Override
			public boolean test(TodoItem todoitem) {
				return (todoitem.getDeadline().equals(LocalDate.now()));
			}
		};
		
		filteredlist=new FilteredList<TodoItem>(TodoData.getInstance().getTodoItems(),wantAllItems);
				
		SortedList<TodoItem> sortedlist=new SortedList<TodoItem>(filteredlist,new Comparator<TodoItem>() {
			@Override
			public int compare(TodoItem o1,TodoItem o2) {
				return o1.getDeadline().compareTo(o2.getDeadline());
			}
		});

	//	todoListView.setItems(TodoData.getInstance().getTodoItems());
		
		todoListView.setItems(sortedlist);
		todoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		todoListView.getSelectionModel().selectFirst();

		todoListView.setCellFactory(new Callback<ListView<TodoItem>, ListCell<TodoItem>>() {
			@Override
			public ListCell<TodoItem> call(ListView<TodoItem> param) {
				ListCell<TodoItem> cell = new ListCell<TodoItem>() {

					@Override
					protected void updateItem(TodoItem item, boolean empty) {
						super.updateItem(item, empty);
						if (empty) {
							setText(null);
						} else {
							setText(item.getShortDescription());
							if (item.getDeadline().isBefore(LocalDate.now().plusDays(1))) {
								setTextFill(Color.RED);
							} else if (item.getDeadline().equals(LocalDate.now().plusDays(1))) {
								setTextFill(Color.BROWN);
							}
						}
					}
				};

				cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
					if (isNowEmpty) {
						cell.setContextMenu(null);
					} else {
						cell.setContextMenu(listContextMenu);
					}

				});

				return cell;
			}
		});
	}

	@FXML
	public void showNewItemDialog() {
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.initOwner(mainBorderPane.getScene().getWindow());
		dialog.setTitle("Add New Todo Item");
		dialog.setHeaderText("Use this dialog to create a new todo item");
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setLocation(getClass().getResource("todoItemDialog.fxml"));
		try {
			dialog.getDialogPane().setContent(fxmlLoader.load());

		} catch (Exception e) {
			System.out.println("Couldn't load the dialog");
			e.printStackTrace();
			return;
		}

		dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
		dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

		Optional<ButtonType> result = dialog.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK) {
			DialogController controller = fxmlLoader.getController();
			TodoItem newItem = controller.processResults();
			todoListView.getSelectionModel().select(newItem);
		}

	}

	@FXML
	public void editTodoItemDialog() {
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.initOwner(mainBorderPane.getScene().getWindow());
		dialog.setTitle("Edit Todo Item");
		dialog.setHeaderText("Edit this dialog to change an existing todo item");
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setLocation(getClass().getResource("todoItemDialog.fxml"));
		try {
			dialog.getDialogPane().setContent(fxmlLoader.load());

		} catch (Exception e) {
			System.out.println("Couldn't load the dialog");
			e.printStackTrace();
			return;
		}
		DialogController controller = fxmlLoader.getController();
		controller.editResults(todoListView.getSelectionModel().getSelectedItem());
		dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
		dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

		Optional<ButtonType> result = dialog.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK) {
			TodoItem newItem = controller.processResults();
			todoListView.getSelectionModel().select(newItem);
			TodoData.getInstance().deleteTodoItem(todoListView.getSelectionModel().getSelectedItem());
		}
	}

	@FXML
	public void handleKeyPressed(KeyEvent keyEvent) {
		TodoItem selectedItem = todoListView.getSelectionModel().getSelectedItem();
		if (selectedItem != null) {
			if (keyEvent.getCode().equals(KeyCode.DELETE)) {
				deleteItem(selectedItem);
			}
		}
	}

	@FXML
	public void handleClickListView() {
		TodoItem item = todoListView.getSelectionModel().getSelectedItem();
		itemsDetailsTextArea.setText(item.getDetails());
		deadlineLabel.setText(item.getDeadline().toString());
	}

	public void deleteItem(TodoItem item) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Delete Todo Item");
		alert.setHeaderText("Delete item: " + item.getShortDescription());
		Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
		stage.setAlwaysOnTop(true);
		alert.getDialogPane().setPrefSize(350, 200);
		alert.setContentText("Are you sure?  Press OK to confirm, or cancel to Back out.");
		Optional<ButtonType> result = alert.showAndWait();

		if (result.isPresent() && (result.get() == ButtonType.OK)) {
			TodoData.getInstance().deleteTodoItem(item);
		}

	}
	
	@FXML
	public void handleFilterButton() {
		TodoItem selectedItem=todoListView.getSelectionModel().getSelectedItem();
		
		if(filterToggleButton.isSelected()) {
			filteredlist.setPredicate(wantTodaysItems);
			if(filteredlist.isEmpty()) {
				itemsDetailsTextArea.clear();
				deadlineLabel.setText("");
				
			}else if(filteredlist.contains(selectedItem)) {
				todoListView.getSelectionModel().select(selectedItem);
				
			}else {
				todoListView.getSelectionModel().selectFirst();
			}
			
		}else {
			filteredlist.setPredicate(wantAllItems);
			todoListView.getSelectionModel().select(selectedItem);
		}
	}
	
	@FXML
	public void handleExit() {
		Platform.exit();
	}
}
