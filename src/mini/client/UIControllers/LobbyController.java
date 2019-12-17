package mini.client.UIControllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import mini.client.VO.MessageVO;
import mini.client.VO.RoomVO;
import mini.client.VO.UserVO;
import mini.client.network.controllers.INetController;

public class LobbyController implements Initializable
{
//	===============================================================================================================
//	UI관련 멤버
	private Stage primaryStage;
	private Stage addFriendDialog;
	private Stage addRoomDialog;
	private Stage chatRoomStage;
	private Stage planStage;
	
	private DialogController dialogController;
	private LobbyController myLobbyController = this;
	private PlanController planController;
	
	@FXML private Button btnAddFriend;
	@FXML private Button btnAddRoom;
	@FXML private Button btnAddSavedPlan;
	
	private int selectedPane = 1;
	
	private Window mainWindow;


//	===============================================================================================================
//	탭 관련 멤버
	@FXML private TabPane tabPaneLobby;
	@FXML private Tab tabFriend;
	@FXML private Tab tabRoom;
	@FXML private Tab tabPlan;
	SingleSelectionModel<Tab> tabPaneSeletor;
	
//	===============================================================================================================
//	Component관련 멤버
	@FXML private TableView<UserVO> friendTable;
	@FXML private TableColumn<UserVO, String> friendIdColumn;
	@FXML private TableColumn<UserVO, String> friendNameColumn;
	@FXML private TableColumn<UserVO, String> friendStateColumn;
	
	@FXML private TableView<RoomVO> roomTable;
	@FXML private TableColumn<RoomVO, String> roomNameColumn;
	@FXML private TableColumn<RoomVO, String> roomStartDateColumn;
	@FXML private TableColumn<RoomVO, String> roomEndDateColumn;
	
//	===============================================================================================================
//	방 만들기 페이지 관련 멤버
	@FXML private TextField tfAddRoomRoomName;
	@FXML private TextField tfAddRoomRoomPlace;
	@FXML private DatePicker dpAddRoomRoomStartDate;
	@FXML private DatePicker dpAddRoomRoomEndDate;
	@FXML private Button btnAddRoomSend;
	@FXML private Button btnAddRoomCancel;
	
	
//	===============================================================================================================
//	마우스 우클릭 컨텍스트
	ContextMenu cm = new ContextMenu(); 
	MenuItem menuItemDelete = new MenuItem("삭제");
	MenuItem menuItemGetOut = new MenuItem("방 나가기");
	
//	===============================================================================================================
//	Getter, Setter
	
	
	
//	===============================================================================================================
//	table관련 멤버
	private ObservableList<UserVO> friendList = FXCollections.observableArrayList();
	private ObservableList<RoomVO> roomList = FXCollections.observableArrayList();
	
//	===============================================================================================================
//	table관련 작업
	public void addFriendToFriendList(UserVO friend)
	{
		if(friend.getUserState().equalsIgnoreCase("t"))
			friend.setUserState("접속중");
		else
			friend.setUserState("미접속");
		friendList.add(friend);
	}
	public void setLoginStateFromFriendList(String friendId)
	{
		for(UserVO friend : friendList)
		{
			if(friend.getUserId().equals(friendId))
			{
				friend.setUserState("접속중");
				break;
			}
		}
	}
	public void setLogoutStateFromFriendList(String friendId)
	{
		for(UserVO friend : friendList)
		{
			if(friend.getUserId().equals(friendId))
			{
				friend.setUserState("미접속");
				break;
			}
		}
	}
	public void deleteFriendFromFriendList(UserVO friend)
	{
		friendList.remove(friend);
	}
	public void setFriendList(ObservableList<UserVO> friendList) {
		for(UserVO friend : friendList)
		{
			this.friendList.add(friend);
		}
	}
	public void setRoomList(ObservableList<RoomVO> roomList)
	{
		for(RoomVO room : roomList)
		{
			this.roomList.add(room);
		}
	}
	public void addRoomVOToRoomList(RoomVO room)
	{
		roomList.add(room);
	}
	
//	===============================================================================================================
//	컨트롤러 및 관련 멤버
	private MainUIController mainController;
	private UserVO userMe;
	private INetController iNetController;
	private ChatRoomController chatRoomController;
	
//	===============================================================================================================
//	멤버 초기화 메서드 메인에서 여기 멤버들 세팅
	public void setMainController(MainUIController mainController)
	{
		this.mainController = mainController;
		this.primaryStage = mainController.getPrimaryStage();
		this.userMe = mainController.getUserMe();
		dialogController = mainController.getDialogController();
		dialogController.setPrimaryStage(mainController.getPrimaryStage());
		iNetController = mainController.getiNetController();
		friendTable.setItems(friendList);
		roomTable.setItems(roomList);
	}
	
	
//	===============================================================================================================
//	initialize 메서드	UI 시작 초기화 처리
	@Override
	public void initialize(URL location, ResourceBundle resources) 
	{
//		-----------------------------------------------------------------------------------------------
//		탭 관련 마우스 우클릭 설정
		cm.getItems().add(menuItemDelete);
		cm.getItems().add(menuItemGetOut);
		tabPaneLobby.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if(tabFriend.isSelected())
				{
					selectedPane = 1;
				}
				else if(tabRoom.isSelected())
				{
					selectedPane = 2;
				}
			}
		});
//		-----------------------------------------------------------------------------------------------
//		탭 선택 용 셀렉터		
		tabPaneSeletor = tabPaneLobby.getSelectionModel();
		
		
//		-----------------------------------------------------------------------------------------------
//		친구 테이블 관련 설정들
		friendIdColumn.setCellValueFactory(cellData -> cellData.getValue().getUserIdProperty());
		friendNameColumn.setCellValueFactory(cellData -> cellData.getValue().getUserNameProperty());
		friendStateColumn.setCellValueFactory(cellData -> cellData.getValue().getUserStateProperty());
		
		friendTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) 
			{
				MouseButton msbtn = event.getButton();
				if(event.getClickCount()==1 && tabFriend.isSelected())
				{
					menuItemDelete.setVisible(true);
					menuItemDelete.setDisable(false);
					menuItemGetOut.setVisible(false);
					menuItemGetOut.setDisable(true);
				}
				if(event.getClickCount()==2 && friendTable.getItems().size() != 0)
				{
					if(friendTable.getSelectionModel().getSelectedItem().getUserState().equals("접속중"))
					{
						UserVO selectedFriend = friendTable.getSelectionModel().getSelectedItem();
//						메세지 보내는 거 만들면됨 
						MessageVO message = new MessageVO();
						message.setFrom(userMe.getUserId());
						message.setTo(selectedFriend.getUserId());
						newMessageDialog(message, true);
					}
					else
					{
						dialogController.newAlertDialog("메세지 전송 불가", "접속중인 유저가 아닙니다", "접속중인 유저에게만 메세지를 보낼 수 있습니다.", AlertType.WARNING);
					}
				}
//				마우스 우클릭 했을떄 메뉴 띄우고 누르면 삭제 날림
				else if(msbtn == MouseButton.SECONDARY && selectedPane == 1)
				{
					cm.show(primaryStage,event.getScreenX(),event.getScreenY());
					menuItemDelete.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							if(selectedPane == 1)
							{
								UserVO tempF = friendTable.getSelectionModel().getSelectedItem();
								iNetController.deleteFriend(tempF);
								friendList.remove(tempF);
							}
						}
					});
				}
				
			}
		});
		
//		-----------------------------------------------------------------------------------------------
//		방 목록 테이블 관련 설정들
		roomNameColumn.setCellValueFactory(cellData -> cellData.getValue().getRoomNameProperty());
		roomStartDateColumn.setCellValueFactory(cellData -> cellData.getValue().getRoomStartDateProperty());
		roomEndDateColumn.setCellValueFactory(cellData -> cellData.getValue().getRoomEndDateProperty());
		
		roomTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) 
			{
				MouseButton msbtn = event.getButton();
				if(event.getClickCount()==1 && tabRoom.isSelected())
				{
					menuItemDelete.setVisible(false);
					menuItemDelete.setDisable(true);
					menuItemGetOut.setVisible(true);
					menuItemGetOut.setDisable(false);
				}
//				방 목록에서 방 아이템 더블클릭 (방 입장)
				if(event.getClickCount()==2 && roomTable.getItems().size() != 0)
				{
					if(chatRoomStage ==null)
					{
						chatRoomStage = new Stage(StageStyle.UTILITY);
						chatRoomStage.initModality(Modality.WINDOW_MODAL);
					}
					chatRoomStage.setTitle(roomTable.getSelectionModel().getSelectedItem().getRoomName());
					
					if(planStage == null)
					{
						planStage = new Stage(StageStyle.UTILITY);
						planStage.initModality(Modality.WINDOW_MODAL);
					}
					planStage.setTitle(roomTable.getSelectionModel().getSelectedItem().getRoomName()+" 계획방");
					
					try {
						FXMLLoader loader = new FXMLLoader();
						loader.setLocation(getClass().getResource("../../../fxml/ChattingRoom.fxml"));
						AnchorPane chatRoomLayout = (AnchorPane)loader.load();
						
//						*************************************************************************************************
//						채팅방 컨트롤러 받아온다...
						chatRoomController = loader.getController();
						chatRoomController.setMustHaveMembers(mainController, userMe, roomTable.getSelectionModel().getSelectedItem());
						chatRoomController.setLobbyController(myLobbyController);
//						*************************************************************************************************
						
						
						Scene chatRoomScene = new Scene(chatRoomLayout);
						chatRoomStage.setScene(chatRoomScene);
						
//						채팅방 초기 위치 설정
						mainWindow = primaryStage.getScene().getWindow();
						chatRoomStage.setX(mainWindow.getX()-295);
						chatRoomStage.setY(mainWindow.getY());
						
						chatRoomStage.show();
						
//						채팅 로그(기록) 서버에 요청
						iNetController.getChatList(roomTable.getSelectionModel().getSelectedItem().getRoomNum());
						


						
//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
//						여기부터 플레너 UI
						
						FXMLLoader planLoader = new FXMLLoader();
						planLoader.setLocation(getClass().getResource("../../../fxml/PlannerLayout.fxml"));
						AnchorPane planLayout = (AnchorPane)planLoader.load();
						
//						*************************************************************************************************
//						계획방 컨트롤러 받아온다...
						planController = planLoader.getController();
						planController.setMustHaveMembers(mainController, userMe, roomTable.getSelectionModel().getSelectedItem());
						planController.setLobbyController(myLobbyController);
						planController.setChatRoomController(chatRoomController);
//						*************************************************************************************************
					
						System.out.println(planStage.toString());
						
						Scene planScene = new Scene(planLayout);
						planStage.setScene(planScene);
						
//						플레너 초기 위치 설정
						mainWindow = primaryStage.getScene().getWindow();
						planStage.setX(mainWindow.getX()-1185);
						planStage.setY(mainWindow.getY());
						
						planStage.show();
						
//						*********************************************************************************************************
//						플레너 관련 서버 요청은 여기서
//						*********************************************************************************************************						
						
//						플랜 목록(이방의 것 전부다) 서버에 요청
						mainController.SleepMainUIController(200);
						iNetController.getAllPlanList(roomTable.getSelectionModel().getSelectedItem().getRoomNum());
						
						
//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
						
						
//						메인 꺼질때 채팅창 / 플래너 끄기..
						mainController.getPrimaryStage().setOnCloseRequest(new EventHandler<WindowEvent>() {
							@Override
							public void handle(WindowEvent event) {
								if(event.getEventType()==event.WINDOW_CLOSE_REQUEST)
								{
									if(chatRoomStage.isShowing())
										chatRoomStage.close();
									if(planStage.isShowing())
										planStage.close();
								}
							}
						});
						
						
//						방테이블 비활성화 하고 1번 탭으로 가기
						roomTable.setDisable(true);
						tabPaneSeletor.select(0);
						
//						채팅방 꺼질때 방테이블 비활성화 풀기 (planStage 켜져있으면 그거도 끔)
						chatRoomStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
							@Override
							public void handle(WindowEvent event) {
								if(event.getEventType()==event.WINDOW_CLOSE_REQUEST)
								{
									if(planStage.isShowing())
										planStage.close();
									roomTable.setDisable(false);
								}
							}
						});
						
						
					} catch (IOException e) {
						System.out.println("로비 컨트롤러 -> 채팅방 생성중 에러 : "+e.getMessage());	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
					}
					
					
				}
//				마우스 우클릭 했을떄 메뉴 띄우고 누르면 삭제 날림
				else if(msbtn == MouseButton.SECONDARY)
				{
					cm.show(primaryStage, event.getScreenX(), event.getScreenY());
					menuItemGetOut.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							if (selectedPane == 2) {
								RoomVO roomTemp = roomTable.getSelectionModel().getSelectedItem();
								iNetController.getOutRoom(userMe.getUserId(), roomTemp);
								roomList.remove(roomTemp);
							}
						}
					});
					
					
					
				}
			}
		});
		
//		-----------------------------------------------------------------------------------------------
//		친구 추가 버튼
		btnAddFriend.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) 
			{
				try {
					addFriendDialog = new Stage(StageStyle.UTILITY);
					addFriendDialog.initModality(Modality.WINDOW_MODAL);
					addFriendDialog.initOwner(primaryStage);
					addFriendDialog.setTitle("메세지");
					
					AnchorPane addFriendLayout = (AnchorPane)FXMLLoader.load(getClass().getResource("../../../fxml/AddFriendDialog.fxml"));
					TextField tfId = (TextField)addFriendLayout.lookup("#tfId");
					Button btnSend = (Button)addFriendLayout.lookup("#btnSend");
					Button btnCancel = (Button)addFriendLayout.lookup("#btnCancel");
					
					btnSend.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) 
						{
							String friendId = tfId.getText();
							if(!friendId.equals(""))
							{
								for(UserVO friend : friendList)
								{
									if(friend.getUserId().equals(friendId))
									{
										dialogController.newAlertDialog("친구추가 실패", "이미 추가된 아이디 입니다", "이미 추가되있는 친구입니다. \n다시 확인해 주세요", AlertType.WARNING);
										addFriendDialog.close();
										return;
									}
								}
								iNetController.addFirend(userMe, friendId);
							}
						}
					});
					
					btnCancel.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) 
						{
							addFriendDialog.close();
						}
					});
					
					Scene addFriendScene = new Scene(addFriendLayout);
					addFriendDialog.setScene(addFriendScene);
					addFriendDialog.setResizable(false);
					addFriendDialog.show();
				} catch (IOException e) {
					System.out.println("로비컨트롤러 : 친구추가 다이얼로그 만들다 오류");	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
				}
			}
		});
		
//		-----------------------------------------------------------------------------------------------
//		방추가 버튼
		btnAddRoom.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) 
			{
				try {
					addRoomDialog = new Stage(StageStyle.UTILITY);
					addRoomDialog.initModality(Modality.WINDOW_MODAL);
					addRoomDialog.initOwner(primaryStage);
					addRoomDialog.setTitle("여행 계획 추가");
					
					AnchorPane addRoomLayout = (AnchorPane)FXMLLoader.load(getClass().getResource("../../../fxml/AddRoomDialog.fxml"));
					tfAddRoomRoomName = (TextField)addRoomLayout.lookup("#tfAddRoomRoomName");
					tfAddRoomRoomPlace = (TextField)addRoomLayout.lookup("#tfAddRoomRoomPlace");
					dpAddRoomRoomStartDate = (DatePicker)addRoomLayout.lookup("#dpAddRoomRoomStartDate");
					dpAddRoomRoomEndDate = (DatePicker)addRoomLayout.lookup("#dpAddRoomRoomEndDate");
					btnAddRoomSend = (Button)addRoomLayout.lookup("#btnAddRoomSend");
					btnAddRoomCancel = (Button)addRoomLayout.lookup("#btnAddRoomCancel");
					
//					방 추가 요청 보내기
					btnAddRoomSend.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							if(checkAddRoomDialogValues())
							{
								RoomVO room = new RoomVO();
								room.setRoomOwnerId(userMe.getUserId());
								room.setRoomName(tfAddRoomRoomName.getText());
								room.setRoomPlace(tfAddRoomRoomPlace.getText());
								room.setRoomStartDate(dpAddRoomRoomStartDate.getValue().toString().replace('-', '/'));
								room.setRoomEndDate(dpAddRoomRoomEndDate.getValue().toString().replace('-', '/'));
								
								iNetController.addRoom(userMe.getUserId(), room);
								addRoomDialog.close();
							}
						}
					});
					
					btnAddRoomCancel.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							addRoomDialog.close();
						}
					});
					
					Scene addRoomScene = new Scene(addRoomLayout);
					addRoomDialog.setScene(addRoomScene);
					addRoomDialog.setResizable(false);
					addRoomDialog.show();
					
				} catch (IOException e) {
					System.out.println("로비컨트롤러 : 방추가 다이얼로그 만들다 오류");	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
				}
			}
		});
		
		
	}
	
//	===============================================================================================================
//	다이얼로그 닫는 메서드
	public void closeAddFriendDialog()
	{
		addFriendDialog.close();
	}
	public void closeAddRoomDialog()
	{
		addRoomDialog.close();
	}
	
//	===============================================================================================================
//	방 추가 다이얼로그 체크
	public boolean checkAddRoomDialogValues()
	{
		if(tfAddRoomRoomName.getText().equals(""))
		{
			dialogController.newAlertDialog("경고", "방 제목 오류", "방제목을 제대로 입력해 주세요.", AlertType.WARNING);
			return false;
		}
		else if(tfAddRoomRoomPlace.getText().equals(""))
		{
			dialogController.newAlertDialog("경고", "여행지 오류", "여행지를 제대로 입력해 주세요.", AlertType.WARNING);
			return false;
		}
		try {
			dpAddRoomRoomStartDate.getValue().toString();
			dpAddRoomRoomEndDate.getValue().toString();
		} 
		catch (NullPointerException e) {
			dialogController.newAlertDialog("경고", "일정 입력 오류", "출발 날짜와 마지막날을 제대로 입력해 주세요.", AlertType.WARNING);
			return false;
		}
		
		return true;
	}
	
	
//	===============================================================================================================
//	채팅방을 항상 위로 설정/해제하는 메서드
	public void setChatRoomAlwaysOnTop()
	{
		chatRoomStage.setAlwaysOnTop(true);
	}
	public void setChatRoomNotOnTop()
	{
		chatRoomStage.setAlwaysOnTop(false);
	}
	
	
//	===============================================================================================================
//	Component 들 제어용 메서드
	
	
//	===============================================================================================================
//	table 제어용 메서드
	
//	친구 테이블 더블클릭시 메세지
	public void newMessageDialog(MessageVO message, boolean isSend)
	{
		try {
			Stage messageDialog = new Stage(StageStyle.UTILITY);
			messageDialog.initModality(Modality.WINDOW_MODAL);
			messageDialog.setTitle("메세지");
			
			AnchorPane messageLayout = (AnchorPane)FXMLLoader.load(getClass().getResource("../../../fxml/MessageDialog.fxml"));
			Label lbFrom = (Label)messageLayout.lookup("#lbFrom");
			Label lbTo = (Label)messageLayout.lookup("#lbTo");
			TextArea taMessage = (TextArea)messageLayout.lookup("#taMessage");
			Button btnSend = (Button)messageLayout.lookup("#btnSend");
			Button btnCancel = (Button)messageLayout.lookup("#btnCancel");
			
			lbFrom.setText(message.getFrom());
			lbTo.setText(message.getTo());
			if(message.getMessage() != null)
				taMessage.setText(message.getMessage());
			
			btnSend.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) 
				{
					message.setMessage(taMessage.getText());
					iNetController.sendMessage(message);
					messageDialog.close();
				}
			});
			
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
			}
			
			Scene messageScene = new Scene(messageLayout);
			messageDialog.setScene(messageScene);
			messageDialog.setResizable(false);
			
			mainWindow = primaryStage.getScene().getWindow();
			messageDialog.setX(mainWindow.getX()-400);
			messageDialog.setY(mainWindow.getY());
			
			messageDialog.show();
		} 
		catch (IOException e) {
			System.out.println("로비컨트롤러 : 메세지만들다 오류");	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		}
	}
	
	
}
