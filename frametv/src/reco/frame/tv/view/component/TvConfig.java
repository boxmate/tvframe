package reco.frame.tv.view.component;

public class TvConfig {

	
	public static int startId=1000001;
	public static int freeId=1000001;
	
	public static int buildId(){
		freeId++;
		return freeId;
	}
}
