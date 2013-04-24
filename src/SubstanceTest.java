import java.awt.Graphics;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Arrays;
import java.util.List;

import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.kenneh.core.api.framework.KScript;
import org.powerbot.core.script.Script;
import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.methods.Environment;

@Manifest(authors = { "Kenneh" }, description = "asdf", name = "asdf")
public class SubstanceTest extends KScript implements Script {

	private final String JAR_FILES[] = {
			"substance-7.2.1.jar", "laf-plugin-7.2.1.jar", "laf-widget-7.2.1.jar", "trident-7.2.1.jar"
	};
	
	private File SAVE_DIR;
	private final String URL_HOST = "https://dl.dropboxusercontent.com/u/9359719/laf/";
	
	private LookAndFeel def;

	private void downloadFile(String file) throws Exception {
		URL website = new URL(URL_HOST + file);
	    ReadableByteChannel rbc = Channels.newChannel(website.openStream());
	    FileOutputStream fos = new FileOutputStream(SAVE_DIR.getAbsolutePath() + File.separator + file);
	    fos.getChannel().transferFrom(rbc, 0, 1 << 24);
	}
	
	@Override
	public boolean init() {
		SAVE_DIR = Environment.getStorageDirectory();
		def = UIManager.getLookAndFeel();
		final List<String> contents = Arrays.asList(SAVE_DIR.list());
		for(String file : JAR_FILES) {
			if(!contents.contains(file)) {
				log.info(file + " not found! Attempting to download.");
				try {
					downloadFile(file);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}

	@Override
	public void close() {
		try {
			UIManager.setLookAndFeel(def);
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void paint(Graphics g) {

	}

}
