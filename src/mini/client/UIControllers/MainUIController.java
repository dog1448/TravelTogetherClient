package mini.client.UIControllers;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.nio.channels.SocketChannel;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import mini.client.VO.UserVO;
import mini.client.network.Services.IListenerService;
import mini.client.network.Services.ListenerService;
import mini.client.network.controllers.INetController;
import mini.client.network.controllers.NetController;

public class MainUIController extends Application
{
//	===============================================================================================================
//	내정보 객체
	private UserVO userMe;
	
//	===============================================================================================================
//	컨트롤러 및 통신 멤버
	
	private INetController iNetController;
	
	private SocketChannel socketChannel;
	
	
	IListenerService iListenerService;
	
//	===============================================================================================================
//	UI관련 멤버
	private Stage primaryStage;
	BorderPane rootPane;
	AnchorPane loginPane;
	LoginController loginController;
	AnchorPane lobbyPane;
	LobbyController lobbyController;
	
	private Window mainWindow;
	private DialogController dialogController;
	
	
//	===============================================================================================================
//	Getter, Setter
	public Stage getPrimaryStage()
	{
		return primaryStage;
	}
	public INetController getiNetController() {
		return iNetController;
	}
	public UserVO getUserMe() {
		return userMe;
	}
	public void setUserMe(UserVO userMe) {
		this.userMe = userMe;
	}
	public DialogController getDialogController() {
		return dialogController;
	}
	
//	===============================================================================================================
//	메인
	public static void main(String[] args) 
	{
		launch(args);
	}

//	===============================================================================================================
//	start
	@Override
	public void start(Stage primaryStage) throws Exception 
	{
		this.primaryStage = primaryStage;
		readyToNetwork();
		System.out.println("MainUIController : readyToNetwork()");		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		getSocketFromController();
		System.out.println("MainUIController : getSocketFromController()");	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		System.out.println("MainUIController : new ReceiveController");	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		iListenerService = new ListenerService(socketChannel, primaryStage, this);
		iListenerService.startListener();
		setRootPane();
		setLoginPane();
		iListenerService.setMainUIController(this);
		this.dialogController = new DialogController(primaryStage,iNetController);
	}
	
	
//	===============================================================================================================
//	네트워크 준비
	public void readyToNetwork()
	{
		iNetController = new NetController();
		iNetController.startServerConnection();
	}
	
	public void getSocketFromController()
	{
		while(true)
		{
			if(iNetController.isConnected())
			{
				socketChannel = iNetController.getSocketChannel();
				System.out.println("MainUIController : 소켓받음");		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
				return;
			}
		}
	}
	
	
	
//	===============================================================================================================
//	UI 구동 관련 메서드
	
//	----------------------------------------------------------------------------------------------
//	Root UI 로딩
	public void setRootPane()
	{
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("../../../fxml/RootPane.fxml"));
		try {
			rootPane = (BorderPane)loader.load();
			Scene Scene = new Scene(rootPane);
			
			setWindowLocation();
			
			primaryStage.setScene(Scene);
			primaryStage.setTitle("Travel Together 1.0");
			
//			윈도우 사이즈 받아와서 오른쪽 끝에 창 소환
			Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
			primaryStage.setX(screen.getWidth()-480);
			primaryStage.setY(0);
			
			primaryStage.show();
		} 
		catch (IOException e) {
			System.out.println("MainUIController : error: setRootPane() / IOException");	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			e.printStackTrace();
		}
	}
	
//	-----------------------------------------------------------------------------------------------
//	로그인UI를 Root UI 센터에 세팅
	public void setLoginPane()
	{
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("../../../fxml/LoginPane.fxml"));
		try {
			loginPane = (AnchorPane)loader.load();
			rootPane.setCenter(loginPane);
			
			loginController = loader.getController();
			loginController.setMainController(this);
		} 
		catch (IOException e) {
			System.out.println("MainUIController : error: setLoginPane() / IOException");	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			e.printStackTrace();
		}
		loginController.setiNetController(iNetController);
		loginController.setiListenerService(iListenerService);
	}

//	-----------------------------------------------------------------------------------------------
//	LobbyUI를 RootUI 센터에 세팅
	public void setLobbyPane()
	{
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("../../../fxml/LobbyPane.fxml"));
		try {
			lobbyPane = (AnchorPane)loader.load();
			rootPane.setCenter(lobbyPane);
			
			lobbyController = loader.getController();
			lobbyController.setMainController(this);
			iListenerService.setLobbyController(lobbyController);
			
//			서버에서 리스트 받아오는 코드
//			친구 리스트 요청
			iNetController.getFriendList(userMe.getUserId());
			SleepMainUIController(500);
//			방 목록 요청
			iNetController.getRoomList(userMe.getUserId());
			dialogController.setLobbyController(lobbyController);
			dialogController.setiNetController(iNetController);
			dialogController.setUserMe(userMe);
			iListenerService.setDialogController(dialogController);
			
		} 
		catch (IOException e) {
			System.out.println("MainUIController : error: setLobbyPane() / IOException");	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			e.printStackTrace();
		}
	}
	
//	-----------------------------------------------------------------------------------------------
//	UI시작시 오른쪽에 세팅
	private void setWindowLocation()
	{
		
	}
	
//	-----------------------------------------------------------------------------------------------
//	스레드 잠시 휴식
	public void SleepMainUIController(int mSec)
	{
		try {
			Thread.sleep(mSec);
		} catch (InterruptedException e) {
			System.out.println("메인컨트롤러 스레드 sleep중 예외");	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		}
	}
	
//	-----------------------------------------------------------------------------------------------
//	리스너에 챗컨트롤러 세팅
	public void setChatRoomControllerToListener(ChatRoomController chatRoomController)
	{
		iListenerService.setChatRoomController(chatRoomController);
	}
	
	public void setPlanControllerToListener(PlanController planController)
	{
		iListenerService.setPlanController(planController);
	}
	
	
	
	
	
}
