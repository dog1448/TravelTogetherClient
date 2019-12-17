package mini.client.network.Services;

import mini.client.VO.ChatVO;
import mini.client.VO.MessageVO;
import mini.client.VO.PlanVO;
import mini.client.VO.RoomVO;
import mini.client.VO.UserVO;
import mini.client.network.staticValues.NetworkProtocolHeads;

public class ProtocolEncodingService implements IProtocolEncodingService
{	
//	1001:아이디
	@Override
	public String getProtocolStringOfCheckId(String userId) {
		StringBuffer sb = new StringBuffer();
		sb.append(NetworkProtocolHeads.CHECK_ID_PROTOCOL);
		sb.append(":");
		sb.append(userId);
		return sb.toString();
	}
	
// 1002:'아이디':'비밀번호':'이름':'성별':'폰번호'
	@Override
	public String getProtocolStringOfSignUp(UserVO user) 
	{
		StringBuffer sb = new StringBuffer();
		sb.append(NetworkProtocolHeads.SIGNUP_PROTOCOL);
		sb.append(":");
		sb.append(user.getUserId());
		sb.append(":");
		sb.append(user.getUserPassword());
		sb.append(":");
		sb.append(user.getUserName());
		sb.append(":");
		sb.append(user.getUserGender());
		sb.append(":");
		if(user.getUserPhone()!=null)
		{
			sb.append(user.getUserPhone());
		}
		else
		{
			sb.append("nope");
		}
		return sb.toString();
	}

// 1003:'아이디':'비밀번호'
	@Override
	public String getProtocolStringOfLogin(UserVO user) 
	{
		StringBuffer sb = new StringBuffer();
		sb.append(NetworkProtocolHeads.LOGIN_PROTOCOL);
		sb.append(":");
		sb.append(user.getUserId());
		sb.append(":");
		sb.append(user.getUserPassword());
		return sb.toString();
	}

	@Override
	public String getProtocolStringOfLogout(UserVO user) {
		// TODO Auto-generated method stub
		return null;
	}

// 2001:'아이디from':'아이디to':'메세지내용'
	@Override
	public String getProtocolStringOfSendMessage(MessageVO message) 
	{
		StringBuffer sb = new StringBuffer();
		sb.append(NetworkProtocolHeads.SEND_MESSAGE_PROTOCOL);
		sb.append(":");
		sb.append(message.getFrom());
		sb.append(":");
		sb.append(message.getTo());
		sb.append(":");
		if(message.getMessage() ==null || message.getMessage().equals(""))
			sb.append("nope");
		else
			sb.append(message.getMessage());
		return sb.toString();
	}

// 3002:'내아이디':'친구아이디'
	@Override
	public String getProtocolStringOfAddFriend(String userId, String friendId) 
	{
		StringBuffer sb = new StringBuffer();
		sb.append(NetworkProtocolHeads.ADD_FRIEND_PROTOCOL);
		sb.append(":");
		sb.append(userId);
		sb.append(":");
		sb.append(friendId);
		return sb.toString();
	}
	
// 3003:'친구아이디'
	@Override
	public String getProtocolStringOfDeleteFriend(String userId) 
	{
		StringBuffer sb = new StringBuffer();
		sb.append(NetworkProtocolHeads.DELETE_FRIEND_PROTOCOL);
		sb.append(":");
		sb.append(userId);
		return sb.toString();
	}
	
// 3001:'내아이디'
	public String getProtocolStringOfRequestFriendList(String userId)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(NetworkProtocolHeads.GET_FRIEND_LIST_PROTOCOL);
		sb.append(":");
		sb.append(userId);
		return sb.toString();
	}

// 4002:'내아이디':'ROOM_NAME':'ROOM_PLACE':'ROOM_START_DATE':'ROOM_END_DATE'
	@Override
	public String getProtocolStringOfAddRoom(String userId, RoomVO room) 
	{
		StringBuffer sb = new StringBuffer();
		sb.append(NetworkProtocolHeads.ADD_ROOM_PROTOCOL);
		sb.append(":");
		sb.append(userId);
		sb.append(":");
		sb.append(room.getRoomName());
		sb.append(":");
		sb.append(room.getRoomPlace());
		sb.append(":");
		sb.append(room.getRoomStartDate());
		sb.append(":");
		sb.append(room.getRoomEndDate());
		return sb.toString();
	}

// 4003:'내아이디':'방번호'
	@Override
	public String getProtocolStringOfGetOutRoom(String userId, RoomVO room) 
	{
		StringBuffer sb = new StringBuffer();
		sb.append(NetworkProtocolHeads.GET_OUT_ROOM_PROTOCOL);
		sb.append(":");
		sb.append(userId);
		sb.append(":");
		sb.append(room.getRoomNum());
		return sb.toString();
	}
	
// 4001:'내아이디'
	@Override
	public String getProtocolStringOfRequestRoomList(String userId) 
	{
		StringBuffer sb = new StringBuffer();
		sb.append(NetworkProtocolHeads.GET_ROOM_LIST_PROTOCOL);
		sb.append(":");
		sb.append(userId);
		return sb.toString();
	}

// 5002:'내아이디':'방번호':'메세지' -> 5052
	@Override
	public String getProtocolStringOfSendChat(ChatVO chat) 
	{
		StringBuffer sb = new StringBuffer();
		sb.append(NetworkProtocolHeads.SEND_CHAT_PROTOCOL);
		sb.append(":");
		sb.append(chat.getChatMemberId());
		sb.append(":");
		sb.append(chat.getChatRoomNum());
		sb.append(":");
		sb.append(chat.getChatMessage());
		return sb.toString();
	}
	
	@Override
	public String getProtocolStringOfGetRoomMember(int roomNum) 
	{
		StringBuffer sb = new StringBuffer();
		sb.append(NetworkProtocolHeads.GET_ROOM_MEMBER_LIST_PROTOCOL);
		sb.append(":");
		sb.append(roomNum);
		return sb.toString();
	}
	
// 5003:'내아이디':'방번호':'여행지':'친구아이디' -> 4052
	@Override
	public String getProtocolStringOfAddRoomMember(String myId, String friendId, RoomVO room) 
	{
		StringBuffer sb = new StringBuffer();
		sb.append(NetworkProtocolHeads.ADD_ROOM_MEMBER_PROTOCOL);
		sb.append(":");
		sb.append(myId);
		sb.append(":");
		sb.append(room.getRoomNum());
		sb.append(":");
		sb.append(room.getRoomPlace());
		sb.append(":");
		sb.append(friendId);
		return sb.toString();
	}
	
// 5004:'T'(수락여부):'초대받은아이디':'초대한아이디':'room_num'
	@Override
	public String getProtocolStringOfReplyRoomInvite(String myId, String replyToId, RoomVO room, boolean reply) 
	{
		StringBuffer sb = new StringBuffer();
		sb.append(NetworkProtocolHeads.REPLY_ROOM_INVITE_PROTOCOL);
		sb.append(":");
		if(reply)
			sb.append("T");
		else
			sb.append("F");
		sb.append(":");
		sb.append(myId);
		sb.append(":");
		sb.append(replyToId);
		sb.append(":");
		sb.append(room.getRoomNum());
		return sb.toString();
	}
	
// 5005:'채팅방번호'
	@Override
	public String getProtocolStringOfGetChatList(int roomNum) 
	{
		return NetworkProtocolHeads.GET_CHAT_LIST_PROTOCOL+":"+roomNum;
	}

//	6005:
	@Override
	public String getProtocolStringOfGetComboPurposeList() 
	{
		return "6005:";
	}

//* 6002:'방번호'
	@Override
	public String getProtocolStringOfGetAllPlanList(int roomNum) 
	{
		return "6002:"+roomNum;
	}
	
	@Override
	public String getProtocolStringOfGetPlanInfo(PlanVO plan) {
		// TODO Auto-generated method stub
		return null;
	}
	
// 6003:'작성자':'방번호':'제목':'구분':'예상지출':'좌표':'기타':'링크':'날짜':'이미지명' -> 6051
	@Override
	public String getProtocolStringOfAddCandidatePlan(PlanVO plan) 
	{
		StringBuffer sb = new StringBuffer();
		sb.append(NetworkProtocolHeads.ADD_CANDIDATE_PLAN_PROTOCOL);
		sb.append(":");
		sb.append(plan.getPlanUserId());
		sb.append(":");
		sb.append(plan.getPlanRoomNum());
		sb.append(":");
		sb.append(plan.getPlanName());
		sb.append(":");
		sb.append(plan.getPlanPurposeName());
		sb.append(":");
		sb.append(plan.getPlanMoney());
		sb.append(":");
		sb.append(plan.getPlanLoc());
		sb.append(":");
		sb.append(plan.getPlanOther());
		sb.append(":");
		String link = plan.getPlanLink();
		link.replace(":", "~");
		sb.append(link);
		sb.append(":");
		sb.append(plan.getPlanDate());
		sb.append(":");
		sb.append(plan.getPlanImgLoc());
		return sb.toString();
	}
	
// 6004:'플렌번호':'방번호'
	@Override
	public String getProtocolStringOfDeleteCandidatePlan(PlanVO plan) 
	{
		return "6004:"+plan.getPlanNum()+":"+plan.getPlanRoomNum();
	}

// 6102:'플렌번호':'플랜시간':'방번호':'플랜날짜'
	@Override
	public String getProtocolStringOfAddPlanToVote(PlanVO plan, int roomNum, String dateNow) 
	{
		StringBuffer sb = new StringBuffer();
		sb.append(NetworkProtocolHeads.ADD_PLAN_TO_VOTE_PROTOCOL);
		sb.append(":");
		sb.append(plan.getPlanNum());
		sb.append(":");
		sb.append(plan.getPlanTime());
		sb.append(":");
		sb.append(roomNum);
		sb.append(":");
		sb.append(dateNow);
		return sb.toString();
	}

// 6103:'방번호':'플랜번호':'동의유저ID':'방인원수' -> 6152
	@Override
	public String getProtocolStringOfAgreePlan(PlanVO plan, String userId, int roomMemberNum) 
	{
		StringBuffer sb = new StringBuffer();
		sb.append(NetworkProtocolHeads.AGREE_PLAN_PROTOCOL);
		sb.append(":");
		sb.append(plan.getPlanRoomNum());
		sb.append(":");
		sb.append(plan.getPlanNum());
		sb.append(":");
		sb.append(userId);
		sb.append(":");
		sb.append(roomMemberNum);
		return sb.toString();
	}



	

	

	

	

}
