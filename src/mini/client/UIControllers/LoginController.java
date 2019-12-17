package mini.client.UIControllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import mini.client.VO.UserVO;
import mini.client.network.Services.IListenerService;
import mini.client.network.controllers.INetController;
import mini.client.network.staticValues.FormatStaticValues;

public class LoginController implements Initializable
{
//	===============================================================================================================
//	UI관련 멤버
	private Stage primaryStage;
	private Stage signUpStage;
	private AnchorPane signUpPane;
	private DialogController dialogController;
	
	
//	===============================================================================================================
//	----------------------------------------------------------------------------------------------
//	Login Component관련 멤버
	@FXML private TextField tfId;
	@FXML private PasswordField tfPassword;
	@FXML private Button btnLogin;
	@FXML private Button btnSignUp;
	
//	----------------------------------------------------------------------------------------------
//	SignUp Dialog Component관련 멤버
	private TextField tfSignUpId;
	private TextField tfSignUpPassword;
	private TextField tfSignUpConfirm;
	private TextField tfSignUpName;
	private TextField tfSignUpPhone2;
	private TextField tfSignUpPhone3;
	private ComboBox<String> comboSignUpGender;
	private ComboBox<String> comboSignUpPhone1;
	private Button btnSignUpSubmit;
	private Button btnSignUpCancel;
	private Button btnCheckId;
	

	
	
//	===============================================================================================================
//	컨트롤러
	private MainUIController mainController;
	private INetController iNetController;
	private IListenerService iListenerService;
	
//	===============================================================================================================
//	
	public void setiListenerService(IListenerService iListenerService) {
		this.iListenerService = iListenerService;
	}

	
//	===============================================================================================================
//	메인에서 셋팅해주는것
	public void setiNetController(INetController iNetController) {
		this.iNetController = iNetController;
	}
	
//	===============================================================================================================
//	멤버 초기화 메서드 메인에서 뭔가 받아오기
	public void setMainController(MainUIController mainController)
	{
		this.mainController = mainController;
		this.primaryStage = mainController.getPrimaryStage();
		dialogController = mainController.getDialogController();
		
	}
	
	
	
//	===============================================================================================================
//	initialize 메서드	UI 시작 초기화 처리
	@Override
	public void initialize(URL location, ResourceBundle resources) 
	{
	}
	
	
//	===============================================================================================================
//	Component 들 제어용 메서드
	
//	----------------------------------------------------------------------------------------------
//	로그인 버튼 핸들러
	public void handleLoginButton()
	{
		
//		***************************************************************************************************************************
//		로그인 인증하는 메서드 구현해야함
//		***************************************************************************************************************************
		String id = tfId.getText();
		String pw = tfPassword.getText();
		
		if(id.length()==0)
		{
			DialogController tempDia = new DialogController(signUpStage, iNetController);
			tempDia.newAlertDialog("오류", "아이디 필드가 비었습니다.", "아이디를 입력해주세요.", AlertType.WARNING);
		}
		else if(pw.length()==0)
		{
			DialogController tempDia = new DialogController(signUpStage, iNetController);
			tempDia.newAlertDialog("오류", "비밀번호 필드가 비었습니다.", "비밀번호를 입력해주세요.", AlertType.WARNING);
		}
		else
		{
			UserVO user = new UserVO();
			user.setUserId(id);
			user.setUserPassword(pw);
			iNetController.login(user);
		}
	}
	
//	----------------------------------------------------------------------------------------------
//	회원가입 버튼 핸들러 / 회원가입 다이얼로그
	public void handleSignUpButton()
	{
		try {
			signUpStage = new Stage(StageStyle.UTILITY);
			signUpStage.initModality(Modality.WINDOW_MODAL);
			signUpStage.initOwner(primaryStage);
			signUpStage.setTitle("회원가입");
			
			signUpPane = (AnchorPane)FXMLLoader.load(getClass().getResource("../../../fxml/SignUpDialog.fxml"));
			tfSignUpId = (TextField) signUpPane.lookup("#tfSignUpId");
			tfSignUpPassword = (TextField)signUpPane.lookup("#tfSignUpPassword");
			tfSignUpConfirm = (TextField)signUpPane.lookup("#tfSignUpConfirm");
			tfSignUpName = (TextField)signUpPane.lookup("#tfSignUpName");
			comboSignUpGender = (ComboBox<String>)signUpPane.lookup("#comboSignUpGender");
			comboSignUpPhone1 = (ComboBox<String>)signUpPane.lookup("#comboSignUpPhone1");
			tfSignUpPhone2 = (TextField)signUpPane.lookup("#tfSignUpPhone2");
			tfSignUpPhone3 = (TextField)signUpPane.lookup("#tfSignUpPhone3");
			btnSignUpSubmit = (Button)signUpPane.lookup("#btnSignUpSubmit");
			btnSignUpCancel = (Button)signUpPane.lookup("#btnSignUpCancel");
			btnCheckId = (Button)signUpPane.lookup("#btnCheckId");
			
			tfSignUpId.setPromptText(FormatStaticValues.ID_MIN_LENGTH+" ~ "+FormatStaticValues.ID_MAX_LENGTH+"자");
			tfSignUpPassword.setPromptText(FormatStaticValues.PW_MIN_LENGTH+" ~ "+FormatStaticValues.PW_MAX_LENGTH+"자");
			
			Scene Scene = new Scene(signUpPane);
			signUpStage.setScene(Scene);
			signUpStage.setResizable(false);
			signUpStage.show();
			
			iListenerService.setLoginController(this);
		}
		catch (IOException e) {
			System.out.println("error: handleSignUpButton() / IOException");	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			e.printStackTrace();
		}
		
//		***************************************************************************************************************************
//		회원가입 다이얼로그 관련 시작
//		***************************************************************************************************************************


		
//		회원가입 중복확인 버튼
		btnCheckId.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				String tempstr = tfSignUpId.getText();
				if(tempstr.length()<FormatStaticValues.ID_MIN_LENGTH || tempstr.length()>FormatStaticValues.ID_MAX_LENGTH)
				{
					DialogController tempDia = new DialogController(signUpStage, iNetController);
					tempDia.idFalseFormatWarningDialog();
				}
				else
				{
					iNetController.checkIdDuplicate(tempstr);
				}
			}
		});

//		회원 아이디 조건 성립에 따른 배경색설정, 바뀌면 버튼 활성화
		tfSignUpId.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) 
			{
				if(btnCheckId.isDisabled())
				{
					btnCheckId.setText("중복확인");
					btnCheckId.setDisable(false);
				}
				if(newValue.length()==0)
				{
					tfSignUpId.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, #000000, 10, 0, 3, 3)");
					tfSignUpId.setPromptText(FormatStaticValues.ID_MIN_LENGTH+" ~ "+FormatStaticValues.ID_MAX_LENGTH+"자로 해주세요");
				}
				else if(newValue.length()<FormatStaticValues.ID_MIN_LENGTH || newValue.length()>FormatStaticValues.ID_MAX_LENGTH)
				{
					tfSignUpId.setStyle("-fx-background-color: linear-gradient(to bottom, white 80%, red);-fx-effect: dropshadow(three-pass-box, #000000, 10, 0, 3, 3)");
				}
				else
				{
					tfSignUpId.setStyle("-fx-background-color: linear-gradient(to bottom, white 80%, green); -fx-effect: dropshadow(three-pass-box, #000000, 10, 0, 3, 3)");
				}
			}
		});
		
//		회원가입 비밀번호 길이 체크
		tfSignUpPassword.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) 
			{
				if(newValue.length()==0)
				{
					tfSignUpPassword.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, #000000, 10, 0, 3, 3)");
					tfSignUpPassword.setPromptText(FormatStaticValues.PW_MIN_LENGTH+" ~ "+FormatStaticValues.PW_MAX_LENGTH+"자로 해주세요");
				}
				else if(newValue.length()<FormatStaticValues.PW_MIN_LENGTH || newValue.length()>FormatStaticValues.PW_MAX_LENGTH)
				{
					tfSignUpPassword.setStyle("-fx-background-color: linear-gradient(to bottom, white 80%, red);-fx-effect: dropshadow(three-pass-box, #000000, 10, 0, 3, 3)");
				}
				else
				{
					tfSignUpPassword.setStyle("-fx-background-color: linear-gradient(to bottom, white 80%, green); -fx-effect: dropshadow(three-pass-box, #000000, 10, 0, 3, 3)");
				}
			}
		});
		
//		회원가입 비밀번호 체크
		tfSignUpConfirm.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if(newValue.equals(tfSignUpPassword.getText()))
				{
					tfSignUpConfirm.setStyle("-fx-background-color: linear-gradient(to bottom, white 80%, green); -fx-effect: dropshadow(three-pass-box, #000000, 10, 0, 3, 3)");
				}
				else
				{
					tfSignUpConfirm.setStyle("-fx-background-color: linear-gradient(to bottom, white 80%, red);-fx-effect: dropshadow(three-pass-box, #000000, 10, 0, 3, 3)");
				}
			}
		});
		
		
//		회원가입 요청 버튼
		btnSignUpSubmit.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				boolean isChecked = checkFormIsCorrect();
				if(isChecked)
				{
					UserVO user = new UserVO();
					user.setUserId(tfSignUpId.getText());
					user.setUserPassword(tfSignUpPassword.getText());
					user.setUserName(tfSignUpName.getText());
					
					if(comboSignUpGender.getSelectionModel().getSelectedItem().equals("남자"))
						user.setUserGender("M");
					else
						user.setUserGender("W");
					if(tfSignUpPhone2.getText().length()==4 && tfSignUpPhone3.getText().length()==4)
					{
						user.setUserPhone(comboSignUpPhone1.getSelectionModel().getSelectedItem()+"-"+tfSignUpPhone2.getText()+"-"+tfSignUpPhone3.getText());
					}					
					iNetController.signUp(user);					
				}	
			}
		});
		
		
//		회원가입 다이얼로그 취소버튼
		btnSignUpCancel.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) 
			{
				signUpStage.close();
			}
		});
		
		
	}
	
//	회원중복확인 버튼 off
	public void idCheckOk()
	{
		btnCheckId.setText("사용가능");
		btnCheckId.setDisable(true);
		tfSignUpPassword.requestFocus();
	}
	
//	회원가입 창 닫기
	public void closeSignUpDialog()
	{
		signUpStage.close();
	}
	
	
//	회원가입 요청 보내기전 필드 체크
	public boolean checkFormIsCorrect()
	{
		DialogController tempDia = new DialogController(signUpStage, iNetController);
		if(btnCheckId.getText().equals("중복확인"))
		{
			tempDia.newAlertDialog("필드 입력오류", "아이디 중복확인 필요", "아이디 중복확인을 진행해 주세요.",AlertType.WARNING);
			return false;
		}
		if(!tfSignUpPassword.getText().equals(tfSignUpConfirm.getText()))
		{
			tempDia.newAlertDialog("필드 입력오류", "비밀번호 일치 필요", "비밀번호와 비밀번호 확인이 일치하지 않습니다.",AlertType.WARNING);
			return false;
		}	
		if(tfSignUpName.getText().equals(""))
		{
			tempDia.newAlertDialog("필드 입력오류", "이름 입력 필요", "이름을 입력해 주세요.",AlertType.WARNING);
			return false;
		}
		if(comboSignUpGender.getSelectionModel().getSelectedItem()==null)
		{
			tempDia.newAlertDialog("필드 입력오류", "성별 입력 필요", "성별을 선택해 주세요.",AlertType.WARNING);
			return false;
		}
		String p2 = tfSignUpPhone2.getText();
		String p3 = tfSignUpPhone3.getText();
		if(comboSignUpPhone1.getSelectionModel().getSelectedItem()!=null || p2.length() != 0 || p3.length() != 0)
		{
			if(comboSignUpPhone1.getSelectionModel().getSelectedItem()!=null && p2.length() == 4 && p3.length() == 4)
			{
				for(int i=0; i<4;i++)
				{
//					폰번호가 모두 숫자인지 확인
					if(p2.charAt(i)<'0' || p2.charAt(i)>'9' || p3.charAt(i)<'0' || p3.charAt(i)>'9')
					{
						tempDia.newAlertDialog("필드 입력오류", "핸드폰 번호 필요", "올바른 핸드폰 번호를 입력해 주세요.",AlertType.WARNING);
						return false;
					}
				}
				return true;
			}
			tempDia.newAlertDialog("필드 입력오류", "핸드폰 번호 필요", "올바른 핸드폰 번호를 입력해 주세요.",AlertType.WARNING);
			return false;
		}	
		return true;
	}
	
	
}
