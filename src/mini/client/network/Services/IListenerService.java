package mini.client.network.Services;

import mini.client.UIControllers.ChatRoomController;
import mini.client.UIControllers.DialogController;
import mini.client.UIControllers.LobbyController;
import mini.client.UIControllers.LoginController;
import mini.client.UIControllers.MainUIController;
import mini.client.UIControllers.PlanController;

public interface IListenerService 
{
	public void startListener();
	public void setDialogController(DialogController dialogController);
	public void setLoginController(LoginController loginController);
	public void setMainUIController(MainUIController mainUIController);
	public void setLobbyController(LobbyController lobbyController);
	public void setChatRoomController(ChatRoomController chatRoomController);
	public void setPlanController(PlanController planController);
}
