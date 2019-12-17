package mini.client.network.Services;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.List;
import java.util.StringTokenizer;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import mini.client.UIControllers.ChatRoomController;
import mini.client.UIControllers.DialogController;
import mini.client.UIControllers.LobbyController;
import mini.client.UIControllers.LoginController;
import mini.client.UIControllers.MainUIController;
import mini.client.UIControllers.PlanController;
import mini.client.VO.ChatVO;
import mini.client.VO.MessageVO;
import mini.client.VO.PlanVO;
import mini.client.VO.RoomVO;
import mini.client.VO.UserVO;
import mini.client.network.staticValues.NetworkProtocolHeads;

public class ListenerService implements IListenerService
{
	SocketChannel socketChannel;
	Thread thread;
	IProtocolDecodeService iProtocolDecodeService;
	DialogController dialogController;
	UserVO userMe = new UserVO();
	
	Stage primaryStage;
	
	LoginController loginController;
	MainUIController mainUIController;
	LobbyController lobbyController;
	ChatRoomController	chatRoomController;
	PlanController planController;
	
	boolean finalBoolean = false;
	String finalString = "";
	UserVO finalUserVO = new UserVO();
	MessageVO finalMessageVO = new MessageVO();
	RoomVO finalRoomVO = new RoomVO();
	PlanVO finalPlanVO = new PlanVO();
	ChatVO finalChatVO = new ChatVO();
	ObservableList<UserVO> finalUserVOList;
	ObservableList<RoomVO> finalRoomList;
	ObservableList<PlanVO> finalPlanList;
	ObservableList<ChatVO> finalChatList;
	ObservableList<String> finalStringList;
	
	public ListenerService(SocketChannel socketChannel, Stage primaryStage, MainUIController mainUIController) 
	{
		this.socketChannel = socketChannel;
		this.primaryStage = primaryStage;
		iProtocolDecodeService = new ProtocolDecodeService();
		finalUserVOList = FXCollections.observableArrayList();
		finalRoomList = FXCollections.observableArrayList();
		finalPlanList = FXCollections.observableArrayList();
		finalChatList = FXCollections.observableArrayList();
		finalStringList = FXCollections.observableArrayList();
		dialogController = new DialogController(primaryStage, mainUIController.getiNetController());
	}
	
	public void setLoginController(LoginController loginController) {
		this.loginController = loginController;
	}
	public void setMainUIController(MainUIController mainUIController) {
		this.mainUIController = mainUIController;
	}
	public void setLobbyController(LobbyController lobbyController) {
		this.lobbyController = lobbyController;
	}
	public void setChatRoomController(ChatRoomController chatRoomController) {
		this.chatRoomController = chatRoomController;
	}
	public void setDialogController(DialogController dialogController) {
		this.dialogController = dialogController;
	}
	public void setPlanController(PlanController planController) {
		this.planController = planController;
	}
	
	public void startListener()
	{
		thread = new Thread(new Runnable() {
			@Override
			public void run() 
			{
				while(true)
				{
					try {
						ByteBuffer byteBuffer = ByteBuffer.allocate(1000000);
						
						int readByteCnt = socketChannel.read(byteBuffer);
						
						if(readByteCnt == -1)
							throw new IOException();
						
						byteBuffer.flip();
						Charset charset = Charset.forName("UTF-8");
						String protocolFromServer = "";
						try {
							protocolFromServer = charset.decode(byteBuffer).toString();
							System.out.println("받은 데이터(ListenerServiceThread): "+protocolFromServer);	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
							
//							문자열로 온 프로토콜 인지 확인
							if(iProtocolDecodeService.isStringProtocol(protocolFromServer))
							{
//								각각의 프로토콜 머리 확인
								String head = protocolFromServer.substring(0, 4);
								int headNum = Integer.parseInt(head);
								int typeOfResult = iProtocolDecodeService.getTypeOfResult(protocolFromServer);
								
								switch(typeOfResult)
								{
//						===============================================================================================================
//						여기부터 참/거짓 관련 리시브헤더
								case NetworkProtocolHeads.BOOLEAN :
									finalBoolean = iProtocolDecodeService.toBoolean(protocolFromServer);
									switch(headNum)
									{
//									아이디 중복확인
									case NetworkProtocolHeads.ID_DUPLICATE_CHECK_RECEIVE_PROTOCOL :
										if(finalBoolean)
											signUpIdCheckButtonOff();
										else
											Platform.runLater(()->dialogController.newAlertDialog("확인 결과", "중복 확인결과", "중복된 아이디 입니다.", AlertType.WARNING));
										break;
//									회원가입 성공여부
									case NetworkProtocolHeads.SIGNUP_RESULT_RECEIVE_PROTOCOL :
										if(finalBoolean)
											Platform.runLater(()->dialogController.newAlertDialog("성공!", "회원가입 성공!", "정상적으로 가입되었습니다.\n로그인을 해주세요", AlertType.INFORMATION));
										else
											Platform.runLater(()->dialogController.newAlertDialog("회원가입 실패", "문제로 인한 회원가입실패", "처음부터 다시 시도 해주십시요.", AlertType.WARNING));
										Platform.runLater(()->loginController.closeSignUpDialog());
										break;
//									친구 삭제 성공여부
									case NetworkProtocolHeads.DELETE_FRIEND_RESULT_RECEIVE_PROTOCOL :
										if(finalBoolean)
											Platform.runLater(()->dialogController.newAlertDialog("삭제", "친구 삭제 완료", "정상적으로 삭제되었습니다.", AlertType.INFORMATION));
										else
										{
											Platform.runLater(()->{
												dialogController.newAlertDialog("삭제 실패", "문제로 인한 삭제실패", "재접속 해주십시요.", AlertType.WARNING);
												System.exit(0);
											});
										}
										break;
//									방 나가기 요청 성공여부
									case NetworkProtocolHeads.GET_OUT_ROOM_RECEIVE_PROTOCOL :
										if(finalBoolean)
											Platform.runLater(()->dialogController.newAlertDialog("나오기", "방 나오기 성공", "정상적으로 방에서 나왔습니다.", AlertType.INFORMATION));
										else
										{
											Platform.runLater(()->{
												dialogController.newAlertDialog("나오기 실패", "문제로 인한 나오기 실패", "재접속 해주십시요.", AlertType.WARNING);
												System.exit(0);
											});
										}
										break;
									}

									break;
									
//						===============================================================================================================
//						여기부터 String 관련 리시브헤더
								case NetworkProtocolHeads.STRING :
									finalString = iProtocolDecodeService.rsToString(protocolFromServer);
									switch(headNum)
									{
//									친구의 로그인 소식 ㅎㅎ
									case NetworkProtocolHeads.FRIEND_LOGIN_RECEIVE_PROTOCOL :
										lobbyController.setLoginStateFromFriendList(finalString);
										if(chatRoomController != null)
										{
											chatRoomController.friendLogin(finalString);
										}
										break;
//									친구의 로그아웃 소식 ㅎㅎㅎㅎㅎ
									case NetworkProtocolHeads.FRIEND_LOGOUT_RECEIVE_PROTOCOL :
										lobbyController.setLogoutStateFromFriendList(finalString);
										if(chatRoomController != null)
										{
											chatRoomController.friendLogout(finalString);
										}
										break;
//									투표 동의 소식이 들려왔을때...
									case NetworkProtocolHeads.AGREE_PLAN_RECEIVE_PROTOCOL :
										planController.agreePlanReceive(finalString);
										break;
									}
									
									break;
									
//						===============================================================================================================
//						여기부터 USER_VO 관련 리시브헤더
								case NetworkProtocolHeads.USER_VO :
									finalUserVO = iProtocolDecodeService.toUserVO(protocolFromServer);
									switch(headNum)
									{
//									로그인 성공여부
									case NetworkProtocolHeads.LOGIN_RESULT_RECEIVE_PROTOCOL :
										if(finalUserVO.getUserState().equals("2"))
										{
											Platform.runLater(()->dialogController.newAlertDialog("회원오류", "비밀번호 오류", "잘못된 비밀번호입니다.\n비밀번호를 다시 체크해 주세요 ", AlertType.WARNING));
										}
										else if(finalUserVO.getUserState().equals("3"))
										{
											Platform.runLater(()->dialogController.newAlertDialog("회원오류", "아이디 오류", "없는 아이디입니다.\n아이디를 다시 체크해 주세요 ", AlertType.WARNING));
										}
										else if(finalUserVO.getUserState().equals("1"))
										{
											userMe = finalUserVO;
											Platform.runLater(()->{
												mainUIController.setUserMe(finalUserVO);
												mainUIController.setLobbyPane();
											});
										}
										break;
//									친구 추가 성공 여부
									case NetworkProtocolHeads.ADD_FRIEND_RESULT_RECEIVE_PROTOCOL :
										if(finalUserVO != null)
										{
											lobbyController.addFriendToFriendList(finalUserVO);
											Platform.runLater(()->{
												dialogController.newAlertDialog("친구 추가 성공", "성공적으로 추가되었습니다", (finalUserVO.getUserId()+"와 친구가 되었습니다"), AlertType.INFORMATION);
												lobbyController.closeAddFriendDialog();
											});
										}
										else
											Platform.runLater(()->{
												dialogController.newAlertDialog("친구추가 실패", "없는 아이디입니다", "친구 아이디를 다시 확인해 주세요", AlertType.WARNING);
												lobbyController.closeAddFriendDialog();
											});
										break;
//									방에 멤버 추가 성공 여부
									case NetworkProtocolHeads.ADD_ROOM_MEMBER_RESULT_RECEIVE_PROTOCOL :
										if(finalUserVO.getUserIpNow().equals("성공"))
										{
											chatRoomController.addRoomMemberToList(finalUserVO);
											Platform.runLater(()->{
												dialogController.newAlertDialog("멤버 추가 성공", "성공적으로 추가되었습니다", (finalUserVO.getUserId()+"님이 추가 되었습니다"), AlertType.INFORMATION);
											});
										}
										else if(finalUserVO.getUserIpNow().equals("없는 아이디"))
										{
											Platform.runLater(()->{
												dialogController.newAlertDialog("멤버 추가 실패", "없는 아이디입니다", "친구 아이디를 다시 확인해 주세요", AlertType.WARNING);
											});
										}
										else if(finalUserVO.getUserIpNow().equals("거절하셨습니다."))
										{
											Platform.runLater(()->{
												dialogController.newAlertDialog("멤버 추가 실패", "거절 하셨습니다", "상대방님이 요청을 거절 하셨습니다.", AlertType.WARNING);
											});
										}
										else if(finalUserVO.getUserIpNow().equals("친구가미접속"))
										{
											Platform.runLater(()->{
												dialogController.newAlertDialog("멤버 추가 실패", "친구가 미접속", "상대방님이 접속중일때 요청해주세요.", AlertType.WARNING);
											});
										}
										break;
										
										
									}
									break;
//						===============================================================================================================
//						여기부터 MESSAGE_VO 관련 리시브헤더
								case NetworkProtocolHeads.MESSAGE_VO :
									finalMessageVO = iProtocolDecodeService.toMessageVO(protocolFromServer);
									switch(headNum)
									{
//									메세지 받는거 처리(쪽지)
									case NetworkProtocolHeads.MESSAGE_RECEIVE_PROTOCOL :
										if(finalMessageVO != null && !userMe.getUserId().equals(finalMessageVO.getFrom()))
										{
											Platform.runLater(()->dialogController.newMessageDialog(finalMessageVO, false));
										}
										
										break;
									}
									break;
//						===============================================================================================================
//						여기부터 ROOM_VO 관련 리시브헤더			
								case NetworkProtocolHeads.ROOM_VO :
									finalRoomVO = iProtocolDecodeService.toRoomVO(protocolFromServer);
									switch(headNum)
									{
//									방 추가 결과 받는 프로토콜
									case NetworkProtocolHeads.ADD_ROOM_RESULT_RECEIVE_PROTOCOL :
										if(finalRoomVO !=null)
											Platform.runLater(()->{
												dialogController.newAlertDialog("방 만들기", "방 만들기 성공", "새로운 여행계획이 추가되었습니다.", AlertType.INFORMATION);
												lobbyController.addRoomVOToRoomList(finalRoomVO);
											});
										else
										{
											Platform.runLater(()->{
												dialogController.newAlertDialog("방 만들기실패", "문제로 인한 방 만들기 실패", "재접속 해주십시요.", AlertType.WARNING);
												System.exit(0);
											});
										}
										break;
//									방초대 요청을 받았을떄
									case NetworkProtocolHeads.ROOM_INVITE_RECEIVE_PROTOCOL :
										if(finalRoomVO != null)
										{
											Platform.runLater(()->{
												dialogController.newRoomInviteDialog(finalRoomVO.getRoomMemberId(), finalRoomVO);
											});
										}
										break;
										
										
									}
									break;
//						===============================================================================================================
//						여기부터 PLAN_VO 관련 리시브헤더		
								case NetworkProtocolHeads.PLAN_VO :
									finalPlanVO = iProtocolDecodeService.toPlanVO(protocolFromServer);
									switch(headNum)
									{
//									새로운 단일 플랜을 받았을떄
									case NetworkProtocolHeads.NEW_PLAN_RECEIVE_PROTOCOL :
										planController.addPlanTofirstPlanList(finalPlanVO);
										break;
//									플랜삭제 요청을 받았을때
									case NetworkProtocolHeads.DELETE_CANDIDATE_PLAN_RECEIVE_PROTOCOL :
										planController.deletePlanFromFirstPlanList(finalPlanVO);
										break;
//									플랜 투표에 올라갔을때
									case NetworkProtocolHeads.ADD_PLAN_TO_VOTE_RECEIVE_PROTOCOL :
										planController.addCandidatePlanToVotePlanList(finalPlanVO.getPlanNum(), finalPlanVO.getPlanTime(), finalPlanVO.getPlanDate());
										break;
									}
									
									
									
									break;
//						===============================================================================================================
//						여기부터 USER_VO_LIST 관련 리시브헤더
								case NetworkProtocolHeads.USER_VO_LIST:
									finalUserVOList = iProtocolDecodeService.toUserList(protocolFromServer);
									switch(headNum)
									{
//									로비의 친구 리스트 받아오기
									case NetworkProtocolHeads.FRIEND_LIST_RECEIVE_PROTOCOL :
										lobbyController.setFriendList(finalUserVOList);
										break;
//									채팅방에 멤버 리스트 받아오기
									case NetworkProtocolHeads.ROOM_MEMBER_LIST_RECEIVE_PROTOCOL :
										chatRoomController.setRoomMemberList(finalUserVOList);
										break;
										
										
									}
									
									break;
//						===============================================================================================================
//						여기부터 ROOM_VO_LIST 관련 리시브헤더
								case NetworkProtocolHeads.ROOM_VO_LIST:
									finalRoomList = iProtocolDecodeService.toRoomList(protocolFromServer);
									lobbyController.setRoomList(finalRoomList);
									break;
//						===============================================================================================================
//						여기부터 PLAN_VO_LIST 관련 리시브헤더
								case NetworkProtocolHeads.PLAN_VO_LIST:
									finalPlanList = iProtocolDecodeService.toPlanList(protocolFromServer);
									switch(headNum)
									{
									case NetworkProtocolHeads.ALL_PLAN_LIST_RECEIVE_PROTOCOL :
										planController.addAllPlanToIndivisualList(finalPlanList);
										break;
									}
									
									
									break;
									
//						===============================================================================================================
//						여기부터 CHAT_VO 관련 리시브헤더	
								case NetworkProtocolHeads.CHAT_VO :
									finalChatVO = iProtocolDecodeService.toChatVO(protocolFromServer);
									switch(headNum)
									{
//									채팅 하나 받아온거 띄우기
									case NetworkProtocolHeads.CHAT_RECEIVE_RECEIVE_PROTOCOL :
										Platform.runLater(()->chatRoomController.addText(finalChatVO));
										break;
										
										
										
										
									}
									break;
									
//						===============================================================================================================
//						여기부터 CHAT_VO 관련 리시브헤더	
								case NetworkProtocolHeads.CHAT_VO_LIST :
									finalChatList = iProtocolDecodeService.toChatList(protocolFromServer);
									switch(headNum)
									{
//									방 들어갈떄 요청한 채팅 들 불러오기
									case NetworkProtocolHeads.CHAT_LIST_RECEIVE_PROTOCOL :
										Platform.runLater(()->chatRoomController.loadChatLog(finalChatList));
										break;
									}
									
									
									
									
									break;
									
									
//						===============================================================================================================
//						여기부터 CHAT_VO 관련 리시브헤더
								case NetworkProtocolHeads.STRING_LIST :
									finalStringList = iProtocolDecodeService.toStringList(protocolFromServer);
									switch(headNum)
									{
									case NetworkProtocolHeads.COMBOBOX_PURPOSE_LIST_RECEIVE_PROTOCOL :
										planController.addComboItemList(finalStringList);
										break;
									}
									
									break;
									
									
								default :
									System.out.println("ListenerServiceThread: 잘못된 프로토콜 머리"); //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
								}
								
								
								
								
							}
						}
						catch(NumberFormatException numE)
						{
//							HEAD가 숫자가 아닐떄
						}
						catch (Exception e) {
						}
						
						
//						여기까지가 버퍼 만들어서 값 받고 터리하는 부분
						
					} 
					catch (IOException e) {
						System.out.println("[ReceiveMessageThread] 서버 통신 불가----------------------------------------");		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
					}
				}
				
				
			}
		});
		thread.setName("ListenerServiceThread");
		thread.setDaemon(true);
		thread.start();
	}
	
	
	public void signUpIdCheckButtonOff()
	{
		Platform.runLater(()->{
			dialogController.newAlertDialog("확인 결과", "중복 확인결과", "사용 가능한 아이디 입니다.", AlertType.CONFIRMATION);
			loginController.idCheckOk();
		});
	}

	
	
	
	
	
	
}
