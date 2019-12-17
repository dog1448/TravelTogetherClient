package mini.client.UIControllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Comparator;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import mini.client.VO.PlanVO;
import mini.client.VO.RoomVO;
import mini.client.VO.SearchVO;
import mini.client.VO.UserVO;
import mini.client.network.Services.DistanceCalcService;
import mini.client.network.Services.IDistanceCalcService;
import mini.client.network.controllers.INetController;
import mini.client.network.staticValues.ServerConnectionStaticValues;

public class PlanController implements Initializable
{
//	===============================================================================================================
//	필수 멤버들
	private MainUIController mainController;
	private Stage primaryStage;
	private INetController iNetController;
	private DialogController dialogController;
	private UserVO userMe;
	private RoomVO roomNow;
	private LobbyController lobbyController;
	private Stage planInfoDialog;
	
	private Stage addPlanDialog;
	private String dateNow;
	private ChatRoomController chatRoomController;
	
	private ObservableList<PlanVO> allPlanList;
	
	private IDistanceCalcService iDistanceCalcService;

	Stage selectstage;
	TextField tfLoc;
	
//	===============================================================================================================
//	Getter, Setter
	public void setLobbyController(LobbyController lobbyController) {
		this.lobbyController = lobbyController;
	}
//	구분 아이템 리스트 추가 메서드
	public void addComboItemList(ObservableList<String> comboItemsList)
	{
		for(String s : comboItemsList)
		{
			this.comboItemsList.add(s);
		}
	}
//	채팅방 객체 여기에 세팅
	public void setChatRoomController(ChatRoomController chatRoomController) {
		this.chatRoomController = chatRoomController;
	}
	
	public void setTfLoc(String loc)
	{
		tfLoc.setText(loc);
	}
	
	public void closeSelectStage()
	{
		selectstage.close();
	}
	
	public PlanController getPlanContoller()
	{
		return this;
	}
	
//	===============================================================================================================
//	컴포넌트들
	@FXML private Button btnAddPlan;
	@FXML private Button btnDeletePlan;
	@FXML private Button btnVotePlan;
	@FXML private Button btnAgreeAll;
	@FXML private Button btnSelectDate;
	@FXML private Label lbDate;
	
	private ObservableList<String> comboItemsList = FXCollections.observableArrayList();
	
	
//	===============================================================================================================
//	상세정보 페이지 관련 컴포넌트들
	TextField tfTitleI;
	TextField tfMoneyI;
	TextField tfLocI;
	TextArea taOtherI;
	TextField tfLinkI;
	Label lbTimeI;
	TextField tfTimeI;
	ComboBox<String> comboBoxPurposI;
	Button btnAgreeI;
	Button btnCancelI;
	AnchorPane planInfoLayout;
	ImageView imgViewInfo;
	
	File selectedAddPlanImgFile;
	
//	===============================================================================================================
//	상세정보 페이지 세팅 메서드
	private void createInfoDialog()
	{
		try {
			planInfoDialog = new Stage(StageStyle.UTILITY);
			planInfoDialog.initModality(Modality.WINDOW_MODAL);
			planInfoDialog.initOwner(primaryStage);
			planInfoLayout = (AnchorPane)FXMLLoader.load(getClass().getResource("../../../fxml/PlanInfoDialog.fxml"));
			tfTitleI = (TextField)planInfoLayout.lookup("#tfTitle");
			tfMoneyI = (TextField)planInfoLayout.lookup("#tfMoney");
			tfLocI = (TextField)planInfoLayout.lookup("#tfLoc");
			taOtherI = (TextArea)planInfoLayout.lookup("#taOther");
			tfLinkI = (TextField)planInfoLayout.lookup("#tfLink");
			lbTimeI = (Label)planInfoLayout.lookup("#lbTime");
			tfTimeI = (TextField)planInfoLayout.lookup("#tfTime");
			comboBoxPurposI = (ComboBox<String>)planInfoLayout.lookup("#comboBoxPurpos");
			comboBoxPurposI.setItems(comboItemsList);
			comboBoxPurposI.setDisable(true);
			btnAgreeI = (Button)planInfoLayout.lookup("#btnAgree");
			btnCancelI = (Button)planInfoLayout.lookup("#btnCancel");
			imgViewInfo = (ImageView)planInfoLayout.lookup("#imgViewInfo");

			
			
//			플랜정보 다이얼로그 캔슬버튼
			btnCancelI.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					if(planInfoDialog.isShowing())
						planInfoDialog.close();
				}
			});
		} catch (IOException e1) {
			System.out.println("플레너컨트롤러 : 플렌상세정보 다이얼로그 만들다 오류"+e1.getMessage());	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		}
	}
//	기초 정보 받아와서 정보다이얼로그에 세팅
	public void setBasicInfoToDialog(PlanVO plan)
	{
		tfTitleI.setText(plan.getPlanName());
		tfMoneyI.setText(""+plan.getPlanMoney());
		tfLocI.setText(plan.getPlanLoc());
		taOtherI.setText(plan.getPlanOther());
		tfLinkI.setText(plan.getPlanLink());
//		comboBoxPurposI
	}
	
	
//	모든 플랜 받아온걸 절절히 세팅(시작할때..)
	public void addAllPlanToIndivisualList(ObservableList<PlanVO> planList)
	{
		if(allPlanList == null)
			allPlanList = planList;
		for(PlanVO plan : planList)
		{
//			일반 플랜일때
			if(plan.getPlanState().equals("nor"))
			{
				firstPlanList.add(plan);
			}
//			투표중인 플랜일때
			else if(plan.getPlanState().equals("vot"))
			{
				firstPlanList.add(plan);
				voteAllPlanList.add(plan);
				if(plan.getPlanDate().equals(dateNow))
				{
					votePlanList.add(plan);
				}
			}
//			확정 플랜일때
			else if(plan.getPlanState().equals("fin"))
			{
				finalAllPlanList.add(plan);
				if(plan.getPlanDate().equals(dateNow))
				{
					finalPlanList.add(plan);
				}
			}
		}
		sortFinalPlanTable();
		setDistanceOfFinalPlanList();
	}
	
//	테이블 리스트 전부 클리어
	public void clearAllTableList()
	{
		firstPlanList.clear();
		voteAllPlanList.clear();
		votePlanList.clear();
		finalAllPlanList.clear();
		finalPlanList.clear();
	}
	
	
	
//	===============================================================================================================
//	후보 플렌 테이블 관련
	private ObservableList<PlanVO> firstPlanList = FXCollections.observableArrayList();
	
	@FXML private TableView<PlanVO> firstPlanTable;
	
	@FXML private TableColumn<PlanVO, String> firstPlanTitleColumn;
	@FXML private TableColumn<PlanVO, String> firstPlanPurposeColumn;
	@FXML private TableColumn<PlanVO, Integer> firstPlanDistanceColumn;
	
//	개별적인 플랜 추가(무조건 후보 플랜)
	public void addPlanTofirstPlanList(PlanVO plan)
	{
		allPlanList.add(plan);
		firstPlanList.add(plan);
		clearleftTableList();
		addLeftPlanTOTable(allPlanList);
	}
//	개별적인 플랜 삭제
	public void deletePlanFromFirstPlanList(PlanVO plan)
	{
		for(int i=0; i<allPlanList.size(); i++)
		{
			if(allPlanList.get(i).getPlanNum() == plan.getPlanNum())
			{
				allPlanList.remove(i);
				break;
			}
		}
		clearAllTableList();
		addAllPlanToIndivisualList(allPlanList);
		refreashFirstAndVoteTableList();
		setDistanceOfFinalPlanList();
	}

	
//	후보플랜을 투표 플랜에 추가
	public void addCandidatePlanToVotePlanList(int planNum, String planTime, String planDate)
	{
		for(int i=0; i<allPlanList.size(); i++)
		{
			if(allPlanList.get(i).getPlanNum() == planNum)
			{
				allPlanList.get(i).setPlanTime(planTime);
				allPlanList.get(i).setPlanState("vot");
				allPlanList.get(i).setPlanAgreeNum(1);
				allPlanList.get(i).setPlanDate(planDate);
				clearleftTableList();
				addLeftPlanTOTable(allPlanList);
				refreashFirstAndVoteTableList();
				return;
			}
		}
	}
	
//	투표 동의 이벤트가 일어났을떄 처리...
	// 1(1.모두동의, 2.아직남음):'플랜번호':'동의유저ID'
	public void agreePlanReceive(String finalString)
	{
//		프로토콜 이상하면 끝냄
		if(finalString.charAt(0) == '1' || finalString.charAt(0) == '2') 
		{}
		else
			return;
		
		StringTokenizer st = new StringTokenizer(finalString, ":");
		String agreeState = st.nextToken();
		int agreePlanNum = Integer.parseInt(st.nextToken());
		String agreeUserId = st.nextToken();
		
//		동의 유저가 나일때 agreeSet에 추가
		if(userMe.getUserId().equals(agreeUserId))
		{
			userMe.getAgreeSet().add(""+agreePlanNum);
		}
		
//		해당 플랜 찾기
		for(PlanVO plan : votePlanList)
		{
			if(plan.getPlanNum() == agreePlanNum)
			{
				plan.setPlanAgreeNum(plan.getPlanAgreeNum()+1);
//				모두 동의 했을떄
				if(agreeState.equals("1"))
				{
					plan.setPlanState("fin");
					finalPlanList.add(plan);
					votePlanList.remove(plan);
				}
				sortFinalPlanTable();
				
				setDistanceOfFinalPlanList();
				refreashFirstAndVoteTableList();
				return;
			}
		}
	}
	
//	===============================================================================================================
//	투표 플렌 테이블 관련
	private ObservableList<PlanVO> votePlanList = FXCollections.observableArrayList();
	private ObservableList<PlanVO> voteAllPlanList = FXCollections.observableArrayList();
	
	@FXML private TableView<PlanVO> votePlanTable;
	
	@FXML private TableColumn<PlanVO, String> votePlanTimeColumn;
	@FXML private TableColumn<PlanVO, String> votePlanTitleColumn;
	@FXML private TableColumn<PlanVO, String> votePlanPurposeColumn;
	@FXML private TableColumn<PlanVO, Integer> votePlanDistanceColumn;
	@FXML private TableColumn<PlanVO, Integer> votePlanVoteStateColumn;
	
//	===============================================================================================================
//	확정 플렌 테이블 관련
	private ObservableList<PlanVO> finalPlanList = FXCollections.observableArrayList();
	private ObservableList<PlanVO> finalAllPlanList = FXCollections.observableArrayList();
	
	@FXML private TableView<PlanVO> finalPlanTable;
	
	@FXML private TableColumn<PlanVO, String> finalPlanTimeColumn;
	@FXML private TableColumn<PlanVO, String> finalPlanTitleColumn;
	@FXML private TableColumn<PlanVO, String> finalPlanPurposeColumn;
	@FXML private TableColumn<PlanVO, Integer> finalPlanDistanceColumn;
	
//	===============================================================================================================
//	장소선택 다이얼로그 테이블 관련
	private ObservableList<SearchVO> searchList = FXCollections.observableArrayList();
	
	
	
	
//	***************************************************************************************************************
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
		dateNow = room.getRoomStartDate();
		lbDate.setText(dateNow+" PLAN");
		mainController.setPlanControllerToListener(this);
		
		firstPlanTable.setItems(firstPlanList);
		votePlanTable.setItems(votePlanList);
		finalPlanTable.setItems(finalPlanList);
		
//		콤보 박스 리스트 요청
		mainController.SleepMainUIController(200);
		iNetController.getComboPurposeList();
	}
	
	
//	===============================================================================================================
//	===============================================================================================================
//	INITIALIZE 시작
	
	@Override
	public void initialize(URL location, ResourceBundle resources) 
	{
//		===============================================================================================================
//		거리계산 서비스 초기화
		iDistanceCalcService = new DistanceCalcService();

		
//		===============================================================================================================
//		후보 플렌 테이블 관련 설정
		firstPlanTitleColumn.setCellValueFactory(cellData -> cellData.getValue().getPlanNameProperty());
		firstPlanPurposeColumn.setCellValueFactory(cellData -> cellData.getValue().getPlanPurposeNameProperty());
		firstPlanDistanceColumn.setCellValueFactory(cellData -> cellData.getValue().getPlanDistanceProperty().asObject());
		
//		---------------------------------------------------------------------------------------------------------------
//		후보 테이블 클릭시 상세정보
		firstPlanTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if(event.getClickCount()==2 && firstPlanTable.getItems().size()!=0 && firstPlanTable.isFocused())
				{
					createInfoDialog();
					
					PlanVO plan = firstPlanTable.getSelectionModel().getSelectedItem();
					setBasicInfoToDialog(plan);
					
					
//					이미지 받아오기 위한 프로토콜
					iNetController.getImgFromServer(plan.getPlanNum());
					setImgToPlanInfo();
					
					planInfoDialog.setTitle(plan.getPlanName());
					
					lbTimeI.setVisible(false);
					tfTimeI.setVisible(false);
					btnAgreeI.setVisible(false);
					
					Scene planInfoScene = new Scene(planInfoLayout);
					planInfoDialog.setScene(planInfoScene);
					planInfoDialog.setResizable(false);
					planInfoDialog.show();
				}
			}
		});
		
//		===============================================================================================================
//		투표 플렌 테이블 관련 설정
		votePlanTimeColumn.setCellValueFactory(cellData -> cellData.getValue().getPlanTimeProperty());
		votePlanTitleColumn.setCellValueFactory(cellData -> cellData.getValue().getPlanNameProperty());
		votePlanPurposeColumn.setCellValueFactory(cellData -> cellData.getValue().getPlanPurposeNameProperty());
		votePlanDistanceColumn.setCellValueFactory(cellData -> cellData.getValue().getPlanDistanceProperty().asObject());
		votePlanVoteStateColumn.setCellValueFactory(cellData -> cellData.getValue().getPlanAgreeNumProperty().asObject());
		
//		---------------------------------------------------------------------------------------------------------------
//		투표 플랜 테이블 클릭시 상세정보
		votePlanTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if(event.getClickCount()==2 && votePlanTable.getItems().size()!=0 && votePlanTable.isFocused())
				{
					createInfoDialog();
					
					PlanVO plan = votePlanTable.getSelectionModel().getSelectedItem();
					setBasicInfoToDialog(plan);
					
//					이미지 받아오기 위한 프로토콜
					iNetController.getImgFromServer(plan.getPlanNum());
					setImgToPlanInfo();
					
					planInfoDialog.setTitle(plan.getPlanName());
					
					tfTimeI.setText(plan.getPlanTime());
					
//					이미 동의 한 거면 동의버튼 안보임
					if(userMe.getAgreeSet().contains(""+plan.getPlanNum()))
						btnAgreeI.setVisible(false);
					
//					동의 버튼 누르면 작동
					btnAgreeI.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							PlanVO votePlan = votePlanTable.getSelectionModel().getSelectedItem();
							iNetController.agreePlan(votePlan, userMe.getUserId(), chatRoomController.getRoomMemberSize());
							planInfoDialog.close();
						}
					});
					
					Scene planInfoScene = new Scene(planInfoLayout);
					planInfoDialog.setScene(planInfoScene);
					planInfoDialog.setResizable(false);
					planInfoDialog.show();
				}
			}
		});
		
//		===============================================================================================================
//		확정 플렌 테이블 관련 설정
		finalPlanTimeColumn.setCellValueFactory(cellData -> cellData.getValue().getPlanTimeProperty());
		finalPlanTitleColumn.setCellValueFactory(cellData -> cellData.getValue().getPlanNameProperty());
		finalPlanPurposeColumn.setCellValueFactory(cellData -> cellData.getValue().getPlanPurposeNameProperty());
		finalPlanDistanceColumn.setCellValueFactory(cellData -> cellData.getValue().getPlanDistanceProperty().asObject());
		
//		---------------------------------------------------------------------------------------------------------------
//		확정테이블 선택시마다 거리계산기에 입력
		finalPlanTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<PlanVO>() {
			@Override
			public void changed(ObservableValue<? extends PlanVO> observable, PlanVO oldValue, PlanVO newValue) 
			{
				iDistanceCalcService.setSelectedFinalPlan(newValue);
				refreashFirstAndVoteTableList();
			}
		});
		
//		---------------------------------------------------------------------------------------------------------------
//		확정 플랜 테이블 클릭시 상세정보
		finalPlanTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if(event.getClickCount()==2 && finalPlanTable.getItems().size()!=0 && finalPlanTable.isFocused())
				{
					createInfoDialog();
					
					PlanVO plan = finalPlanTable.getSelectionModel().getSelectedItem();
					setBasicInfoToDialog(plan);
					
//					이미지 받아오기 위한 프로토콜
					iNetController.getImgFromServer(plan.getPlanNum());
					setImgToPlanInfo();
					
					planInfoDialog.setTitle(plan.getPlanName());
					
					tfTimeI.setText(plan.getPlanTime());
					btnAgreeI.setVisible(false);
					
					Scene planInfoScene = new Scene(planInfoLayout);
					planInfoDialog.setScene(planInfoScene);
					planInfoDialog.setResizable(false);
					planInfoDialog.show();
				}
			}
		});
		
		
		
//		플렌 추가 버튼 클릭시
		btnAddPlan.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) 
			{
				try {
					addPlanDialog = new Stage(StageStyle.UTILITY);
					addPlanDialog.initModality(Modality.WINDOW_MODAL);
					addPlanDialog.initOwner(primaryStage);
					addPlanDialog.setTitle("여행 일정 추가");
					
					AnchorPane addPlanLayout = (AnchorPane)FXMLLoader.load(getClass().getResource("../../../fxml/AddPlanDialog.fxml"));
					TextField tfTitle = (TextField)addPlanLayout.lookup("#tfTitle");
					TextField tfMoney = (TextField)addPlanLayout.lookup("#tfMoney");
					tfLoc = (TextField)addPlanLayout.lookup("#tfLoc");
					TextArea taOther = (TextArea)addPlanLayout.lookup("#taOther");
					TextField tfLink = (TextField)addPlanLayout.lookup("#tfLink");
					ComboBox<String> comboBoxPurpos = (ComboBox<String>)addPlanLayout.lookup("#comboBoxPurpos");
					comboBoxPurpos.setItems(comboItemsList);
					Button btnSubmit = (Button)addPlanLayout.lookup("#btnSubmit");
					Button btnCancel = (Button)addPlanLayout.lookup("#btnCancel");
					Button btnGetLoc = (Button)addPlanLayout.lookup("#btnGetLoc");
					
					Button btnAddImg = (Button)addPlanLayout.lookup("#btnAddImg");
					ImageView imgViewPhoto = (ImageView)addPlanLayout.lookup("#imgViewPhoto");
					imgViewPhoto.setImage(new Image("/imgs/nope.jpg",300,400,false,true));
					
					
//					************************************************************************************************************
//					************************************************************************************************************
//					사진 추가 버튼
					btnAddImg.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) 
						{
							FileChooser fileChooser = new FileChooser();
							fileChooser.getExtensionFilters().addAll(
								new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"),
								new ExtensionFilter("All Files", "*.*")
							);
							selectedAddPlanImgFile = fileChooser.showOpenDialog(primaryStage);
							if(selectedAddPlanImgFile!=null)
							{
								System.out.println(selectedAddPlanImgFile.toURI().toString());
								imgViewPhoto.setImage(new Image(selectedAddPlanImgFile.toURI().toString(),300,400,false,true));
								iNetController.sendImageToServer(selectedAddPlanImgFile);
							}
						}
					});
//					************************************************************************************************************
//					************************************************************************************************************
					
					
//					위치 받는 버튼
					btnGetLoc.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							try {
								Stage getLocationDialog = new Stage(StageStyle.UTILITY);
								getLocationDialog.initModality(Modality.WINDOW_MODAL);
								getLocationDialog.initOwner(primaryStage);
								getLocationDialog.setTitle("좌표 찾기");
								
								AnchorPane getLocationLayout = (AnchorPane)FXMLLoader.load(getClass().getResource("../../../fxml/GetLocationDialog.fxml"));
								TextField tfLocation = (TextField)getLocationLayout.lookup("#tfLocation");
								Button btnSearch = (Button)getLocationLayout.lookup("#btnSearch");
								Button btnCancel = (Button)getLocationLayout.lookup("#btnCancel");
								
//								검색 버튼
								btnSearch.setOnAction(new EventHandler<ActionEvent>() {
									@Override
									public void handle(ActionEvent event) {
										String result = searchLocation(tfLocation.getText());
										if(result == null)
										{
											dialogController.newAlertDialog("통신 문제", "통신에 문제가 있습니다.", "인터넷 연결을 확인해 주세요", AlertType.WARNING);
										}
										else
										{
											int resultNum = setResultList(result);
											if(resultNum < 1)
											{
												dialogController.newAlertDialog("검색 문제", "검색결과가 없습니다.", "다른것을 검색해 주세요", AlertType.WARNING);
											}
											else
											{
												selectstage = new Stage(StageStyle.UTILITY);
												selectstage.initModality(Modality.WINDOW_MODAL);
												selectstage.initOwner(primaryStage);
												selectstage.setTitle("장소 선택");
												FXMLLoader loaderS = new FXMLLoader();
												loaderS.setLocation(getClass().getResource("../../../fxml/SelectPlaceDialog.fxml"));
												try {
													AnchorPane selectDialog = (AnchorPane)loaderS.load();
													SelectPlaceController selectPlaceController = loaderS.getController();
													Scene selectScene = new Scene(selectDialog);
													selectstage.setScene(selectScene);
													selectPlaceController.setPlanController(getPlanContoller());
													selectPlaceController.setSearchList(searchList);
													selectstage.show();
													
												} catch (IOException e) {
													System.out.println("플랜컨트롤러 -> 장소선택 다이얼로그 만들다 에러: "+e.getMessage());		//~~~~~~~~~~~~~~~~~~~~~~
												}
												getLocationDialog.close();
											}
											
											
										}
											
									}
								});
								
//								취소버튼
								btnCancel.setOnAction(new EventHandler<ActionEvent>() {
									@Override
									public void handle(ActionEvent event) {
										getLocationDialog.close();
									}
								});
								
								Scene getLocation = new Scene(getLocationLayout);
								getLocationDialog.setScene(getLocation);
								getLocationDialog.setResizable(false);
								getLocationDialog.show();
							} catch (IOException e) {
								System.out.println("플랜컨트롤러 -> 위치 좌표 다이얼로그 만들다 에러: "+e.getMessage());		//~~~~~~~~~~~~~~~~~~~~~~
							}
						}
					});
					
					
					
//					확인 버튼
					btnSubmit.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) 
						{
							String title = tfTitle.getText();
							String purpose = comboBoxPurpos.getSelectionModel().getSelectedItem();
							int money;
							
							String loc = tfLoc.getText();
							String other = taOther.getText();
							String link = tfLink.getText();
							if(title.equals(""))
							{
								dialogController.newAlertDialog("양식 오류", "제목이 없습니다", "제목을 입력해 주세요", AlertType.WARNING);
								return;
							}
							else if(purpose == null || purpose.equals(""))
							{
								dialogController.newAlertDialog("양식 오류", "구분이 없습니다", "구분을 선택해 주세요", AlertType.WARNING);
								return;
							}
							try {
								if(tfMoney.getText().equals(""))
									money = -1;
								else
									money = Integer.parseInt(tfMoney.getText());
							} catch (NumberFormatException e) {
								dialogController.newAlertDialog("양식 오류", "숫자가 아닙니다", "예상지출에 숫자만 입력해 주세요", AlertType.WARNING);
								return;
							}
							
							PlanVO plan = new PlanVO();
							plan.setPlanName(title);
							plan.setPlanPurposeName(purpose);
							plan.setPlanMoney(money);
							if(loc.equals(""))
								loc = "nope";
							plan.setPlanLoc(loc);
							
							if(other.equals(""))
								other = "nope";
							plan.setPlanOther(other);
							
							if(link.equals(""))
								link = "nope";
							plan.setPlanLink(link);
							plan.setPlanDate(dateNow);
							
							if(selectedAddPlanImgFile != null)
							{
								plan.setPlanImgLoc(selectedAddPlanImgFile.getName());
								selectedAddPlanImgFile = null;
							}
							else
							{
								plan.setPlanImgLoc("nope");
							}
							
							plan.setPlanUserId(userMe.getUserId());
							plan.setPlanRoomNum(roomNow.getRoomNum());
							
							iNetController.addCandidatePlan(plan);
							addPlanDialog.close();
						}
					});
					
//					취소 버튼
					btnCancel.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							addPlanDialog.close();
						}
					});
					

					
					
					Scene addPlanScene = new Scene(addPlanLayout);
					addPlanDialog.setScene(addPlanScene);
					addPlanDialog.setResizable(false);
					addPlanDialog.show();
					
					
				} catch (IOException e) {
					System.out.println("플레너컨트롤러 : 플렌추가 다이얼로그 만들다 오류"+e.getMessage());	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
				}
				
				
			}
		});
		
//		후보 플랜 삭제 버튼
		btnDeletePlan.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				PlanVO plan = firstPlanTable.getSelectionModel().getSelectedItem();
				if(userMe.getUserId().equals(plan.getPlanUserId()))
					iNetController.deleteCandidatePlan(plan);
				else
					dialogController.newAlertDialog("삭제 실패", "작성자만 삭제 가능합니다", "작성자에게 삭제를 요청해 주세요", AlertType.WARNING);
			}
		});
		
//		투표 넘기기 버튼
		btnVotePlan.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				PlanVO selectedFirstPlan = firstPlanTable.getSelectionModel().getSelectedItem();
				if(selectedFirstPlan == null)
				{
					dialogController.newAlertDialog("오류", "투표할 대상이 없음", "찬/반 투표할 대상을 선택해주세요", AlertType.WARNING);
					return;
				}
				else
				{
					try {
						Stage addTimeDialog = new Stage(StageStyle.UTILITY);
						addTimeDialog.initModality(Modality.WINDOW_MODAL);
						addTimeDialog.initOwner(primaryStage);
						addTimeDialog.setTitle("시간 설정");
						
						AnchorPane addTimeLayout = (AnchorPane)FXMLLoader.load(getClass().getResource("../../../fxml/SelectTime.fxml"));
						TextField tfTime = (TextField)addTimeLayout.lookup("#tfTime");
						Button btnSend2 = (Button)addTimeLayout.lookup("#btnSend");
						Button btnCancel = (Button)addTimeLayout.lookup("#btnCancel");
						
//						시간추가 보내기 버튼
						btnSend2.setOnAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent event) {
								if(tfTime.getText().equals(""))
								{
									dialogController.newAlertDialog("양식 오류", "시간을 입력해 주세요", "시간이 없습니다.", AlertType.WARNING);
									return;
								}
								else
								{
									if(checkFormatOfTime(tfTime.getText()))
									{
										PlanVO plan = firstPlanTable.getSelectionModel().getSelectedItem();
//										현재 노말 상태가 아니라면
										if(!plan.getPlanState().equals("nor"))
										{
											dialogController.newAlertDialog("투표 오류", "이미 투표/확정 플랜에 존재합니다", "이미 확정된 플랜입니다.\n다른 플랜을 선택해 주세요", AlertType.WARNING);
											return;
										}
										plan.setPlanTime(tfTime.getText());
										userMe.getAgreeSet().add(""+plan.getPlanNum());
										iNetController.addPlanToVote(plan, roomNow.getRoomNum(), dateNow);
										addTimeDialog.close();
									}
									else
									{
										dialogController.newAlertDialog("양식 오류", "시간을 양식이 다릅니다", "시간을 24시시계로 00:00으로 입력해 주세요.", AlertType.WARNING);
										return;
									}
								}
								
							}
						});
						
//						시간추가 캔슬버튼
						btnCancel.setOnAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent event) {
								addTimeDialog.close();
							}
						});
						
						Scene addTimeScene = new Scene(addTimeLayout);
						addTimeDialog.setScene(addTimeScene);
						addTimeDialog.setResizable(false);
						addTimeDialog.show();
					} catch (IOException e) {
						System.out.println("플레너컨트롤러 : 투표 추가 다이얼로그 만들다 오류"+e.getMessage());	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
					}
				}
			}
		});
		
//		일괄동의 버튼
		btnAgreeAll.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) 
			{
				Set<String> agSet = userMe.getAgreeSet();
				for(PlanVO plan : votePlanList)
				{
					if(!agSet.contains(""+plan.getPlanNum()))
					{
						iNetController.agreePlan(plan, userMe.getUserId(), chatRoomController.getRoomMemberSize());
						mainController.SleepMainUIController(10);
					}
				}
			}
		});
		
//		날짜 선택
		btnSelectDate.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event)
			{
				try {
					Stage selectDateDialog = new Stage(StageStyle.UTILITY);
					selectDateDialog.initModality(Modality.WINDOW_MODAL);
					selectDateDialog.initOwner(primaryStage);
					selectDateDialog.setTitle("날짜 설정");
					
					AnchorPane selectDateLayout = (AnchorPane)FXMLLoader.load(getClass().getResource("../../../fxml/SelectDate.fxml"));
					Label lbStartDate = (Label)selectDateLayout.lookup("#lbStartDate");
					Label lbEndDate = (Label)selectDateLayout.lookup("#lbEndDate");
					DatePicker dpSelectDate = (DatePicker)selectDateLayout.lookup("#dpSelectDate");
					Button btnOk = (Button)selectDateLayout.lookup("#btnOk");
					Button btnCancel = (Button)selectDateLayout.lookup("#btnCancel");
					
					lbStartDate.setText(roomNow.getRoomStartDate());
					lbEndDate.setText(roomNow.getRoomEndDate());
					
//					확인 버튼 (날짜 선택)
					btnOk.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) 
						{
							String selectedDate = dpSelectDate.getValue().toString().replace('-', '/');
							dateNow = selectedDate;
							lbDate.setText(dateNow+" PLAN");
							clearAllTableList();
							addAllPlanToIndivisualList(allPlanList);
							refreashFirstAndVoteTableList();
							selectDateDialog.close();
						}
					});
					
					
//					취소버튼
					btnCancel.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							selectDateDialog.close();
						}
					});
					
					Scene selectDateScene = new Scene(selectDateLayout);
					selectDateDialog.setScene(selectDateScene);
					selectDateDialog.setResizable(false);
					selectDateDialog.show();
					
					
					
				} catch (IOException e) {
					System.out.println("플레너컨트롤러 : 날짜 선택 다이얼로그 만들다 오류"+e.getMessage());	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
				}
			}
		});
		
		
		
	}	// Initialize
	
//	시간 입력할때 형식에 맞는것인지 확인
	public boolean checkFormatOfTime(String time)
	{
		if(time.length() != 5)
			return false;
		if(time.charAt(0) < '0' || time.charAt(0) > '2')
			return false;
		if(time.charAt(1) < '0' || time.charAt(1) > '9')
			return false;
		else
		{
			if(time.charAt(0) == '2' && time.charAt(1) > '3')
			{
				return false;
			}
		}
		if(time.charAt(2) != ':')
			return false;
		if(time.charAt(3) < '0' || time.charAt(3) > '5')
			return false;
		if(time.charAt(4) < '0' || time.charAt(4) > '9')
			return false;
			
		return true;
	}
	
	public void refreashFirstAndVoteTableList()
	{
		for(PlanVO plan : allPlanList)
		{
			if(!plan.getPlanState().equals("fin"))
			{
				plan.setPlanDistance(iDistanceCalcService.getDistanceFromSelectedFinalPlan(plan.getPlanLoc()));
			}
		}
		clearleftTableList();
		addLeftPlanTOTable(allPlanList);
	}
	
	public void clearleftTableList()
	{
		firstPlanList.clear();
		voteAllPlanList.clear();
		votePlanList.clear();
	}
	public void addLeftPlanTOTable(ObservableList<PlanVO> allPlanList)
	{
		for(PlanVO plan : allPlanList)
		{
//			일반 플랜일때
			if(plan.getPlanState().equals("nor"))
			{
				firstPlanList.add(plan);
			}
//			투표중인 플랜일때
			if(plan.getPlanState().equals("vot"))
			{
				firstPlanList.add(plan);
				voteAllPlanList.add(plan);
				if(plan.getPlanDate().equals(dateNow))
				{
					votePlanList.add(plan);
				}
			}
		}
		
		sortFinalPlanTable();
		
	}
	
	public void sortFinalPlanTable()
	{
		finalPlanList.sort(new Comparator<PlanVO>() {
			@Override
			public int compare(PlanVO o1, PlanVO o2) {
				if(Integer.parseInt(o1.getPlanTime().substring(0, 2))>Integer.parseInt(o2.getPlanTime().substring(0, 2)))
				{
					return 1;
				}
				else if(Integer.parseInt(o1.getPlanTime().substring(0, 2))<Integer.parseInt(o2.getPlanTime().substring(0, 2)))
				{
					return -1;
				}
				else
				{
					if(Integer.parseInt(o1.getPlanTime().substring(3).trim())>Integer.parseInt(o2.getPlanTime().substring(3).trim()))
					{
						return 1;
					}
					else if(Integer.parseInt(o1.getPlanTime().substring(3).trim())<Integer.parseInt(o2.getPlanTime().substring(3).trim()))
					{
						return -1;
					}
				}
				return 0;
			}
		});
	}
	
//	확정 플랜의 거리 계산
	public void setDistanceOfFinalPlanList()
	{
		if(finalPlanList.size() == 0)
			return;
		
		finalPlanList.get(0).setPlanDistance(0);
		int startNum=Integer.MAX_VALUE-1;
		int prevNum=-1;
		for(int i=0; i<finalPlanList.size(); i++)
		{
			if(finalPlanList.get(i).getPlanLoc() != null)
			{
				startNum=i;
				prevNum=i;
				break;
			}
		}
		for(int i=startNum+1; i<finalPlanList.size(); i++)
		{
			if(finalPlanList.get(i).getPlanLoc() == null)
				continue;
			
			while(prevNum < i)
			{
				if(finalPlanList.get(prevNum).getPlanLoc() == null)
				{
					prevNum++;
					continue;
				}
				else
				{
					finalPlanList.get(i).setPlanDistance(iDistanceCalcService.getDistanceFromTwoPoint(finalPlanList.get(i).getPlanLoc(), finalPlanList.get(prevNum).getPlanLoc()));
					prevNum++;
					break;
				}
				
			}
			
		}
	}
	
	private ServerSocketChannel fileServerSocketChannel = null;
//	이미지 받아오기위한 준비
	public void setImgToPlanInfo()
	{
		String filePath="";
		try {
			if(fileServerSocketChannel == null)
			{
				fileServerSocketChannel = ServerSocketChannel.open();
				try {
					fileServerSocketChannel.bind(new InetSocketAddress(ServerConnectionStaticValues.SERVER_INET_ADDRESS, ServerConnectionStaticValues.C_FILE_SERVER_PORT));
				} catch (Exception e) {
					System.out.println("bindException******************************************************");
					fileServerSocketChannel = null;
					return;}
			}
			SocketChannel fileSocketChannel = fileServerSocketChannel.accept();

			String fileName = "";
			Charset charset = Charset.forName("UTF-8");
			ByteBuffer fileNameByteBuffer = ByteBuffer.allocate(500);
			int byteCnt = fileSocketChannel.read(fileNameByteBuffer);
			
			fileNameByteBuffer.flip();
			fileName += charset.decode(fileNameByteBuffer).toString();
			
			if(fileName.trim().equals("nope"))
			{
				imgViewInfo.setImage(new Image("/imgs/nope.jpg",300,400,false,true));
				return;
			}
			
			filePath = "X:/miniproject1/client/imgs/"+fileName.trim();
			
			File file = new File(filePath);
			System.out.println(file.getPath());

			ByteBuffer getImgBuffer = ByteBuffer.allocate(100000);
			int bytesRead = fileSocketChannel.read(getImgBuffer);
			FileOutputStream getImgFileOutputStream = new FileOutputStream(file);
			FileChannel getImgFileChannel = getImgFileOutputStream.getChannel();

			while(bytesRead != -1) 
			{
				getImgBuffer.flip();
				getImgFileChannel.write(getImgBuffer);
				getImgBuffer.compact();
				bytesRead = fileSocketChannel.read(getImgBuffer);
			}
			
			getImgFileChannel.close();
			getImgFileOutputStream.close();
			fileSocketChannel.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally
		{
			try {fileServerSocketChannel.close();} catch (IOException e) {}
			fileServerSocketChannel = null;
		}
		File file = new File(filePath);
		imgViewInfo.setImage(new Image(file.toURI().toString(),300,400,false,true));
	}
	
	
	
//	좌표 검색 메서드 (제이슨 타입 리턴)
	public String searchLocation(String loc)
	{
		String clientId = ServerConnectionStaticValues.MY_NAVER_API_ID;// 애플리케이션 클라이언트 아이디값";
		String clientSecret = ServerConnectionStaticValues.MY_NAVER_API_SECRET_CODE;// 애플리케이션 클라이언트 시크릿값";
		try {
			String text = URLEncoder.encode(loc, "UTF-8");
			String apiURL = "https://naveropenapi.apigw.ntruss.com/map-place/v1/search?query=" + text+"&coordinate=127.1054328,37.3595963"; // json 결과
			URL url = new URL(apiURL);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
			con.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);
			int responseCode = con.getResponseCode();
			BufferedReader br;
			if (responseCode == 200) { // 정상 호출
				br = new BufferedReader(new InputStreamReader(con.getInputStream()));
			} else { // 에러 발생
				br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
			}
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = br.readLine()) != null) {
				response.append(inputLine);
			}
			br.close();
			return response.toString();
		} catch (Exception e) {
			System.out.println(e);
		}
		return null;
	}
	
//	받아온 제이슨 타입 좌표변환
	public String parseJsonToLoc(String json)
	{
		JsonParser jsParser = new JsonParser();
		JsonElement jsElement = jsParser.parse(json);
		JsonElement jsAdreess = jsElement.getAsJsonObject().get("places");
		JsonElement jArr;
		try {
			jArr = jsAdreess.getAsJsonArray().get(0);
			
			double x = jArr.getAsJsonObject().get("x").getAsDouble();
			double y = jArr.getAsJsonObject().get("y").getAsDouble();
			String name = jArr.getAsJsonObject().get("name").getAsString();
			System.out.println("y: "+y);
			System.out.println("x: "+x);
			System.out.println(name);
			StringBuffer sb = new StringBuffer();
			sb.append(y);
			sb.append(", ");
			sb.append(x);
			sb.append(":");
			sb.append(name);
			return sb.toString();
			
		} catch (IndexOutOfBoundsException e) {
			System.out.println("결과가 없습니다.");
			return null;
		}
	}
	
//	받아온 제이슨 타입 리스트화
	public int setResultList(String json)
	{
		searchList.clear();
		
		JsonParser jsParser = new JsonParser();
		JsonElement jsElement = jsParser.parse(json);
		JsonElement jsAdreess = jsElement.getAsJsonObject().get("places");
		JsonElement jArr;
		try {
			JsonArray jarr = jsAdreess.getAsJsonArray();
			for(int i=0; i<jarr.size(); i++)
			{
				jArr = jsAdreess.getAsJsonArray().get(i);
				double x = jArr.getAsJsonObject().get("x").getAsDouble();
				double y = jArr.getAsJsonObject().get("y").getAsDouble();
				String name = jArr.getAsJsonObject().get("name").getAsString();
				SearchVO search = new SearchVO();
				search.setX(x);
				search.setY(y);
				search.setName(name);
				searchList.add(search);
			}
			return searchList.size();
		} catch (IndexOutOfBoundsException e) {
			System.out.println("결과가 없습니다.");
			return 0;
		}
	}

}
