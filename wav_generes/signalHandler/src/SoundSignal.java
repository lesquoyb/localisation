
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import javax.sound.sampled.*;

//import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Classe de gestion d'un signal audio
 * @author Extrait de plusieurs classes
 */



public class SoundSignal {
	
	
	
	private short[] signal;
	private int samplingFrequency;

	/**
	 * Constructeur du lecteur audio
	 */
	public SoundSignal() {
		signal = null;
	}

	/**
	 * AudioFormat.Encoding.PCM_SIGNED
	 */
	public static AudioFormat defFormat = new AudioFormat((float) 22050, 16, 1, true, false);


	/**
	 * Lit un fichier wav
	 * @param file fichier contenant le signal wav
	 * @throws UnsupportedAudioFileException
	 * @throws IOException
	 */
	public void setSignal(String file) throws UnsupportedAudioFileException,IOException {

		AudioInputStream stream;
		
		if (file != null) {
			stream = AudioSystem.getAudioInputStream(new File(file));
			this.samplingFrequency = (int)stream.getFormat().getSampleRate(); 
			int lenByte = (int) (stream.getFrameLength() * stream.getFormat().getFrameSize());
			byte[] signalByBytes = new byte[lenByte];
			int off = 0;
			int len = stream.getFormat().getFrameSize();
			boolean doneReading = false;
			while (!doneReading) {
				int nb = stream.read(signalByBytes, off, len);
				if (nb == -1) {
					doneReading = true;
				} else {
					off += nb;
				}
			}
			ByteBuffer bb = ByteBuffer.wrap(signalByBytes);
			if (!stream.getFormat().isBigEndian()){
				bb.order(ByteOrder.LITTLE_ENDIAN);
			}
			else{
				bb.order(ByteOrder.BIG_ENDIAN);
			}
			ShortBuffer sb = bb.asShortBuffer();
			int limit = sb.remaining();
			signal = new short[limit];
			sb.get(signal);

			stream.close();
		}
	}

	/**
	 * Lit un tableau de short
	 * @param  tableau (short) contenant un signal
	 * @param  frequence d'echantillonnage
	 */
	public void setSignal(short [] newsignal, int samplingFrequency ){

		/*signal = new short[newsignal.length];																																																																																																																																																																																																																																																							        for(int i=0;i<newsignal.length;i++)																																																																																																																																																																																																																																																            signal[i]=newsignal[i];
		 */
		signal = newsignal;
		this.samplingFrequency = samplingFrequency;
	}


	/**
	 * Retourne le signal lu sous forme d'un tableau de short
	 * @return un short[] tableau représentant le signal
	 */
	public short[] getSignal() {
		return signal;
	}

	/**
	 * Retourne l'echantillon i du signal lu
	 * @return short
	 */
	public short getSignalSample(int i) {
		return signal[i];
	}

	/**
	 * Retourne la longueur du signal en échantillons
	 * @return un int longueur du signal en échantillons
	 */
	public int getSignalLength() {
		return signal.length;
	}

	/**
	 * Retourne la frequence d'echantillonnage du signal
	 * @return un int pour le frequence d'echantillonnage
	 */
	public int getSamplingFrequency() {
		return samplingFrequency;
	}

	/**
	 * Write in LittleEndian a short
	 * 
	 */
	private void writeShort(DataOutputStream out, int arg0) throws IOException {
		out.writeByte(arg0 & 0xff);
		out.writeByte((arg0 >> 8) & 0xff);
	}

	/**
	 * Write in LittleEndian an int
	 * 
	 */
	private void writeInt(DataOutputStream out,int arg0) throws IOException {
		out.writeByte(arg0 & 0xff);
		out.writeByte((arg0 >> 8) & 0xff);
		out.writeByte((arg0 >> 16) & 0xff);
		out.writeByte((arg0 >> 24) & 0xff);
	}

	/**
	 * Export a signal (array) in a sound file.
	 * @param output the sound file name for output
	 * @param header true if wav file, else raw file (wav file : 16KHz, 16bits, Mono)
	 * @throws IOException if problem with output file
	 */
	public void exportSignal(String output,boolean header) throws IOException{
		DataOutputStream data = new DataOutputStream(new FileOutputStream(output)) ;

		if (header) {
			int sizeHeader = 44 ;
			//WRITE HEADER
			data.write("RIFF".getBytes()) ; //4
			this.writeInt(data, (int)sizeHeader - 8 + signal.length*2) ; //8
			data.write("WAVE".getBytes()); //12
			data.write("fmt ".getBytes()); //16
			this.writeInt(data, (int)16); //20
			this.writeShort(data, (short)1) ; //22 PCM
			this.writeShort(data, (short)1) ; //24 NUMCHANEL CANAUX 1=MONO
			this.writeInt(data, (int)this.getSamplingFrequency()) ; //28 SAMPLERATE (FREQUENCE)
			this.writeInt(data, (int)(this.getSamplingFrequency()*2)) ; //32 BYTERATE nb octets par seconde (=SAMPLERATE*NUMCHANEL*BITPERSAMPLE/8)
			this.writeShort(data, (short)2) ; //34 BLOCKALIGN NB octet by frame (=NUMCHANEL * BITPERSAMPLE/8)
			this.writeShort(data, (short)16) ; //36 BITEPERSAMPLE nb octet par sample
			data.write("data".getBytes()); //40
			this.writeInt(data, (int)this.getSignalLength()*2) ; //44
		}

		for (int i = 0 ; i < this.getSignalLength() ; i++){
			this.writeShort(data, signal[i]) ;
		}

		data.close() ;
	}

	/**
	 * Crée un SoundSignal d'une durée s_duration secondes à une fréquence sampligFrequency dont le signal correspond à l'application de f.function sur chaque sample
	 * @param f
	 * @param s_duration
	 * @param samplingFrequency
	 * @return
	 */
	public static  SoundSignal createSound(Functor f, int s_duration, int samplingFrequency){
		SoundSignal ret = new SoundSignal();
		short[] signal = new short[s_duration * samplingFrequency];
		for(int i = 0 ; i <  s_duration * samplingFrequency; i++){
			signal[i] = f.function(i, samplingFrequency);
		}
		ret.setSignal(signal, samplingFrequency);
		return ret;
	}

	double [] signalEnergy(int m, int N){
		int nb_echantillons = (signal.length - N) / m;

		double[] signalEnergy = new double [nb_echantillons];
		for (int i = 0; i < nb_echantillons; i++) {
			signalEnergy[i] = energy(i*m, N);
		}
		return signalEnergy;
	}

	double energy(int instant_echt, int N){
		double energy = 0;
		for (int i = 0; i < N; i++){
			energy += Math.pow(getSignalSample(instant_echt + i), 2);
		}
		energy /= (N+1);
		return 10* Math.log10(energy);
	}
}






