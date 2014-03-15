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
import org.university.unicauca.tdi.model.Layout;
import org.university.unicauca.tdi.model.Scene;

public class Dashboard extends HComponent implements Scene, HKeyListener, HActionListener {

	private XletContext ctx;
	private HScene scene;
	private VotingCore votingCore;

	// Scene components
	private static final String WELCOME_LABEL = "Welcome";
	private static final String VOTE_BUTTON_LABEL = "Vote";
	private static final String CHECK_BUTTON_LABEL = "Check Results";
	private static final String EXIT_BUTTON_LABEL = "Exit";
	private HStaticText welcomeLabel;
	private HTextButton voteButton;
	private HTextButton checkButton;
	private HTextButton exitButton;
	
	// communication
	private RestClient rc;
	private String userNameResource = "/user/name";
	
	
	// JSON Parser
	private JSONParser parser;
	
	// Misc
	private String userName;

	public void cleaner() {
		scene.removeKeyListener(this);
		scene.removeAll();
		scene.repaint();
	}

	public void initializer(VotingCore votingCore) {
		this.ctx = votingCore.getContext();
		this.scene = votingCore.getScene();
		this.votingCore = votingCore;
		rc = new RestClient((String)this.ctx.getXletProperty("dvb.org.id"));
		rc.setAuth(votingCore.getSessionToken());
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
		scene.setBackgroundMode(HScene.BACKGROUND_FILL);
		scene.setBackground(Color.black);

		if(votingCore.getUserName()== null || votingCore.getUserName().equals("")){
			try {
				this.userName = getUserName();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			this.userName = votingCore.getUserName();	
		}
		
		welcomeLabel = new HStaticText(WELCOME_LABEL+" "+this.userName+"!", 10, 450, 400, 40);
		welcomeLabel.setBordersEnabled(false);
		welcomeLabel.setHorizontalAlignment(HVisible.HALIGN_LEFT);
		welcomeLabel.setForeground(Color.white);
		welcomeLabel.setFont(new Font("Tiresias", Font.BOLD, 24));

		voteButton = new HTextButton(VOTE_BUTTON_LABEL, 30, 493, 200, 40);
		voteButton.setTextContent(VOTE_BUTTON_LABEL, HVisible.FOCUSED_STATE);
		voteButton.setBordersEnabled(true);
		voteButton.setForeground(Color.white);
		voteButton.setBackground(Color.green);
		voteButton.setBackgroundMode(HVisible.BACKGROUND_FILL);
		voteButton.setFont(new Font("Tiresias", Font.BOLD, 24));
		
		checkButton = new HTextButton(CHECK_BUTTON_LABEL, 260, 493, 200, 40);
		checkButton.setTextContent(CHECK_BUTTON_LABEL, HVisible.FOCUSED_STATE);
		checkButton.setBordersEnabled(true);
		checkButton.setForeground(Color.black);
		checkButton.setBackground(Color.yellow);
		checkButton.setBackgroundMode(HVisible.BACKGROUND_FILL);
		checkButton.setFont(new Font("Tiresias", Font.BOLD, 24));
		
		exitButton = new HTextButton(EXIT_BUTTON_LABEL, 490, 493, 200, 40);
		exitButton.setTextContent(EXIT_BUTTON_LABEL, HVisible.FOCUSED_STATE);
		exitButton.setBordersEnabled(true);
		exitButton.setForeground(Color.white);
		exitButton.setBackground(Color.red);
		exitButton.setBackgroundMode(HVisible.BACKGROUND_FILL);
		exitButton.setFont(new Font("Tiresias", Font.BOLD, 24));

		scene.add(welcomeLabel);
		scene.add(voteButton);
		scene.add(checkButton);
		scene.add(exitButton);
		scene.addKeyListener(this);
		
		// It adds actionPerformed method
		voteButton.addHActionListener(this);
		checkButton.addHActionListener(this);
		exitButton.addHActionListener(this);
		
		// It adds all necessary KeyListener
		voteButton.addKeyListener(this);
		checkButton.addKeyListener(this);
		exitButton.addKeyListener(this);
		
		// It configures traversal navigation
		voteButton.setFocusTraversal(exitButton, checkButton, exitButton, checkButton);
		checkButton.setFocusTraversal(voteButton, exitButton, voteButton, exitButton);
		exitButton.setFocusTraversal(checkButton, voteButton, checkButton, voteButton);
		
		voteButton.requestFocus();
	}
	
	public void paint(Graphics graphics){
		graphics.setColor(new DVBColor(255, 255, 255, 255));
		graphics.drawLine(0, 448, 720, 448);
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
				break;
			case VK_DOWN:
				setLogMsg("password");
				break;
			case VK_RIGHT:
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
		String buttonLabel = ((HTextButton)event.getSource()).getTextContent(HVisible.FOCUSED_STATE);
		
		switch (buttonLabel.charAt(0)) {
		case 'V':
			
			break;
		case 'C':
			
			break;
		case 'E':
			
			break;
		default:
			break;
		}	
	}
	
	private String getUserName() throws IOException{
		String data = "";
		return rc.post("http://"+Layout.server_domain+userNameResource, data);
	}

	public void setLogMsg(String msg) {
		System.out.println("LOG SCENE(" + votingCore.getSceneName() + "): "+ msg);
	}

}
