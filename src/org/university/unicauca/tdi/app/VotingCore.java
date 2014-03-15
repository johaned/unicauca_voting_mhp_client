package org.university.unicauca.tdi.app;
/**
 * 
 * Autor: Johan Tique
 * 
 */
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.tv.xlet.Xlet;
import javax.tv.xlet.XletContext;
import javax.tv.xlet.XletStateChangeException;

import org.havi.ui.HScene;
import org.havi.ui.HSceneFactory;
import org.havi.ui.HSceneTemplate;
import org.university.unicauca.tdi.model.Layout;
import org.university.unicauca.tdi.model.Scene;

public class VotingCore implements Xlet{
	private XletContext context;
	private HScene scene;
	private String sceneName = "SignIn";
	private HSceneFactory factory;
	private HSceneTemplate hst;
	private Scene sceneInterface;
	
	//application variables
	private String sessionToken;
	private String userName;

	public void destroyXlet(boolean arg0) throws XletStateChangeException {}

	public void initXlet(XletContext context) throws XletStateChangeException {
		this.context = context;
		createScene();
		
		// It gets server IP address from resource file
		try {
			FileInputStream fstream = new FileInputStream("org/university/unicauca/tdi/res/ip.txt");
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			Layout.server_domain=br.readLine();
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void pauseXlet() {}

	public void startXlet() throws XletStateChangeException {
		setLogMsg("Start XLET");
		System.out.println("RUNNING...");
		//it cleans all scene's components
		scene.removeAll();
		setLogMsg("OPENING => " + sceneName);
		//it  dynamically loads and deploys  scene
		loadScene(sceneName);
		scene.repaint();
		
	}
	
	private void createScene() {
		HSceneFactory factory = HSceneFactory.getInstance();
		HSceneTemplate hst = new HSceneTemplate();
		hst.setPreference(HSceneTemplate.SCENE_SCREEN_DIMENSION,new org.havi.ui.HScreenDimension(1, 1), HSceneTemplate.REQUIRED);
		hst.setPreference(HSceneTemplate.SCENE_SCREEN_LOCATION,new org.havi.ui.HScreenPoint(0, 0), HSceneTemplate.REQUIRED);
		scene = factory.getBestScene(hst);
		Rectangle rect = scene.getBounds();
		scene.setBounds(rect);
		scene.setRenderMode(HScene.IMAGE_CENTER);
		scene.repaint();
		// scene.add(this);
		scene.setVisible(true);
		// this.requestFocus();
	}
	
	private void loadScene(String sceneName) {
		try {
			//it takes a class reference associated with scene class
			Class referenceScene = Class.forName("org.university.unicauca.tdi.scene."+ sceneName);
			if (referenceScene == null)
				System.out.println("null class");
			//Scene is instantiated
			sceneInterface = (Scene) referenceScene.newInstance();
			//Scene is initialized
			sceneInterface.initializer(this);
		} catch (Exception e) {
			System.out.println("ERROR: Could not load class: " + e);
		}
	}
	
	public void setLogMsg(String msg) {
		System.out.println("LOG: " + msg);
	}

	public String getSceneName() {
		return sceneName;
	}

	public void setSceneName(String sceneName) {
		this.sceneName = sceneName;
	}

	public XletContext getContext() {
		return context;
	}

	public HScene getScene() {
		return scene;
	}

	public String getSessionToken() {
		return sessionToken;
	}

	public void setSessionToken(String sessionToken) {
		this.sessionToken = sessionToken;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}	
}
