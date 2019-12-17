package mini.client.network.controllers;

import java.io.File;
import java.nio.channels.SocketChannel;

import javafx.collections.ObservableList;
import mini.client.VO.ChatVO;
import mini.client.VO.MessageVO;
import mini.client.VO.PlanVO;
import mini.client.VO.RoomVO;
import mini.client.VO.UserVO;
import mini.client.network.Services.INetService;
import mini.client.network.Services.IProtocolEncodingService;
import mini.client.network.Services.NetService;
import mini.client.network.Services.ProtocolEncodingService;

public class NetController implements INetController
{
	private INetService iNetService;
	private IProtocolEncodingService iProtocolEncodingService;
	
	
	@Override
	public boolean isConnected() {
		return iNetService.getConnectState();
	}
	@Override
	public SocketChannel getSocketChannel()
	{
		return iNetService.getSocketChannel();
	}
	
	@Override
	public void startServerConnection() 
	{
		iNetService = new NetService();
		iProtocolEncodingService = new ProtocolEncodingService();
	}

	@Override
	public void stopServerConnection() {
		iNetService.disconnectNetService();
	}

	@Override
	public void checkIdDuplicate(String id) 
	{
		String protocol = iProtocolEncodingService.getProtocolStringOfCheckId(id);
		iNetService.sendStringToServer(protocol);
	}

	@Override
	public void signUp(UserVO user) {
		String protocol = iProtocolEncodingService.getProtocolStringOfSignUp(user);
		iNetService.sendStringToServer(protocol);
	}
	
	@Override
	public void login(UserVO user) 
	{
		String protocol = iProtocolEncodingService.getProtocolStringOfLogin(user);
		iNetService.sendStringToServer(protocol);
	}

	@Override
	public void logout(UserVO user) {
		// TODO Auto-generated method stub
	}

	@Override
	public void sendMessage(MessageVO message) 
	{
		String protocol = iProtocolEncodingService.getProtocolStringOfSendMessage(message);
		iNetService.sendStringToServer(protocol);
	}

	@Override
	public void getFriendList(String userId) {
		String protocol = iProtocolEncodingService.getProtocolStringOfRequestFriendList(userId);
		iNetService.sendStringToServer(protocol);
	}

	@Override
	public void addFirend(UserVO user, String friendId) 
	{
		String protocol = iProtocolEncodingService.getProtocolStringOfAddFriend(user.getUserId(), friendId);
		iNetService.sendStringToServer(protocol);
	}

	@Override
	public void deleteFriend(UserVO user) 
	{
		String protocol = iProtocolEncodingService.getProtocolStringOfDeleteFriend(user.getUserId());
		iNetService.sendStringToServer(protocol);
	}

	@Override
	public void getRoomList(String myId) {
		String protocol = iProtocolEncodingService.getProtocolStringOfRequestRoomList(myId);
		iNetService.sendStringToServer(protocol);
		
	}

	@Override
	public void addRoom(String myId, RoomVO room) 
	{
		String protocol = iProtocolEncodingService.getProtocolStringOfAddRoom(myId, room);
		iNetService.sendStringToServer(protocol);
	}

	@Override
	public void getOutRoom(String myId, RoomVO room) {
		String protocol = iProtocolEncodingService.getProtocolStringOfGetOutRoom(myId, room);
		iNetService.sendStringToServer(protocol);
	}

	@Override
	public void getRoomMemberList(int roomNum) {
		String protocol = iProtocolEncodingService.getProtocolStringOfGetRoomMember(roomNum);
		iNetService.sendStringToServer(protocol);
	}

	@Override
	public void sendChat(ChatVO chat) 
	{
		String protocol = iProtocolEncodingService.getProtocolStringOfSendChat(chat);
		iNetService.sendStringToServer(protocol);
	}

	@Override
	public void addRoomMember(String myId, String friendId, RoomVO room) 
	{
		String protocol = iProtocolEncodingService.getProtocolStringOfAddRoomMember(myId, friendId, room);
		iNetService.sendStringToServer(protocol);
	}
	
	@Override
	public void replyRoomInvite(String myId, String reply_id, RoomVO room, boolean reply) 
	{
		String protocol = iProtocolEncodingService.getProtocolStringOfReplyRoomInvite(myId, reply_id, room, reply);
		iNetService.sendStringToServer(protocol);
	}
	
	@Override
	public void getChatList(int roomNum) 
	{		
		String protocol = iProtocolEncodingService.getProtocolStringOfGetChatList(roomNum);
		iNetService.sendStringToServer(protocol);
	}
	
	@Override
	public void getComboPurposeList() 
	{
		String protocol = iProtocolEncodingService.getProtocolStringOfGetComboPurposeList();
		iNetService.sendStringToServer(protocol);
	}
	
	@Override
	public void getAllPlanList(int roomNum) 
	{
		String protocol = iProtocolEncodingService.getProtocolStringOfGetAllPlanList(roomNum);
		iNetService.sendStringToServer(protocol);
	}

	@Override
	public void addCandidatePlan(PlanVO plan) 
	{
		String protocol = iProtocolEncodingService.getProtocolStringOfAddCandidatePlan(plan);
		iNetService.sendStringToServer(protocol);
	}

	@Override
	public void deleteCandidatePlan(PlanVO plan) 
	{
		String protocol = iProtocolEncodingService.getProtocolStringOfDeleteCandidatePlan(plan);
		iNetService.sendStringToServer(protocol);
	}

	@Override
	public void addPlanToVote(PlanVO plan, int roomNum, String dateNow) 
	{
		String protocol = iProtocolEncodingService.getProtocolStringOfAddPlanToVote(plan, roomNum, dateNow);
		iNetService.sendStringToServer(protocol);
	}

	@Override
	public void agreePlan(PlanVO plan, String userId, int roomMemberNum) 
	{
		String protocol = iProtocolEncodingService.getProtocolStringOfAgreePlan(plan, userId, roomMemberNum);
		iNetService.sendStringToServer(protocol);
	}
	
	
	
	@Override
	public void sendImageToServer(File imageFile) 
	{	
		iNetService.sendFileToServer(imageFile);
	}
	
	@Override
	public void getImgFromServer(int PlanNum) 
	{
		//* 6001:'플랜번호'
		iNetService.sendStringToServer("6001:"+PlanNum);
	}


	
	




}
