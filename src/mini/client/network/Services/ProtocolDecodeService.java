package mini.client.network.Services;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import mini.client.VO.ChatVO;
import mini.client.VO.MessageVO;
import mini.client.VO.PlanVO;
import mini.client.VO.RoomVO;
import mini.client.VO.UserVO;
import mini.client.network.staticValues.NetworkProtocolHeads;

public class ProtocolDecodeService implements IProtocolDecodeService
{
	@Override
	public int getTypeOfResult(String protocol) 
	{
		int protocolNum = Integer.parseInt(protocol.substring(0,4));
		
		switch(protocolNum)
		{
		case NetworkProtocolHeads.ID_DUPLICATE_CHECK_RECEIVE_PROTOCOL :
		case NetworkProtocolHeads.SIGNUP_RESULT_RECEIVE_PROTOCOL :
		case NetworkProtocolHeads.DELETE_FRIEND_RESULT_RECEIVE_PROTOCOL :
		case NetworkProtocolHeads.GET_OUT_ROOM_RECEIVE_PROTOCOL :
			return NetworkProtocolHeads.BOOLEAN;
			
		case NetworkProtocolHeads.FRIEND_LOGIN_RECEIVE_PROTOCOL :
		case NetworkProtocolHeads.FRIEND_LOGOUT_RECEIVE_PROTOCOL :
		case NetworkProtocolHeads.AGREE_PLAN_RECEIVE_PROTOCOL :
			return NetworkProtocolHeads.STRING;
			
		case NetworkProtocolHeads.MESSAGE_RECEIVE_PROTOCOL :
			return NetworkProtocolHeads.MESSAGE_VO;
		
		case NetworkProtocolHeads.ROOM_INVITE_RECEIVE_PROTOCOL :
		case NetworkProtocolHeads.ADD_ROOM_RESULT_RECEIVE_PROTOCOL :
			return NetworkProtocolHeads.ROOM_VO;
			
		case NetworkProtocolHeads.NEW_PLAN_RECEIVE_PROTOCOL :
		case NetworkProtocolHeads.DELETE_CANDIDATE_PLAN_RECEIVE_PROTOCOL :
		case NetworkProtocolHeads.ADD_PLAN_TO_VOTE_RECEIVE_PROTOCOL :
			return NetworkProtocolHeads.PLAN_VO;
			
		case NetworkProtocolHeads.LOGIN_RESULT_RECEIVE_PROTOCOL :
		case NetworkProtocolHeads.ADD_FRIEND_RESULT_RECEIVE_PROTOCOL :
		case NetworkProtocolHeads.ADD_ROOM_MEMBER_RESULT_RECEIVE_PROTOCOL :
			return NetworkProtocolHeads.USER_VO;
			
		case NetworkProtocolHeads.FRIEND_LIST_RECEIVE_PROTOCOL :
		case NetworkProtocolHeads.ROOM_MEMBER_LIST_RECEIVE_PROTOCOL : 
			return NetworkProtocolHeads.USER_VO_LIST;
			
		case NetworkProtocolHeads.ROOM_LIST_RECEIVE_PROTOCOL :
			return NetworkProtocolHeads.ROOM_VO_LIST;
			
		case NetworkProtocolHeads.CHAT_RECEIVE_RECEIVE_PROTOCOL :
			return NetworkProtocolHeads.CHAT_VO;
			
		case NetworkProtocolHeads.CHAT_LIST_RECEIVE_PROTOCOL :
			return NetworkProtocolHeads.CHAT_VO_LIST;
			
		case NetworkProtocolHeads.COMBOBOX_PURPOSE_LIST_RECEIVE_PROTOCOL :
			return NetworkProtocolHeads.STRING_LIST;
			
		case NetworkProtocolHeads.ALL_PLAN_LIST_RECEIVE_PROTOCOL :
			return NetworkProtocolHeads.PLAN_VO_LIST;
			
			
		default:
			return -1;
		}
	}
	
//	=============================================================================================================================
//	String프로토콜이 맞는지 확인
	@Override
	public boolean isStringProtocol(String protocol) 
	{
		try {
			int protocolNum = Integer.parseInt(protocol.substring(0,4));
			
			if(protocolNum < 7000)
			{
				return true;
			}
		} 
		catch (NumberFormatException e) {return false;}
		return false;
	}
	
	
//	=============================================================================================================================
//	boolean으로
	@Override
	public boolean toBoolean(String protocol) 
	{
		if(protocol.substring(5, 9).equalsIgnoreCase("true"))
			return true;
		return false;
	}
	
	
//	=============================================================================================================================
//	String으로
	@Override
	public String rsToString(String protocol) 
	{
		return protocol.substring(5).trim();
	}
	
	@Override
	public MessageVO toMessageVO(String protocolFromServer)
	{
		String head = protocolFromServer.substring(0, 4);
		// 2051:'아이디from':'아이디to':'메세지내용'
		if(head.equals(""+NetworkProtocolHeads.MESSAGE_RECEIVE_PROTOCOL))
		{
			System.out.println("protocolFromServer: "+protocolFromServer);
			StringTokenizer st = new StringTokenizer(protocolFromServer,":");
			st.nextToken();
			MessageVO receivedMessage = new MessageVO();
			receivedMessage.setTo(st.nextToken());
			receivedMessage.setFrom(st.nextToken());
			receivedMessage.setMessage(st.nextToken());
			return receivedMessage;
		}
		return null;
	}

	
//	=============================================================================================================================
//	PlanVO 로
	@Override
	public PlanVO toPlanVO(String protocol) 
	{
		String head = protocol.substring(0, 4);
		//	6051:'플랜번호':'제목':'플랜방번호':'작성자':'구분':'예상지출':'좌표':'기타':'링크'
		if(head.equals(""+NetworkProtocolHeads.NEW_PLAN_RECEIVE_PROTOCOL))
		{
			StringTokenizer st = new StringTokenizer(protocol, ":");
			st.nextToken();
			
			PlanVO plan = new PlanVO();
			plan.setPlanNum(Integer.parseInt(st.nextToken()));
			plan.setPlanName(st.nextToken());
			plan.setPlanRoomNum(Integer.parseInt(st.nextToken()));
			plan.setPlanUserId(st.nextToken());
			plan.setPlanPurposeName(st.nextToken());
			plan.setPlanMoney(Integer.parseInt(st.nextToken()));
			plan.setPlanLoc(st.nextToken());
			plan.setPlanOther(st.nextToken());
			String link = st.nextToken();
			link.replace("~", ":");
			plan.setPlanLink(link);
			plan.setPlanState("nor");
			return plan;
		}
		// 6052:'플랜번호'
		else if(head.equals(""+NetworkProtocolHeads.DELETE_CANDIDATE_PLAN_RECEIVE_PROTOCOL))
		{
			PlanVO plan = new PlanVO();
			plan.setPlanNum(Integer.parseInt(protocol.substring(5).trim()));
			return plan;
		}
		// 6151:'플랜번호':'플랜시간':'플랜날짜'
		else if(head.equals(""+NetworkProtocolHeads.ADD_PLAN_TO_VOTE_RECEIVE_PROTOCOL))
		{
			StringTokenizer st = new StringTokenizer(protocol, ":");
			st.nextToken();
			PlanVO plan = new PlanVO();
			plan.setPlanNum(Integer.parseInt(st.nextToken()));
			plan.setPlanTime(st.nextToken()+":"+st.nextToken());
			plan.setPlanDate(st.nextToken());
			return plan;
		}
		
		
		return null;
	}

	
//	=============================================================================================================================
//	RoomVO 로
	@Override
	public RoomVO toRoomVO(String protocol) 
	{
		String head = protocol.substring(0, 4);
		// 4051:'ROOM_NUM':'ROOM_OWNER_ID':'ROOM_NAME':'ROOM_PLACE':'ROOM_START_DATE':'ROOM_END_DATE':'ROOM_MEMBER_NUM'
		if(head.equals(""+NetworkProtocolHeads.ADD_ROOM_RESULT_RECEIVE_PROTOCOL))
		{
			try {
				RoomVO room = new RoomVO();
				StringTokenizer st = new StringTokenizer(protocol, ":");
				st.nextToken();
				room.setRoomNum(Integer.parseInt(st.nextToken()));
				room.setRoomOwnerId(st.nextToken());
				room.setRoomName(st.nextToken());
				room.setRoomPlace(st.nextToken());
				room.setRoomStartDate(st.nextToken());
				room.setRoomEndDate(st.nextToken());
				room.setRoomMemberNum(Integer.parseInt(st.nextToken()));
				return room;
			} 
			catch (NumberFormatException e) {
				System.out.println("ProtocolDecodeService: toRoomVO / addroom리스폰스 메세지 이상함 -> "+e.getMessage());	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
				return null;
			}
		}
		
//		4052:'초대한사람':'ROOM_NUM':'ROOM_OWNER_ID':'ROOM_NAME':'ROOM_PLACE':'ROOM_START_DATE':'ROOM_END_DATE':'ROOM_MEMBER_NUM' -> 5004
		else if(head.equals(""+NetworkProtocolHeads.ROOM_INVITE_RECEIVE_PROTOCOL))
		{
			try {
				RoomVO room = new RoomVO();
				StringTokenizer st = new StringTokenizer(protocol, ":");
				st.nextToken();
				room.setRoomMemberId(st.nextToken());
				room.setRoomNum(Integer.parseInt(st.nextToken()));
				room.setRoomOwnerId(st.nextToken());
				room.setRoomName(st.nextToken());
				room.setRoomPlace(st.nextToken());
				room.setRoomStartDate(st.nextToken());
				room.setRoomEndDate(st.nextToken());
				room.setRoomMemberNum(Integer.parseInt(st.nextToken()));
				return room;
			} catch (NumberFormatException e) {
				System.out.println("ProtocolDecodeService: toRoomVO / 초대 메세지 이상함 -> "+e.getMessage());	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
				return null;
			}
		}
		
		return null;
	}

	
//	=============================================================================================================================
//	UserVO 로
	
	@Override
	public UserVO toUserVO(String protocol) 
	{
		String head = protocol.substring(0, 4);
		// 1054:1(1:로그인성공, 2:비번오류, 3:아이디없음):'내이아디':'내이름':'내성별':'내폰번호':'동의플랜'
		if(head.equals(""+NetworkProtocolHeads.LOGIN_RESULT_RECEIVE_PROTOCOL))
		{
			UserVO user = new UserVO();
			StringTokenizer st = new StringTokenizer(protocol, ":");
			st.nextToken();
			String temp = st.nextToken();
			if(!temp.equalsIgnoreCase("1"))
			{
				if(temp.equals("2"))
					user.setUserState("2");
				else if(temp.equals("3"))
					user.setUserState("3");
				return user;
			}
			user.setUserId(st.nextToken());
			user.setUserName(st.nextToken());
			user.setUserGender(st.nextToken());
			user.setUserPhone(st.nextToken());
			user.setUserState("1");
			String agrees = st.nextToken();
			Set<String> agreeSet = new HashSet<>();
			if(agrees.equalsIgnoreCase("nope"))
			{
				user.setAgreeSet(agreeSet);
			}
			else
			{
				StringTokenizer stt = new StringTokenizer(agrees, "/");
				while(stt.hasMoreTokens())
				{
					agreeSet.add(stt.nextToken());
				}
				user.setAgreeSet(agreeSet);
			}
			return user;
		}
		// 3051:T(친구추가성공여부):'친구아이디':'친구이름':'T(친구상태)'
		else if(head.equals(""+NetworkProtocolHeads.ADD_FRIEND_RESULT_RECEIVE_PROTOCOL))
		{
			UserVO friend = new UserVO();
			StringTokenizer st = new StringTokenizer(protocol, ":");
			st.nextToken();
			String temp = st.nextToken();
			if(!temp.equalsIgnoreCase("t"))
				return null;
			friend.setUserId(st.nextToken());
			friend.setUserName(st.nextToken());
			friend.setUserState(st.nextToken());
			return friend;
		}
		// 5053:'2'(1:성공,2:친구id가없음,3:친구가거절, 4:친구가 미접속):'친구아이디':'친구이름':'친구상태'
		else if(head.equals(""+NetworkProtocolHeads.ADD_ROOM_MEMBER_RESULT_RECEIVE_PROTOCOL))
		{
			UserVO member = new UserVO();
			StringTokenizer st = new StringTokenizer(protocol, ":");
			st.nextToken();
			String temp = st.nextToken();
			if(temp.equals("1"))
			{
				member.setUserIpNow("성공");
				member.setUserId(st.nextToken());
				member.setUserName(st.nextToken());
				member.setUserState(st.nextToken());
			}
			else if(temp.equals("2"))
			{
				member.setUserIpNow("없는 아이디");
			}
			else if(temp.equals("3"))
			{
				member.setUserIpNow("거절하셨습니다.");
			}
			else if(temp.equals("4"))
			{
				member.setUserIpNow("친구가미접속");
			}
			return member;
		}
		
		return null;
	}

	
//	=============================================================================================================================
//	UserVO List 로
	@Override
	public ObservableList<UserVO> toUserList(String protocolFromServer) 
	{
		String head = protocolFromServer.substring(0, 4);
		//3054:'친구1아이디':'친구1이름':'친구1상태':'친구2아이디':'친구2이름':'친구2상태':....
		if(head.equals(""+NetworkProtocolHeads.FRIEND_LIST_RECEIVE_PROTOCOL))
		{
			ObservableList<UserVO> list = FXCollections.observableArrayList();
			StringTokenizer st = new StringTokenizer(protocolFromServer, ":");
			st.nextToken();
			while(st.hasMoreTokens())
			{
				UserVO user = new UserVO();
				user.setUserId(st.nextToken());
				user.setUserName(st.nextToken());
				if(st.nextToken().equalsIgnoreCase("T"))
					user.setUserState("접속중");
				else
					user.setUserState("미접속");
				list.add(user);
			}
			return list;
		}
		
		// 5051:'멤버1아이디':'멤버1이름':'멤버1상태':'멤버2아이디':'멤버2이름':'멤버2상태'....
		if(head.equals(""+NetworkProtocolHeads.ROOM_MEMBER_LIST_RECEIVE_PROTOCOL))
		{
			ObservableList<UserVO> list2 = FXCollections.observableArrayList();
			StringTokenizer st = new StringTokenizer(protocolFromServer, ":");
			st.nextToken();
			while(st.hasMoreTokens())
			{
				UserVO user = new UserVO();
				user.setUserId(st.nextToken());
				user.setUserName(st.nextToken());
				if(st.nextToken().equalsIgnoreCase("T"))
					user.setUserState("접속중");
				else
					user.setUserState("미접속");
				list2.add(user);
			}
			return list2;
		}
		
		return null;
	}

	
//	=============================================================================================================================
//	RoomVO List 로
	@Override
	public ObservableList<RoomVO> toRoomList(String protocolFromServer) 
	{
		// 4054:'room_num':'room_owner_id':'room_name':'room_place':'start_date':'end_date':'room_member_num'
		if(protocolFromServer.substring(0, 4).equals(""+NetworkProtocolHeads.ROOM_LIST_RECEIVE_PROTOCOL))
		{
			try 
			{
				ObservableList<RoomVO> roomList = FXCollections.observableArrayList();
				StringTokenizer st = new StringTokenizer(protocolFromServer, ":");
				st.nextToken();
				while(st.hasMoreTokens())
				{
					RoomVO room = new RoomVO();
					room.setRoomNum(Integer.parseInt(st.nextToken()));
					room.setRoomOwnerId(st.nextToken());
					room.setRoomName(st.nextToken());
					room.setRoomPlace(st.nextToken());
					room.setRoomStartDate(st.nextToken());
					room.setRoomEndDate(st.nextToken());
					room.setRoomMemberNum(Integer.parseInt(st.nextToken()));
					roomList.add(room);
				}
				return roomList;
			} 
			catch (NumberFormatException e) {
				System.out.println("ProtocolDecodeService: toRoomList -> "+e.getMessage());	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			}
			
		}
		return null;
	}

//	=============================================================================================================================
//	ChatVO List 로
	
	@Override
	public ObservableList<ChatVO> toChatList(String protocolFromServer) 
	{
		// 5054:'방번호':'채팅시간1':'채팅한사람1':'채팅내용1':'채팅시간2':'채팅한사람2':'채팅내용2'....
		if(protocolFromServer.substring(0, 4).equals(""+NetworkProtocolHeads.CHAT_LIST_RECEIVE_PROTOCOL))
		{
			StringTokenizer st = new StringTokenizer(protocolFromServer, ":");
			st.nextToken();
			int roomNum = Integer.parseInt(st.nextToken());
			ObservableList<ChatVO> list = FXCollections.observableArrayList();
			while(st.hasMoreTokens())
			{
				ChatVO chat = new ChatVO();
				chat.setChatRoomNum(roomNum);
				String time = st.nextToken()+":"+st.nextToken();
				chat.setChatTime(time);
				chat.setChatMemberId(st.nextToken());
				chat.setChatMessage(st.nextToken());
				list.add(chat);
			}
			return list;
		}
		
		return null;
	}

	
//	=============================================================================================================================
//	PlanVO List로
	
	@Override
	public ObservableList<PlanVO> toPlanList(String protocolFromServer) 
	{
		String head = protocolFromServer.substring(0, 4);
		//* 6054:'플렌번호':'플랜명':'플랜방번호':'작성자':'구분':'예상지출':'기타':'링크':'좌표':'플랜상태':'플랜날짜':'플랜:시간':'대표여부':'동의자수'
		if(head.equals(""+NetworkProtocolHeads.ALL_PLAN_LIST_RECEIVE_PROTOCOL))
		{
			StringTokenizer st = new StringTokenizer(protocolFromServer, ":");
			st.nextToken();
			
			ObservableList<PlanVO> list = FXCollections.observableArrayList();
			
			while(st.hasMoreTokens())
			{
				PlanVO plan = new PlanVO();
				plan.setPlanNum(Integer.parseInt(st.nextToken()));
				plan.setPlanName(st.nextToken());
				plan.setPlanRoomNum(Integer.parseInt(st.nextToken()));
				plan.setPlanUserId(st.nextToken());
				plan.setPlanPurposeName(st.nextToken());
				
				String money = st.nextToken();
				if(!money.equals("nope"))
					plan.setPlanMoney(Integer.parseInt(money));
				String other = st.nextToken();
				if(!other.equals("nope"))
					plan.setPlanOther(other);
				String link = st.nextToken();
				if(!link.equals("nope"))
				{
					link.replace("~", ":");
					plan.setPlanLink(link);
				}
				String loc = st.nextToken();
				if(!loc.equals("nope"))
					plan.setPlanLoc(loc);
				
				String state = st.nextToken();
				plan.setPlanState(state);
				
				if(!state.equals("nor"))
				{
					plan.setPlanDate(st.nextToken());
					plan.setPlanTime((st.nextToken()+":"+st.nextToken()));
					plan.setPlanRep(st.nextToken());
				}
				else
				{
					st.nextToken();
					st.nextToken();
					st.nextToken();
					st.nextToken();
					plan.setPlanDate("nope");
					plan.setPlanTime("nope");
					plan.setPlanRep("nope");
				}
				plan.setPlanAgreeNum(Integer.parseInt(st.nextToken()));
				
				list.add(plan);
			}
			return list;
		}
		
		return null;
	}
	
	
//	=============================================================================================================================
//	ChatVO 로
	
	@Override
	public ChatVO toChatVO(String protocolFromServer) 
	{
		// 5052:'방번호':'채팅한사람':'채팅내용'
		if(protocolFromServer.substring(0, 4).equals(""+NetworkProtocolHeads.CHAT_RECEIVE_RECEIVE_PROTOCOL))
		{
			ChatVO chat = new ChatVO();
			StringTokenizer st = new StringTokenizer(protocolFromServer, ":");
			st.nextToken();
			chat.setChatRoomNum(Integer.parseInt(st.nextToken()));
			chat.setChatMemberId(st.nextToken());
			chat.setChatMessage(st.nextToken());
			return chat;
		}
		
		
		return null;
	}

	
//	=============================================================================================================================
//	String List 로
	
	@Override
	public ObservableList<String> toStringList(String protocolFromServer) 
	{
		// 6053:'구분1':'구분2':'구분3'....
		if(protocolFromServer.substring(0, 4).equals(""+NetworkProtocolHeads.COMBOBOX_PURPOSE_LIST_RECEIVE_PROTOCOL))
		{
			ObservableList<String> list =FXCollections.observableArrayList();
			StringTokenizer st = new StringTokenizer(protocolFromServer, ":");
			st.nextToken();
			while(st.hasMoreTokens())
			{
				list.add(st.nextToken());
			}
			return list;
		}
		return null;
	}

	

}
