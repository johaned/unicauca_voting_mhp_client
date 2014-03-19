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

public class Results extends HComponent implements Scene, HKeyListener, HActionListener {

	private XletContext ctx;
	private HScene scene;
	private VotingCore votingCore;

	// Scene components
	private static final String RESULTS_LABEL = "Results:";
	private static final String BACK_BUTTON_LABEL = "Back";
	private HStaticText resultsLabel;
	private HTextButton backButton;
	
	// communication
	private RestClient rc;
	private String resultsResource = "/questions/results";
	
	// JSON Parser
	private JSONParser parser;
	
	// Misc
	private String results;
	private boolean hasResults = false;
	private float affirmativeResponses;
	private float negativeResponses;
	private float maxWidth = 500;
	

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
        JSONObject objectResponse = null;
		try {
			objectResponse = getResults();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		if(objectResponse!=null){
			affirmativeResponses = Float.parseFloat(objectResponse.get("affirmative_responses_percentage").toString());
			negativeResponses = Float.parseFloat(objectResponse.get("negative_responses_percentage").toString());
			this.results = "Total: "+objectResponse.get("total_reponses").toString()+" Affirmative-> "+objectResponse.get("affirmative_responses_percentage_caption").toString()+" Negative-> "+objectResponse.get("negative_responses_percentage_caption").toString();
			this.hasResults = true;
		}else{
			this.results = "No results";
		}
		
		resultsLabel = new HStaticText(RESULTS_LABEL+" "+this.results+"", 48, 420, 670, 40);
		resultsLabel.setBordersEnabled(false);
		resultsLabel.setHorizontalAlignment(HVisible.HALIGN_LEFT);
		resultsLabel.setForeground(Color.white);
		resultsLabel.setFont(new Font("Tiresias", Font.BOLD, 24));
		
		backButton = new HTextButton(BACK_BUTTON_LABEL, 580, 463, 100, 40);
		backButton.setTextContent(BACK_BUTTON_LABEL, HVisible.FOCUSED_STATE);
		backButton.setBordersEnabled(true);
		backButton.setForeground(Color.white);
		backButton.setBackground(Color.red);
		backButton.setBackgroundMode(HVisible.BACKGROUND_FILL);
		backButton.setFont(new Font("Tiresias", Font.BOLD, 24));

		scene.add(resultsLabel);
		scene.add(backButton);
		scene.addKeyListener(this);
		
		// It adds actionPerformed method
		backButton.addHActionListener(this);
		
		// It adds all necessary KeyListener
		backButton.addKeyListener(this);
		
		
		backButton.requestFocus();
	}
	
	public void paint(Graphics graphics){
		graphics.setColor(new DVBColor(255, 255, 255, 255));
		graphics.drawLine(0, 418, 720, 418);
		
		if(hasResults){
			graphics.setColor(Color.green);		
	        graphics.fillRoundRect(60, 455, Math.round(this.maxWidth*this.affirmativeResponses), 40, 28, 28);
	        graphics.setColor(Color.red);
	        graphics.fillRoundRect(60, 500, Math.round(this.maxWidth*this.negativeResponses), 40, 28, 28);	
		}
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
		case 'B':
			change_scene("Dashboard");
			break;
		default:
			break;
		}	
	}
	
	private JSONObject getResults() throws IOException, ParseException{
		String data = "";
		return (JSONObject) parser.parse(rc.post("http://"+Layout.server_domain+resultsResource, data));
	}
	

	public void setLogMsg(String msg) {
		System.out.println("LOG SCENE(" + votingCore.getSceneName() + "): "+ msg);
	}

}
