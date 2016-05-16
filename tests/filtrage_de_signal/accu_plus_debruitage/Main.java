import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

public class Main {

	public static void main(String... args) {
		try {
			SoundSignal signal = new SoundSignal();
			String[] dirs = {"18secs-3clacs_par_sec", 
							 "30secs-10clacs_par_sec",
							 "35secs-5clacs_par_sec"};
			int[] nb_samples = {14354,
								4307,
								8163
			};
			
			String[] files = {"temoin.wav", "musique_forte.wav", "musique_tres_forte.wav"};
			
			int i = 0;
			for(String dir : dirs){
				for(String file : files){
					signal.setSignal("input/"+dir + "/" + file);
					signal.debruitage(1, 400, 0);
					signal.filtrage_par_superposition_de_periodes(nb_samples[i]).exportSignal("output/"+dir+"/"+file, true);
				}
				i++;
			}
		
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
	}

}
