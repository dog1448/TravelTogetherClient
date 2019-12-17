package mini.client.network.Services;

import javafx.collections.ObservableList;
import mini.client.VO.ChatVO;
import mini.client.VO.MessageVO;
import mini.client.VO.PlanVO;
import mini.client.VO.RoomVO;
import mini.client.VO.UserVO;

public interface IProtocolDecodeService 
{
	public int getTypeOfResult(String protocol);
	
	public boolean isStringProtocol(String protocolFromServer);
	public boolean toBoolean(String protocolFromServer);
	public String rsToString(String protocolFromServer);
	public MessageVO toMessageVO(String protocolFromServer);
	public PlanVO toPlanVO(String protocolFromServer);
	public RoomVO toRoomVO(String protocolFromServer);
	public UserVO toUserVO(String protocolFromServer);
	public ObservableList<UserVO> toUserList(String protocolFromServer);
	public ObservableList<RoomVO> toRoomList(String protocolFromServer);
	public ObservableList<ChatVO> toChatList(String protocolFromServer);
	public ObservableList<PlanVO> toPlanList(String protocolFromServer);
	public ChatVO toChatVO(String protocolFromServer);
	public ObservableList<String> toStringList(String protocolFromServer);
}
