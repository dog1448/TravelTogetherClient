package mini.client.network.Services;

import mini.client.VO.PlanVO;

public interface IDistanceCalcService 
{
	public void setSelectedFinalPlan(PlanVO selectedFinalPlan);
	public int getDistanceFromSelectedFinalPlan(String planLoc);
	public int getDistanceFromTwoPoint(String planLoc1, String planLoc2);
}
