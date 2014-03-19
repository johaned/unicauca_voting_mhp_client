package org.university.unicauca.tdi.control;

import java.awt.Rectangle;
import javax.media.Control;
import javax.media.Player;
import javax.tv.media.AWTVideoSize;
import javax.tv.media.AWTVideoSizeControl;

import org.university.unicauca.tdi.app.VotingCore;

public class VideoManager {

	private static VotingCore votingCore;
	private static Player player;
	private static AWTVideoSizeControl avsc;

	public static void configurar(VotingCore main) {
		votingCore=main;
		player=votingCore.player;
		getControlPlayer();
	}
	
	private static void getControlPlayer(){
		 if(player!=null){
			Control[] controls = player.getControls();
		    if(controls!=null){
		        for(int i=0; i<controls.length; i++){
		        	if(controls[i] instanceof AWTVideoSizeControl){
		                avsc = (AWTVideoSizeControl) controls[i];
		                avsc.setSize(new AWTVideoSize(new Rectangle(0,0,720,576),new Rectangle(0,0,720,576)));
		            }
		        }
		    }
		}
	}
	
	public static void setSizeToApp(){
		if(avsc!=null){
			avsc.setSize(new AWTVideoSize(new Rectangle(0,0,720,576),new Rectangle(0, 0, 720, 576)));
		}
	}
	public static void setOriginalSize(){
		if(avsc!=null){
			avsc.setSize(new AWTVideoSize(new Rectangle(0,0,720,576),new Rectangle(0, 0, 720, 576)));
		}
	}
	 
}