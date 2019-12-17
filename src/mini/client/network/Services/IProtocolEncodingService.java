package mini.client.network.Services;

import mini.client.VO.ChatVO;
import mini.client.VO.MessageVO;
import mini.client.VO.PlanVO;
import mini.client.VO.RoomVO;
import mini.client.VO.UserVO;

public interface IProtocolEncodingService 
{
//	로그인 관련==============================================================================================================
	public String getProtocolStringOfCheckId(String userId);
	public String getProtocolStringOfSignUp(UserVO user);
	public String getProtocolStringOfLogin(UserVO user);
	public String getProtocolStringOfLogout(UserVO user);
	
//	쪽지 관련===============================================================================================================
	public String getProtocolStringOfSendMessage(MessageVO message);
	
//	친구 관리 관련============================================================================================================
	public String getProtocolStringOfAddFriend(String userId, String friendId);
	public String getProtocolStringOfDeleteFriend(String userId);
	public String getProtocolStringOfRequestFriendList(String userId);
	
//	방 관리 관련=============================================================================================================
	public String getProtocolStringOfAddRoom(String userId, RoomVO room);
	public String getProtocolStringOfGetOutRoom(String userId, RoomVO room);
	public String getProtocolStringOfRequestRoomList(String userId);
	
//	채팅방 관련=============================================================================================================
	public String getProtocolStringOfSendChat(ChatVO chat);
	public String getProtocolStringOfGetRoomMember(int roomNum);
	public String getProtocolStringOfAddRoomMember(String myId, String friendId, RoomVO room);
	public String getProtocolStringOfReplyRoomInvite(String myId, String replyToId, RoomVO room, boolean reply);
	public String getProtocolStringOfGetChatList(int roomNum);
	
//	계획 관련===============================================================================================================
	public String getProtocolStringOfGetAllPlanList(int roomNum);
	
	public String getProtocolStringOfGetComboPurposeList();
	public String getProtocolStringOfGetPlanInfo(PlanVO plan);
	public String getProtocolStringOfAddCandidatePlan(PlanVO plan);
	public String getProtocolStringOfDeleteCandidatePlan(PlanVO plan);
	
	public String getProtocolStringOfAddPlanToVote(PlanVO plan, int roomNum, String dateNow);
	public String getProtocolStringOfAgreePlan(PlanVO plan, String userId, int roomMemberNum);
}