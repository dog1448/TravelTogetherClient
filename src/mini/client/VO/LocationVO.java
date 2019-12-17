package mini.client.VO;

public class LocationVO 
{
	private String lon;	//경도(동경)
	private int lon1;
	private int lon2;
	private double lon3;
	
	private String lat; //위도(북위)
	private int lat1;
	private int lat2;
	private double lat3;
	
	
//	Degree + Minutes/60 + Seconds/3600
	public LocationVO(String fullLoc) 
	{
		fullLoc = fullLoc.trim();
		if(fullLoc.charAt(fullLoc.length()-1) == 'E' || fullLoc.charAt(fullLoc.length()-1) == 'N')
		{}
		else
		{
		}
		lon = fullLoc.split(" ")[1]; //경도(동경)
		lon1 = Integer.parseInt(lon.split("°")[0]);
		lon2 = Integer.parseInt(lon.split("°")[1].split("'")[0]);
		lon3 = Double.parseDouble(lon.split("'")[1].split("\"")[0]);
		
		lat = fullLoc.split(" ")[0]; //위도(북위)
		lat1 = Integer.parseInt(lat.split("°")[0]);
		lat2 = Integer.parseInt(lat.split("°")[1].split("'")[0]);
		lat3 = Double.parseDouble(lat.split("'")[1].split("\"")[0]);
	}

	public String getLon() {
		return lon;
	}

	public void setLon(String lon) {
		this.lon = lon;
	}

	public int getLon1() {
		return lon1;
	}

	public void setLon1(int lon1) {
		this.lon1 = lon1;
	}

	public int getLon2() {
		return lon2;
	}

	public void setLon2(int lon2) {
		this.lon2 = lon2;
	}

	public double getLon3() {
		return lon3;
	}

	public void setLon3(double lon3) {
		this.lon3 = lon3;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public int getLat1() {
		return lat1;
	}

	public void setLat1(int lat1) {
		this.lat1 = lat1;
	}

	public int getLat2() {
		return lat2;
	}

	public void setLat2(int lat2) {
		this.lat2 = lat2;
	}

	public double getLat3() {
		return lat3;
	}

	public void setLat3(double lat3) {
		this.lat3 = lat3;
	}
	
	
	
}
