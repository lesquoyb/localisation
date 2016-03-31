import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

public class SignalHandler {

	public static Functor sinus440 = new Sinus(440);
	public static Functor sinus80 = new Sinus(80);
	public static Functor sinus330 = new Sinus(330);
	

	public static void main(String[] args) throws IOException {
		(SoundSignal.createSound(sinus440, 5, 44100)).exportSignal("test440.wav", true);
		(SoundSignal.createSound(sinus80, 5, 128000)).exportSignal("test80.wav", true);
		(SoundSignal.createSound(sinus330, 20, 192000)).exportSignal("test330.wav", true);
		
		/*
		if (args.length != 1) {
			System.out.println("Usage : SoundSignal <file.wav>");
			System.exit(-1);
		}
		try {
			SoundSignal signal = new SoundSignal();

			signal.setSignal(args[0]);

			System.out.println("Length of the signal (in sample) : "+ signal.getSignalLength());
			System.out.println("Sampling frequency : " + signal.getSamplingFrequency());
			System.out.println("Length of the signal (in ms) : "+ signal.getSignalLength() * 1000 / signal.getSamplingFrequency());
			//Test d'ecriture d'un signal dans un fichier wav                        
			signal.exportSignal("essai_output.wav", true);
			System.out.println("The file essai_output.wav has been created");

			//Test d'un deuxième signal par recopie du premier
			SoundSignal signal2 = new SoundSignal();
			signal2.setSignal(signal.getSignal(), signal.getSamplingFrequency());
			signal2.exportSignal("essai_output2.wav", true);
			System.out.println("The file essai_output2.wav has been created");

			double[] test = signal.signalEnergy((int)(signal.getSamplingFrequency() * 0.001), (int)(signal.getSamplingFrequency()*0.01));
			for (int i=0; i < test.length; i++){
				System.out.println((int)(test[i]*1000));
			}

		} catch (IOException e) {
			System.err.println("error cannot read file <" + args[0] + ">");

		} catch (UnsupportedAudioFileException e) {
			System.err.println("error bad sound file <" + args[0] + ">");
		}*/
	}
	
}
