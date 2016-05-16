import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

public class SignalHandler {

	public static Functor sinus440 = new Sinus(440);
	public static Functor sinus80 = new Sinus(80);
	public static Functor sinus330 = new Sinus(330);
	

	public static void main(String[] args) throws IOException, UnsupportedAudioFileException {
		/*
		(SoundSignal.createSound(sinus440, 5, 44100)).exportSignal("base_sinus_mono_non_bruite.wav", true);
		(SoundSignal.createSound(sinus80, 5, 128000)).exportSignal("test80.wav", true);
		(SoundSignal.createSound(sinus330, 20, 192000)).exportSignal("test330.wav", true);
		*/
		int samplingRate = 44100;
		int duration = 30;
		int nb_clac_par_sec = 10;
		int nb_samples = 5;
		short[] sig = new short[samplingRate * duration];
		for(int i = 0 ; i < duration * nb_clac_par_sec ; i++){
			for(int j = 0 ; j < nb_samples ; j++){
				sig[i*samplingRate/nb_clac_par_sec + j] = Short.MAX_VALUE;
			}
		}
		SoundSignal s = new SoundSignal();
		s.setSignal(sig, samplingRate);
		s.exportSignal("35min-10_par_sec_44.1Khz.wav", true);
		/**/
	} 
}
