import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

public class SignalHandler {

	public static Functor sinus440 = new Sinus(440);
	public static Functor sinus80 = new Sinus(80);
	public static Functor sinus330 = new Sinus(330);
	

	public static void main(String[] args) throws IOException, UnsupportedAudioFileException {
		/*(SoundSignal.createSound(sinus440, 5, 44100)).exportSignal("base_sinus_mono_non_bruite.wav", true);
		/*(SoundSignal.createSound(sinus80, 5, 128000)).exportSignal("test80.wav", true);
		(SoundSignal.createSound(sinus330, 20, 192000)).exportSignal("test330.wav", true);
		*/
		short[] sig = new short[192000 * 30];
		for(int i = 0 ; i < 30 * 3 ; i++){
			for(int j = 0 ; j < 10 ; j++){
				sig[i*192000/3 + j] = Short.MAX_VALUE;
			}
		}
		SoundSignal s = new SoundSignal();
		s.setSignal(sig, 192000);
		s.exportSignal("clac_3_par_sec_192Khz.wav", true);
		
	} 
}
