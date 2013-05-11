import java.awt.EventQueue;
import java.awt.Image;
import java.awt.Toolkit;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;


public class Meow {

	private final JFrame frame;
	private Image catGif;

	public Meow() {
		frame = new JFrame("Zoe kitteh frame :3");
		try {
			catGif = Toolkit.getDefaultToolkit().getImage(new URL("http://25.media.tumblr.com/883094714a174b9c944c29b01e7830c7/tumblr_mjiysc819L1s1f4sxo1_250.gif"));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(catGif != null) {
			frame.getContentPane().add(new JLabel(new ImageIcon(catGif)));
		}
		frame.pack();
		frame.setVisible(true);
	}

	public static String everyNth(String str, int n) {
		final char[] arr = str.toCharArray();
		final StringBuilder sb = new StringBuilder();
		for(int i = 0; i < arr.length; i += n) {
			sb.append(arr[i]);
		}
		return sb.toString();
	}


	public static void main(String[] args) {
		System.out.println(everyNth("Miracle", 2));
//		EventQueue.invokeLater(new Runnable() {
//
//			@Override
//			public void run() {
//				new Meow();
//
//			}
//
//		});
	} 

}
