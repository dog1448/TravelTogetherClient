package mini.client.VO;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SearchVO 
{
//	private final StringProperty chatMemberId;
	private final StringProperty name;
	private final DoubleProperty x;
	private final DoubleProperty y;
	
	public SearchVO() {
		this(null,0,0);
	}
	
	public SearchVO(String name, double x, double y) 
	{
		this.name = new SimpleStringProperty(name);
		this.x = new SimpleDoubleProperty(x);
		this.y = new SimpleDoubleProperty(y);
	}

	public StringProperty getNameProperty() {
		return name;
	}

	public DoubleProperty getXProperty() {
		return x;
	}

	public DoubleProperty getYProperty() {
		return y;
	}

	
	
	public String getName() {
		return name.get();
	}

	public double getX() {
		return x.get();
	}

	public double getY() {
		return y.get();
	}
	
	
	public void setName(String name)
	{
		this.name.set(name);
	}
	
	public void setX(double x)
	{
		this.x.set(x);
	}
	
	public void setY(double y)
	{
		this.y.set(y);
	}
	
	
	
	
}
