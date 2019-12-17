package mini.client.UIControllers;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import mini.client.VO.ChatVO;
import mini.client.VO.RoomVO;
import mini.client.VO.UserVO;
import mini.client.network.controllers.INetController;

public class ChatRoomController implements Initializable
{
//	===============================================================================================================
//	필수 멤버들
	private MainUIController mainController;
	private Stage primaryStage;
	private DialogController dialogController;
	private INetController iNetController;
	private LobbyController lobbyController;
	private UserVO userMe;
	private RoomVO roomNow;
	
	SimpleDateFormat time = new SimpleDateFormat("hh:mm");

//	===============================================================================================================
//	컴포넌트들
	@FXML private Label labelRoomName;
	@FXML private Button btnAddRoomMember;
	@FXML private CheckBox checkBoxAlwaysTop;
	@FXML private TextArea taChatLog;
	@FXML private TextArea taInputChat;
	@FXML private Button btnSendChat;

//	===============================================================================================================
//	방 멤버추가 다이얼로그 관련
	Stage addRoomMemberDialog;
	
	
//	===============================================================================================================
//	테이블 관련
	private ObservableList<UserVO> roomMemberList = FXCollections.observableArrayList();
	
	@FXML private TableView<UserVO> roomMemberTable;
	@FXML private TableColumn<UserVO, String> roomMemberIdColumn;
	@FXML private TableColumn<UserVO, String> roomMemberNameColumn;
	@FXML private TableColumn<UserVO, String> roomMemberStateColumn;
	
//	===============================================================================================================
//	Getter Setter
	public void setRoomMemberList(ObservableList<UserVO> roomMemberList)
	{
		for(UserVO member : roomMemberList)
		{
			this.roomMemberList.add(member);
		}
	}
//	리스트에 방 맴버 추가
	public void addRoomMemberToList(UserVO member)
	{
		if(member.getUserState().equalsIgnoreCase("t"))
			member.setUserState("접속중");
		else
			member.setUserState("미접속");
		this.roomMemberList.add(member);
	}
//	여기에 로비 컨트롤러 셋
	public void setLobbyController(LobbyController lobbyController) {
		this.lobbyController = lobbyController;
	}
//	방인원수 리턴
	public int getRoomMemberSize()
	{
		return roomMemberList.size();
	}
	
//	===============================================================================================================
//	무조건 가져야하는 멤버들 초기화 및 받아오기
	public void setMustHaveMembers(MainUIController mainController, UserVO userMe, RoomVO room)
	{
		this.mainController = mainController;
		this.primaryStage = mainController.getPrimaryStage();
		iNetController = mainController.getiNetController();
		dialogController = mainController.getDialogController();
		this.userMe = userMe;
		this.roomNow = room;
		mainController.setChatRoomControllerToListener(this);
		labelRoomName.setText(room.getRoomName());
		
		roomMemberTable.setItems(roomMemberList);
		iNetController.getRoomMemberList(room.getRoomNum());
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) 
	{
//		===============================================================================================================
//		테이블 관련 설정
		roomMemberIdColumn.setCellValueFactory(cellData -> cellData.getValue().getUserIdProperty());
		roomMemberNameColumn.setCellValueFactory(cellData -> cellData.getValue().getUserNameProperty());
		roomMemberStateColumn.setCellValueFactory(cellData -> cellData.getValue().getUserStateProperty());
		
//		===============================================================================================================
//		방 멤버 추가 버튼
		btnAddRoomMember.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				try {
					addRoomMemberDialog = new Stage(StageStyle.UTILITY);
					addRoomMemberDialog.initModality(Modality.WINDOW_MODAL);
					addRoomMemberDialog.initOwner(primaryStage);
					addRoomMemberDialog.setTitle("여행 멤버 추가");
					
					AnchorPane addRoomMemberLayout = (AnchorPane)FXMLLoader.load(getClass().getResource("../../../fxml/AddFriendDialog.fxml"));
					TextField tfId = (TextField)addRoomMemberLayout.lookup("#tfId");
					Button btnSend = (Button)addRoomMemberLayout.lookup("#btnSend");
					Button btnCancel = (Button)addRoomMemberLayout.lookup("#btnCancel");
					
//					멤버추가 SEND버튼 눌렀을떄
					btnSend.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) 
						{
							iNetController.addRoomMember(userMe.getUserId(),tfId.getText(), roomNow);
							addRoomMemberDialog.close();
						}
					});
					
					btnCancel.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) 
						{
							addRoomMemberDialog.close();
						}
					});
					
					Scene addRoomMemberScene = new Scene(addRoomMemberLayout);
					addRoomMemberDialog.setScene(addRoomMemberScene);
					addRoomMemberDialog.setResizable(false);
					addRoomMemberDialog.show();
				} 
				catch (IOException e) {
					System.out.println("채팅방컨트롤러 : 맴버추가 다이얼로그 만들다 오류");	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
				}
				
			}
		});
		
//		===============================================================================================================
//		채팅방 항상위
		checkBoxAlwaysTop.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(checkBoxAlwaysTop.isSelected())
				{
					lobbyController.setChatRoomAlwaysOnTop();
				}
				else
				{
					lobbyController.setChatRoomNotOnTop();
				}
			}
		});
		
//		===============================================================================================================
//		채팅 메세지 보내기 버튼
		btnSendChat.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(taInputChat.getText().equals(""))
					return;
				ChatVO chat = new ChatVO();
				chat.setChatMemberId(userMe.getUserId());
				chat.setChatRoomNum(roomNow.getRoomNum());
				chat.setChatMessage(taInputChat.getText());
				iNetController.sendChat(chat);
				taInputChat.setText("");
			}
		});
		
		
	}	// Initialize...
	
	
//	===============================================================================================================
//	채팅창에 줄 추가
	public void addText(ChatVO chat)
	{
		if(chat.getChatRoomNum() == roomNow.getRoomNum())
		{
			Date now = new Date();
			taChatLog.appendText("["+time.format(now)+"] ["+chat.getChatMemberId()+"] : "+chat.getChatMessage()+"\n");
		}
	}
	
	public void loadChatLog(ObservableList<ChatVO> chatLogList)
	{
		chatLogList.sort(new Comparator<ChatVO>() {
			@Override
			public int compare(ChatVO o1, ChatVO o2) {
				if(o1.getChatNum() > o2.getChatNum())
					return 1;
				else if(o1.getChatNum() < o2.getChatNum())
					return -1;
				else
					return 0;
			}
		});
		for(ChatVO chat : chatLogList)
		{
			taChatLog.appendText("["+chat.getChatTime()+"] ["+chat.getChatMemberId()+"] : "+chat.getChatMessage()+"\n");
		}
	}
	
	public void friendLogin(String userId)
	{
		for(UserVO user : roomMemberList)
		{
			if(user.getUserId().equals(userId))
			{
				user.setUserState("접속중");
				return;
			}
		}
	}
	
	public void friendLogout(String userId)
	{
		for(UserVO user : roomMemberList)
		{
			if(user.getUserId().equals(userId))
			{
				user.setUserState("미접속");
				return;
			}
		}
	}

}
