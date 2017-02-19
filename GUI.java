import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class GUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private JProgressBar progressBar;

	public GUI(String titleString) throws InterruptedException {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		JLabel label = new JLabel(" -- STATUS --");
		panel.add(label, BorderLayout.SOUTH);

		JPanel panBar = new JPanel();
		progressBar = new JProgressBar(0, 100);
		panBar.add(progressBar, BorderLayout.CENTER);

		progressBar.setValue(0);

		panel.add(panBar, BorderLayout.CENTER);
		this.setVisible(true);
		this.setTitle(titleString);
		this.setSize(400, 85);
		this.setLocation(500, 400);
		this.getContentPane().add(panel);

	}

	public void refresh(double perc) {
		progressBar.setValue((int) perc);
		progressBar.setString(String.valueOf(Math.round(perc)) + "%");
		progressBar.setStringPainted(true);
	}
	
	public void exit() {
		this.setVisible(false);
	}

}
