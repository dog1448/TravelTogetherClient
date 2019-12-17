package mini.client.network.controllers;

import java.io.File;
import java.nio.channels.SocketChannel;

import mini.client.VO.ChatVO;
import mini.client.VO.MessageVO;
import mini.client.VO.PlanVO;
import mini.client.VO.RoomVO;
import mini.client.VO.UserVO;

public interface INetController 
{
//	===============================================================================================================
//	server와 연결 / 끊기 관련
	public void startServerConnection();
	public void stopServerConnection();
	public boolean isConnected();
	public SocketChannel getSocketChannel();
	
//	===============================================================================================================
//	회원가입 / 로그인 화면 관련	
	public void checkIdDuplicate(String id);
	public void signUp(UserVO user);
	public void login(UserVO user);
	public void logout(UserVO user);
	
//	===============================================================================================================
//	쪽지 관련
	public void sendMessage(MessageVO message);
	
//	===============================================================================================================
//	친구 관리 관련
	public void getFriendList(String userId);
	public void addFirend(UserVO user, String frinedId);
	public void deleteFriend(UserVO user);
	
//	===============================================================================================================
//	방 관리 관련
	public void getRoomList(String myId);
	public void addRoom(String myId, RoomVO room);
	public void getOutRoom(String myId, RoomVO room);
	
//	===============================================================================================================
//	채팅방 관련
	public void getRoomMemberList(int roomNum);
	public void sendChat(ChatVO chat);
	public void addRoomMember(String myId, String friendId, RoomVO room);
	public void replyRoomInvite(String myId, String reply_id, RoomVO room, boolean reply);
	public void getChatList(int roomNum);
	
//	===============================================================================================================
//	계획 관련
	public void getComboPurposeList();
	public void getAllPlanList(int roomNum);
	public void addCandidatePlan(PlanVO plan);
	public void deleteCandidatePlan(PlanVO plan);
	public void addPlanToVote(PlanVO plan, int roomNum, String dateNow);
	public void agreePlan(PlanVO plan, String userId, int roomMemberNum);
	public void getImgFromServer(int PlanNum);
	
//	===============================================================================================================
//	서버로 이미지 파일 보내기
	public void sendImageToServer(File imageFile);
	
	
}