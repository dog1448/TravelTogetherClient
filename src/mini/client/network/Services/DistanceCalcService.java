package mini.client.network.Services;

import mini.client.VO.PlanVO;

public class DistanceCalcService implements IDistanceCalcService
{
	private PlanVO selectedFinalPlan;
	private double lat1;
	private double lon1;
	
	
	public void setSelectedFinalPlan(PlanVO selectedFinalPlan) {
		this.selectedFinalPlan = selectedFinalPlan;
		if(selectedFinalPlan == null)
			return;
		if(selectedFinalPlan.equals("nope"))
			return;
		
		if(selectedFinalPlan.getPlanLoc() != null)
		{
			String fullLoc = selectedFinalPlan.getPlanLoc().trim(); 
			if(fullLoc.charAt(fullLoc.length()-1) == 'E' || fullLoc.charAt(fullLoc.length()-1) == 'N')
			{
				String latS = fullLoc.split(" ")[0]; //위도(북위)
				String lonS = fullLoc.split(" ")[1]; //경도(동경)
				
				lat1 = Integer.parseInt(latS.split("°")[0]) + Integer.parseInt(latS.split("°")[1].split("'")[0])/60.0 + Double.parseDouble(latS.split("'")[1].split("\"")[0])/3600.0;  
				lon1 = Integer.parseInt(lonS.split("°")[0]) + Integer.parseInt(lonS.split("°")[1].split("'")[0])/60.0 + Double.parseDouble(lonS.split("'")[1].split("\"")[0])/3600.0;
			}
			else
			{
				lat1 = Double.parseDouble(fullLoc.split(", ")[0]);
				lon1 = Double.parseDouble(fullLoc.split(", ")[1]);
			}
		}
	}
	

	@Override
	public int getDistanceFromSelectedFinalPlan(String planLoc) 
	{
		if(selectedFinalPlan == null)
			return -1;
		if(selectedFinalPlan.getPlanLoc() == null)
			return -1;
		if(planLoc == null)
			return -1;
		if(planLoc.equals("nope"))
			return -1;
		
		String fullLoc = planLoc;
		double lat2 = -999;
		double lon2 = -999;
		if(fullLoc.charAt(fullLoc.length()-1) == 'E' || fullLoc.charAt(fullLoc.length()-1) == 'N')
		{
			String latS = fullLoc.split(" ")[0]; //위도(북위)
			String lonS = fullLoc.split(" ")[1]; //경도(동경)
			
			lat2 = Integer.parseInt(latS.split("°")[0]) + Integer.parseInt(latS.split("°")[1].split("'")[0])/60.0 + Double.parseDouble(latS.split("'")[1].split("\"")[0])/3600.0;  
			lon2 = Integer.parseInt(lonS.split("°")[0]) + Integer.parseInt(lonS.split("°")[1].split("'")[0])/60.0 + Double.parseDouble(lonS.split("'")[1].split("\"")[0])/3600.0;
		}
		else
		{
			lat2 = Double.parseDouble(fullLoc.split(", ")[0]);
			lon2 = Double.parseDouble(fullLoc.split(", ")[1]);
		}
		
		
		int finalDistance = (int)distance(lat1, lon1, lat2, lon2);
		
		return finalDistance;
	}
	
	
	private static double distance(double lat1, double lon1, double lat2, double lon2) {
        
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
         
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
         
        dist = dist * 1609.344;
 
        return (dist);
    }
	
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }
     
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }


	@Override
	public int getDistanceFromTwoPoint(String planLoc1, String planLoc2) 
	{
		if(planLoc1 == null)
			return -1;
		if(planLoc2 == null)
			return -1;
		
		String fullLoc1 = planLoc1;
		double lat1 = -999;
		double lon1 = -999;
		if(fullLoc1.charAt(fullLoc1.length()-1) == 'E' || fullLoc1.charAt(fullLoc1.length()-1) == 'N')
		{
			String latS1 = fullLoc1.split(" ")[0]; //위도(북위)
			String lonS1 = fullLoc1.split(" ")[1]; //경도(동경)
			
			lat1 = Integer.parseInt(latS1.split("°")[0]) + Integer.parseInt(latS1.split("°")[1].split("'")[0])/60.0 + Double.parseDouble(latS1.split("'")[1].split("\"")[0])/3600.0;  
			lon1 = Integer.parseInt(lonS1.split("°")[0]) + Integer.parseInt(lonS1.split("°")[1].split("'")[0])/60.0 + Double.parseDouble(lonS1.split("'")[1].split("\"")[0])/3600.0;
		}
		else
		{
			lat1 = Double.parseDouble(fullLoc1.split(", ")[0]);
			lon1 = Double.parseDouble(fullLoc1.split(", ")[1]);
		}
		
		String fullLoc2 = planLoc2;
		double lat2 = -999;
		double lon2 = -999;
		if(fullLoc2.charAt(fullLoc2.length()-1) == 'E' || fullLoc2.charAt(fullLoc2.length()-1) == 'N')
		{
			String latS2 = fullLoc2.split(" ")[0]; //위도(북위)
			String lonS2 = fullLoc2.split(" ")[1]; //경도(동경)
			
			lat2 = Integer.parseInt(latS2.split("°")[0]) + Integer.parseInt(latS2.split("°")[1].split("'")[0])/60.0 + Double.parseDouble(latS2.split("'")[1].split("\"")[0])/3600.0;  
			lon2 = Integer.parseInt(lonS2.split("°")[0]) + Integer.parseInt(lonS2.split("°")[1].split("'")[0])/60.0 + Double.parseDouble(lonS2.split("'")[1].split("\"")[0])/3600.0;
		}
		else
		{
			lat2 = Double.parseDouble(fullLoc2.split(", ")[0]);
			lon2 = Double.parseDouble(fullLoc2.split(", ")[1]);
		}
		
		
		int finalDistance = (int)distance(lat1, lon1, lat2, lon2);
		return finalDistance;
	}

	
}
