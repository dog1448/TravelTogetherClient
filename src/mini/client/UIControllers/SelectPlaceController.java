package mini.client.UIControllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import mini.client.VO.SearchVO;

public class SelectPlaceController implements Initializable
{
	private ObservableList<SearchVO> searchList = FXCollections.observableArrayList();
	
	@FXML private TableView<SearchVO> ResultTable;
	@FXML private TableColumn<SearchVO, String> resultNameColumn;
	@FXML Button btnPlaceSubmit;
	@FXML Button btnPlaceCancel;
	
	PlanController planController;
	
	public void setSearchList(ObservableList<SearchVO> searchList) {
		for(SearchVO se : searchList)
		{
			this.searchList.add(se);
		}
	}
	
	public void setPlanController(PlanController planController) {
		this.planController = planController;
	}
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) 
	{
		resultNameColumn.setCellValueFactory(cellData -> cellData.getValue().getNameProperty());
		ResultTable.setItems(searchList);
		
		btnPlaceSubmit.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(ResultTable.getSelectionModel().getSelectedItem() !=null)
				{
					double x = ResultTable.getSelectionModel().getSelectedItem().getX();
					double y = ResultTable.getSelectionModel().getSelectedItem().getY();
					planController.setTfLoc(y+", "+x);
					planController.closeSelectStage();
				}
			}
		});
		
		btnPlaceCancel.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				planController.closeSelectStage();
			}
		});
	}

}
