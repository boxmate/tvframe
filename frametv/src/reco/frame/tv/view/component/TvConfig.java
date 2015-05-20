package reco.frame.tv.view.component;

public class TvConfig {

	
	public final static int SCREEN_1280=1280,SCREEN_1920=1920,SCREEN_2560=2560,SCREEN_3840=3840;
	
	public static int startId=1000001;
	public static int freeId=1000001;
	
	public static int buildId(){
		freeId++;
		return freeId;
	}
}
