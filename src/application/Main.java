package application;

import java.io.IOException;

import datamodel.TodoData;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = (Parent) FXMLLoader.load(getClass().getResource("mainWindow.fxml"));
			Scene scene = new Scene(root, 1000, 600);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setTitle("Todo List");
			primaryStage.setScene(scene);
			primaryStage.setAlwaysOnTop(true);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void stop() throws Exception {
		try {
			TodoData.getInstance().storeTodoItems();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	@Override
	public void init() throws Exception {
		try {
			TodoData.getInstance().loadTodoItems();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
