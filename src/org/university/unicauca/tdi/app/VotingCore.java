package org.university.unicauca.tdi.app;
/**
 * 
 * Autor: Johan Tique
 * 
 */
import java.awt.Rectangle;

import javax.tv.xlet.Xlet;
import javax.tv.xlet.XletContext;
import javax.tv.xlet.XletStateChangeException;

import org.havi.ui.HScene;
import org.havi.ui.HSceneFactory;
import org.havi.ui.HSceneTemplate;
import org.university.unicauca.tdi.model.Scene;

public class VotingCore implements Xlet{
	private XletContext context;
	private HScene scene;
	private String sceneName = "HS_welcome";
	private HSceneFactory factory;
	private HSceneTemplate hst;
	private Scene sceneInterface;

	public void destroyXlet(boolean arg0) throws XletStateChangeException {
		// TODO Auto-generated method stub
		
	}

	public void initXlet(XletContext arg0) throws XletStateChangeException {
		this.context = context;
		createScene();	
	}

	public void pauseXlet() {
		// TODO Auto-generated method stub
		
	}

	public void startXlet() throws XletStateChangeException {
		setLogMsg("Start XLET");
		System.out.println("RUNNING...");
		//it cleans all scene's components
		scene.removeAll();
		setLogMsg("OPENING =>" + sceneName);
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
	
	private void loadScene(String scene) {
		try {
			//it takes a class reference associated with scene class
			Class referenceScene = Class.forName("org.university.unicauca.tdi.scene."+ scene);
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
	
	

}
