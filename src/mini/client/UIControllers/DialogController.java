package mini.client.UIControllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import mini.client.VO.MessageVO;
import mini.client.VO.RoomVO;
import mini.client.VO.UserVO;
import mini.client.network.controllers.INetController;
import mini.client.network.staticValues.FormatStaticValues;

public class DialogController implements Initializable 
{
	Stage primaryStage;
	INetController iNetController;
	UserVO userMe;
	LobbyController lobbyController;
	
	private DialogController() {
	}
	public DialogController(Stage primaryStage, INetController iNetController) {
		this.primaryStage = primaryStage;
		this.iNetController = iNetController;
	}
	
	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}
	public void setiNetController(INetController iNetController) {
		this.iNetController = iNetController;
	}
	public void setUserMe(UserVO userMe) {
		this.userMe = userMe;
	}
	public void setLobbyController(LobbyController lobbyController) {
		this.lobbyController = lobbyController;
	}
	
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
	}
	
//	==============================================================================================================================
//	통합 경고 Alert
	public void newAlertDialog(String warningTitle, String warningHeader, String warningContent, Alert.AlertType type)
	{
		Alert alert = new Alert(type);
		alert.initOwner(primaryStage);
		
		alert.setTitle(warningTitle);
		alert.setHeaderText(warningHeader);
		alert.setContentText(warningContent);
		
		alert.showAndWait();
	}
	
//	==============================================================================================================================	
//	메세지 다이얼로그 띄우기
	public void newMessageDialog(MessageVO message, boolean isSend)
	{
		try {
			Stage messageDialog = new Stage(StageStyle.UTILITY);
			messageDialog.initModality(Modality.WINDOW_MODAL);
//			messageDialog.initOwner(primaryStage);
			messageDialog.setTitle("메세지");
			
			AnchorPane messageLayout = (AnchorPane)FXMLLoader.load(getClass().getResource("../../../fxml/MessageDialog.fxml"));
			Label lbFrom = (Label)messageLayout.lookup("#lbFrom");
			Label lbTo = (Label)messageLayout.lookup("#lbTo");
			TextArea taMessage = (TextArea)messageLayout.lookup("#taMessage");
			Button btnSend = (Button)messageLayout.lookup("#btnSend");
			Button btnCancel = (Button)messageLayout.lookup("#btnCancel");
			FlowPane flowPaneTo = (FlowPane)messageLayout.lookup("#flowPaneTo");
			
			System.out.println("messageDialog: "+message);
			
			lbFrom.setText(message.getFrom());
			lbTo.setText(message.getTo());
			if(message.getMessage() != null)
				taMessage.setText(message.getMessage());
			
			btnCancel.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) 
				{
					messageDialog.close();
				}
			});
			
			if(!isSend)
			{
				taMessage.setEditable(false);
				btnSend.setVisible(false);
				flowPaneTo.setVisible(false);
			}
			
			Scene messageScene = new Scene(messageLayout);
			messageDialog.setScene(messageScene);
			messageDialog.setResizable(false);
			messageDialog.setX(100);
			messageDialog.setY(100);
			messageDialog.show();
		} 
		catch (IOException e) {
			System.out.println("메세지받는 쓰레드 : 메세지만들다 오류");	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		}
	}

	
//	==============================================================================================================================
//	회원가입 아이디 공백 경고 다이얼로그
	public void idFalseFormatWarningDialog()
	{
		Alert alert = new Alert(AlertType.WARNING);
		String confirmTitle ="경고";
		String confirmHeader ="아이디형식이 잘못되었습니다.";
		String confirmContent = "아이디는 "+FormatStaticValues.ID_MIN_LENGTH+"~"+FormatStaticValues.ID_MAX_LENGTH+"자로 입력해주세요...";
		
		alert.setTitle(confirmTitle);
		alert.setHeaderText(confirmHeader);
		alert.setContentText(confirmContent);
		alert.showAndWait();
	}
	

//	==============================================================================================================================
//	방 초대 다이얼로그 띄우기
	public void newRoomInviteDialog(String fromId, RoomVO room)
	{
		try {
			Stage inviteDialogStage = new Stage(StageStyle.UTILITY);
			inviteDialogStage.initModality(Modality.WINDOW_MODAL);
			inviteDialogStage.initOwner(primaryStage);
			inviteDialogStage.setTitle("초대를 받았습니다.");
			
			AnchorPane inviteDialogLayout = (AnchorPane)FXMLLoader.load(getClass().getResource("../../../fxml/InviteFromRoomDialog.fxml"));
			Label lbFrom = (Label)inviteDialogLayout.lookup("#lbFrom");
			Label lbRoomName = (Label)inviteDialogLayout.lookup("#lbRoomName");
			Label lbPlaceName = (Label)inviteDialogLayout.lookup("#lbPlaceName");
			Button btnAccept = (Button)inviteDialogLayout.lookup("#btnAccept");
			Button btnCancel = (Button)inviteDialogLayout.lookup("#btnCancel");
			
			lbFrom.setText(fromId);
			lbRoomName.setText(room.getRoomName());
			lbPlaceName.setText(room.getRoomPlace());
			
//			***********************************************************************************************************
//			버튼들 구현
			btnAccept.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					iNetController.replyRoomInvite(userMe.getUserId(), fromId, room, true);
					inviteDialogStage.close();
					lobbyController.addRoomVOToRoomList(room);
				}
			});
			btnCancel.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					iNetController.replyRoomInvite(userMe.getUserId(), fromId, room, false);
					inviteDialogStage.close();
				}
			});
			
			
			Scene inviteDialogScene = new Scene(inviteDialogLayout);
			inviteDialogStage.setScene(inviteDialogScene);
			inviteDialogStage.setResizable(false);
			
			inviteDialogStage.show();
			
		} catch (IOException e) {
			System.out.println("다이얼로그 컨트롤러 : 방초대 만들다가 오류");	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		}
	}
	
	
	
	
	
	
}
