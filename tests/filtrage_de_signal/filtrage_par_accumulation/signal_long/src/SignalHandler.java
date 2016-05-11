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
		SoundSignal s = new SoundSignal();
		//s.setSignal("base_clac_bruite.wav");
		s.setSignal("testP3v2_dos.wav");
		s.filtrage_par_superposition_de_periodes(44100/3)
			.exportSignal("filtrage/testP3v2_dos.wav", true);
			
		s.setSignal("testP3v2_dos_bruite.wav");
		s.filtrage_par_superposition_de_periodes(14355)
			.exportSignal("filtrage/testP3v2_dos_bruite.wav", true);
		
		
		s.setSignal("testP3v2_dos_musique.wav");
		s.filtrage_par_superposition_de_periodes(14355)
			.exportSignal("filtrage/testP3v2_dos_musique.wav", true);
		
		
	} 
}
