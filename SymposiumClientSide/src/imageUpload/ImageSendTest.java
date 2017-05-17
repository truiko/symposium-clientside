package imageUpload;

import javax.swing.JFileChooser;

public class ImageSendTest {

	public static void main(String[] args) {
		final JFileChooser fc = new JFileChooser();
		int returnVal = fc.showOpenDialog(fc);
		String filePath = null;
		if(returnVal == JFileChooser.APPROVE_OPTION){
			filePath = fc.getSelectedFile().getAbsolutePath();
		}else{
			System.out.println("User clicked CANCEL");
			System.exit(1);
		}
		new ImageSender(filePath);
	}

}
