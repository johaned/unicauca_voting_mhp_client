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

public class Vote extends HComponent implements Scene, HKeyListener, HActionListener {

	private XletContext ctx;
	private HScene scene;
	private VotingCore votingCore;

	// Scene components
	private static final String QUESTION_LABEL = "Question:";
	private static final String YES_BUTTON_LABEL = "Yes";
	private static final String NO_BUTTON_LABEL = "No";
	private static final String BACK_BUTTON_LABEL = "Back";
	private HStaticText questionLabel;
	private HTextButton yesButton;
	private HTextButton noButton;
	private HTextButton backButton;
	
	// communication
	private RestClient rc;
	private String voteResource = "/questions/vote";
	private String questionResource = "/questions/current_question";
	
	// JSON Parser
	private JSONParser parser;
	
	// Misc
	private String question;

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

		try {
			this.question = getCurrentQuestion();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		questionLabel = new HStaticText(QUESTION_LABEL+" "+this.question+"", 10, 450, 400, 40);
		questionLabel.setBordersEnabled(false);
		questionLabel.setHorizontalAlignment(HVisible.HALIGN_LEFT);
		questionLabel.setForeground(Color.white);
		questionLabel.setFont(new Font("Tiresias", Font.BOLD, 24));

		yesButton = new HTextButton(YES_BUTTON_LABEL, 30, 493, 100, 40);
		yesButton.setTextContent(YES_BUTTON_LABEL, HVisible.FOCUSED_STATE);
		yesButton.setBordersEnabled(true);
		yesButton.setForeground(Color.white);
		yesButton.setBackground(Color.green);
		yesButton.setBackgroundMode(HVisible.BACKGROUND_FILL);
		yesButton.setFont(new Font("Tiresias", Font.BOLD, 24));
		
		noButton = new HTextButton(NO_BUTTON_LABEL, 150, 493, 100, 40);
		noButton.setTextContent(NO_BUTTON_LABEL, HVisible.FOCUSED_STATE);
		noButton.setBordersEnabled(true);
		noButton.setForeground(Color.black);
		noButton.setBackground(Color.yellow);
		noButton.setBackgroundMode(HVisible.BACKGROUND_FILL);
		noButton.setFont(new Font("Tiresias", Font.BOLD, 24));
		
		backButton = new HTextButton(BACK_BUTTON_LABEL, 590, 493, 100, 40);
		backButton.setTextContent(BACK_BUTTON_LABEL, HVisible.FOCUSED_STATE);
		backButton.setBordersEnabled(true);
		backButton.setForeground(Color.white);
		backButton.setBackground(Color.red);
		backButton.setBackgroundMode(HVisible.BACKGROUND_FILL);
		backButton.setFont(new Font("Tiresias", Font.BOLD, 24));

		scene.add(questionLabel);
		scene.add(yesButton);
		scene.add(noButton);
		scene.add(backButton);
		scene.addKeyListener(this);
		
		// It adds actionPerformed method
		yesButton.addHActionListener(this);
		noButton.addHActionListener(this);
		backButton.addHActionListener(this);
		
		// It adds all necessary KeyListener
		yesButton.addKeyListener(this);
		noButton.addKeyListener(this);
		backButton.addKeyListener(this);
		
		// It configures traversal navigation
		yesButton.setFocusTraversal(backButton, noButton, backButton, noButton);
		noButton.setFocusTraversal(yesButton, backButton, yesButton, backButton);
		backButton.setFocusTraversal(noButton, yesButton, noButton, yesButton);
		
		yesButton.requestFocus();
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
			case VK_GREEN:
				vote("affirmative");
				change_scene("Dashboard");
				break;
			case VK_YELLOW:
				vote("negative");
				change_scene("Dashboard");
				break;
			case VK_RED:
				change_scene("Dashboard");
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
		case 'Y':
			vote("affirmative");
			change_scene("Dashboard");
			break;
		case 'N':
			vote("negative");
			change_scene("Dashboard");
			break;
		case 'B':
			change_scene("Dashboard");
			break;
		default:
			break;
		}	
	}
	
	private String getCurrentQuestion() throws IOException{
		String data = "";
		return rc.post("http://"+Layout.server_domain+questionResource, data);
	}
	
	private void vote(String vote){
		try {
			rc.post("http://"+Layout.server_domain+voteResource, "vote="+vote);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setLogMsg(String msg) {
		System.out.println("LOG SCENE(" + votingCore.getSceneName() + "): "+ msg);
	}

}
