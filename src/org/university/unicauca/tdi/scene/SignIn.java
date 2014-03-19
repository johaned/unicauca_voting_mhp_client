package org.university.unicauca.tdi.scene;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import javax.tv.xlet.XletContext;
import javax.tv.xlet.XletStateChangeException;

import org.dvb.ui.DVBColor;
import org.havi.ui.HComponent;
import org.havi.ui.HScene;
import org.havi.ui.HSinglelineEntry;
import org.havi.ui.HStaticIcon;
import org.havi.ui.HStaticText;
import org.havi.ui.HTextButton;
import org.havi.ui.HVisible;
import org.havi.ui.event.HActionListener;
import org.havi.ui.event.HKeyListener;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.university.unicauca.tdi.app.VotingCore;
import org.university.unicauca.tdi.conn.RestClient;
import org.university.unicauca.tdi.control.VideoManager;
import org.university.unicauca.tdi.model.Layout;
import org.university.unicauca.tdi.model.Scene;

public class SignIn extends HComponent implements Scene, HKeyListener, HActionListener {

	private XletContext ctx;
	private HScene scene;
	private VotingCore votingCore;

	// Scene components
	private static final String MSG_SIGNIN_LABEL = "Sign In";
	private static final String NAME_SIGIN_LABEL = "Name";
	private static final String PWD_SIGIN_LABEL = "Password";
	private static final String SIGNIN_BUTTON_LABEL = "Submit";
	private static final String SIGNIN_ERROR_LABEL = "Please verify your access credentials";
	private static final String SIGNIN_LOADING_LABEL = "Cheking your credentials...";
	private HStaticText flashLabel;
	private boolean hasFlashMsg = false;
	private HStaticText msgLabel;
	private HStaticText nameLabel;
	private HStaticText pwdLabel;
	private HSinglelineEntry nameTextfield;
	private HSinglelineEntry passwordTextfield;
	private HTextButton signinButton;
	
	// communication
	private RestClient rc;
	private String resource = "/sign_in";
	
	// JSON Parser
	private JSONParser parser;

	public void cleaner() {
		scene.removeKeyListener(this);
		scene.removeAll();
		scene.repaint();
	}

	public void initializer(VotingCore votingCore) {
		this.ctx = votingCore.getContext();
		VideoManager.setSizeToApp();
		this.scene = votingCore.getScene();
		this.votingCore = votingCore;
		rc = new RestClient((String)this.ctx.getXletProperty("dvb.org.id"));
		parser = new JSONParser();
		get_resources();
		config_container();

	}

	private void get_resources() {
		// nothing...
	}

	private void config_container() {
		// It configures the component bound, it's important in order to access
		// to native paint method
		Rectangle rect = scene.getBounds();
		setBounds(rect);
		setVisible(true);
		// it adds component in current scene
		scene.add(this);
		scene.addKeyListener(this);
		scene.requestFocus();
		add_components();
		scene.repaint();
		scene.setVisible(true);

	}

	private void add_components() {
		//scene.setBackgroundMode(HScene.BACKGROUND_FILL);
		//scene.setBackground(Color.black);

		msgLabel = new HStaticText(MSG_SIGNIN_LABEL, 50, 420, 100, 40);
		msgLabel.setHorizontalAlignment(HVisible.HALIGN_LEFT);
		msgLabel.setBordersEnabled(false);
		msgLabel.setForeground(Color.white);
		msgLabel.setFont(new Font("Tiresias", Font.BOLD, 24));

		nameLabel = new HStaticText(NAME_SIGIN_LABEL, 50, 460, 100, 40);
		nameLabel.setHorizontalAlignment(HVisible.HALIGN_LEFT);
		nameLabel.setBordersEnabled(false);
		nameLabel.setForeground(Color.white);
		nameLabel.setFont(new Font("Tiresias", Font.BOLD, 24));

		nameTextfield = new HSinglelineEntry();
		nameTextfield.setBounds(160, 460, 300, 38);
		nameTextfield.setEnabled(true);
		nameTextfield.setTextContent("", HVisible.NORMAL_STATE);
		nameTextfield.setBordersEnabled(true);
		nameTextfield.setForeground(Color.blue);
		nameTextfield.setBackground(Color.white);
		nameTextfield.setBackgroundMode(HVisible.BACKGROUND_FILL);
		nameTextfield.setFont(new Font("Tiresias", Font.BOLD, 22));
		nameTextfield.setMaxChars(30);
		nameTextfield.setEditMode(true);
		nameTextfield.setHorizontalAlignment(HVisible.HALIGN_LEFT);
		
		pwdLabel = new HStaticText(PWD_SIGIN_LABEL, 50, 500, 100, 40);
		pwdLabel.setHorizontalAlignment(HVisible.HALIGN_LEFT);
		pwdLabel.setBordersEnabled(false);
		pwdLabel.setForeground(Color.white);
		pwdLabel.setFont(new Font("Tiresias", Font.BOLD, 24));

		passwordTextfield = new HSinglelineEntry();
		passwordTextfield.setBounds(160, 500, 300, 38);
		passwordTextfield.setEnabled(true);
		passwordTextfield.setTextContent("", HVisible.NORMAL_STATE);
		passwordTextfield.setBordersEnabled(true);
		passwordTextfield.setForeground(Color.blue);
		passwordTextfield.setBackground(Color.white);
		passwordTextfield.setBackgroundMode(HVisible.BACKGROUND_FILL);
		passwordTextfield.setFont(new Font("Tiresias", Font.BOLD, 22));
		passwordTextfield.setMaxChars(30);
		passwordTextfield.setEditMode(true);
		passwordTextfield.setHorizontalAlignment(HVisible.HALIGN_LEFT);

		signinButton = new HTextButton(SIGNIN_BUTTON_LABEL, 510, 500, 100, 40);
		signinButton.setTextContent(SIGNIN_BUTTON_LABEL, HVisible.FOCUSED_STATE);
		signinButton.setBordersEnabled(true);
		signinButton.setForeground(Color.white);
		signinButton.setBackground(Color.lightGray);
		signinButton.setBackgroundMode(HVisible.BACKGROUND_FILL);
		signinButton.setFont(new Font("Tiresias", Font.BOLD, 24));

		scene.add(msgLabel);
		scene.add(nameLabel);
		scene.add(nameTextfield);
		scene.add(pwdLabel);
		scene.add(passwordTextfield);
		scene.add(signinButton);
		scene.addKeyListener(this);
		
		// It adds actionPerformed method
		signinButton.addHActionListener(this);
		
		// It adds all necessary KeyListener
		nameTextfield.addKeyListener(this);
		passwordTextfield.addKeyListener(this);
		signinButton.addKeyListener(this);
		
		// You can use setMove method for navigation purpose
		//nameTextfield.setMove(VK_ABA, passwordTextfield);
		//passwordTextfield.setMove(VK_ARR, nameTextfield);
		
		// Also you can use traversal focus for same purpose
		//passwordTextfield.setFocusTraversal(nameTextfield, nameTextfield, nameTextfield, nameTextfield);
		
		nameTextfield.requestFocus();
	}
	
	public void paint(Graphics graphics){
		graphics.setColor(new DVBColor(255, 255, 255, 255));
		graphics.drawLine(0, 418, 720, 418);
	}

	private void change_scene(String sceneName) {
		votingCore.setSceneName(sceneName);
		cleaner();
		votingCore.getScene().removeAll();
		votingCore.pauseXlet();
		try {
			votingCore.startXlet();
		} catch (XletStateChangeException e) {
			e.printStackTrace();
		}
	}

	public void keyPressed(KeyEvent key) {
		System.out.println("key code: "+ key.getKeyCode());
		switch (key.getKeyCode()) {
			case VK_UP:
				setLogMsg("name");
				nameTextfield.requestFocus();
				break;
			case VK_DOWN:
				setLogMsg("password");
				passwordTextfield.requestFocus();
				break;
			case VK_RIGHT:
				signinButton.requestFocus();
				break;
			default:
				break;
		}
	}

	public void keyReleased(KeyEvent key) {
	}

	public void keyTyped(KeyEvent key) {
	}

	public void actionPerformed(ActionEvent event) {
		try {
			if(!hasFlashMsg){
				setLogMsg("first time");
				flashLabel = new HStaticText(SIGNIN_LOADING_LABEL, 160, 450, 320, 40);
				flashLabel.setForeground(Color.blue);
				flashLabel.setBordersEnabled(false);
				flashLabel.setFont(new Font("Tiresias", Font.BOLD, 24));	
				scene.add(flashLabel);
				hasFlashMsg = true;
			}else{
				setLogMsg("after time");			
				flashLabel.setTextContent(SIGNIN_LOADING_LABEL, HVisible.EDIT_MODE_CHANGE);
				flashLabel.setForeground(Color.blue);
			}		
			scene.repaint();
			
			String data = "username="+nameTextfield.getTextContent(HVisible.ALL_STATES)+"&password="+passwordTextfield.getTextContent(HVisible.ALL_STATES);
			String jsonResponse = rc.post("http://"+Layout.server_domain+resource, data);
			setLogMsg(jsonResponse);
			JSONObject objectResponse =(JSONObject)parser.parse(jsonResponse);
			
			// You can use this methods to fetch purpose
			//System.out.println("-->"+objectResponse.get("msg"));
			//System.out.println("-->"+objectResponse.keySet());
			//System.out.println("-->"+objectResponse.values());
			
			int responseCode = Integer.parseInt(objectResponse.get("code").toString());
			switch (responseCode) {
			case 200:
				setLogMsg("session accepted");
				votingCore.setSessionToken(objectResponse.get("token").toString());
				change_scene("Dashboard");
				break;
			case 401:
				setLogMsg("session rejected");
				flashLabel.setTextContent(SIGNIN_ERROR_LABEL, HVisible.EDIT_MODE_CHANGE);
				flashLabel.setForeground(Color.red);	
				nameTextfield.requestFocus();
				scene.repaint();
				break;

			default:
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
	}

	public void setLogMsg(String msg) {
		System.out.println("LOG SCENE(" + votingCore.getSceneName() + "): "+ msg);
	}

}
