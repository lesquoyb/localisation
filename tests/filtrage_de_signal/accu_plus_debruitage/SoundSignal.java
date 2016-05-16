
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import javax.sound.sampled.*;

//import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;


import fft.FFT;


/**
 * Classe de gestion d'un signal audio
 * @author Extrait de plusieurs classes
 */

public class SoundSignal {

	private AudioInputStream stream;
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
	public static AudioFormat defFormat = new AudioFormat(

			(float) 22050, 16, 1, true, false);


	/**
	 * Lit un fichier wav
	 * @param file fichier contenant le signal wav
	 * @throws UnsupportedAudioFileException
	 * @throws IOException
	 */
	public void setSignal(String file) throws UnsupportedAudioFileException,
	IOException {
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
			if (!stream.getFormat().isBigEndian())
				bb.order(ByteOrder.LITTLE_ENDIAN);
			else
				bb.order(ByteOrder.BIG_ENDIAN);
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

		/*signal = new short[newsignal.length];
	for(int i=0;i<newsignal.length;i++)
	    signal[i]=newsignal[i];
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
	public short getSignalSample(int i)
	{
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
	private void writeShort(DataOutputStream out, int arg0) throws IOException 
	{
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
	public void exportSignal(String output,boolean header) throws IOException
	{

		DataOutputStream data = new DataOutputStream(new FileOutputStream(output)) ;

		if (header) 
		{
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

		for (int i = 0 ; i < this.getSignalLength() ; i++)
			this.writeShort(data, signal[i]) ;

		data.close() ;
	}






	double[] sig;

	final int fftOrder = 1024;


	void debruitage(int alpha, int beta, int gamma){
				
		int window_offset = (int) (0.008 * samplingFrequency);
		int window_size = (int) (0.032 * samplingFrequency);
		
		sig = new double[signal.length];
		
		double sommeMoyenne = 0.;
		double denomMoyen = 0.;
		double bruitMoyen = 0.;
		final int nbSpectresMoyen = 5;
		
		for(int i = 0 ; i < signal.length - window_size ; i += window_offset){
			double[] hamming = hamming(i, window_size);
			for(int j = 0 ; j < window_size ; j ++){
				sig[i + j] += (signal[ i + j ] * hamming[j]);
			}
			
			FFT fft = new FFT(fftOrder); 
			double[] x = new double [fftOrder*2];
			
			for (int j = 0 ; j < fftOrder ; j++){
				if(j < window_size)
					x[j*2] = sig[i + j]; //signal fenétré(attention à la taille de signal_fen)
				else
					x[j*2] = 0; //signal fenétré(attention à la taille de signal_fen)
			
				x[( j)*2+1] = 0;
			}
			
			//Réalisation de la transformée de Fourrier
			fft.transform(x, false);
			double[] ampl = spectreAmplitude(x);
			double[] phase = spectre_phase(x);
			
			//Mise � jour du bruit moyen
			if(i/window_offset < nbSpectresMoyen){
				for(int j = 0 ; j < ampl.length ; j++){
					sommeMoyenne += ampl[j];
				}
				denomMoyen += ampl.length;
				bruitMoyen = sommeMoyenne / denomMoyen;
			}
		
			//On effectue la soustraction spectrale
			for(int j = 0 ; j < fftOrder ; j++){
				double diff =  			 Math.pow(ampl[j], alpha)
								- beta * Math.pow(bruitMoyen, alpha);
				if(diff > 0){
					ampl[j] = Math.pow(diff, 1.0/alpha);
				}
				else{
					ampl[j] = gamma * bruitMoyen;
				}
			}
			
			
			
			x = spectrereconstruction(ampl, phase);
			
			//Réalisation de la transformée de Fourrier Inverse
			fft.transform(x,true); //le signal fenêtré final se trouve en x[i*2]
			
			for(int j = 0 ; j < fftOrder ; j++){
				if(j < window_size){
					sig[i + j] = x[j*2];
				}
			}
		}
		reconstructSig();
	}
	
	
	double[] spectre_phase(double[] x_fourier){
		double ret[] = new double[x_fourier.length/2];
		for(int i = 0 ; i < x_fourier.length ; i+=2){
			ret[i/2] = Math.atan2(x_fourier[i+1], x_fourier[i]);
		}
		return ret;
		
	}
	
	double [] spectrereconstruction(double[] spectre_amplitude, double[] spectre_phase){
		double[] ret = new double[spectre_amplitude.length*2];
		for(int i = 0 ; i < spectre_amplitude.length ; i++){
			ret[i*2]   = spectre_amplitude[i] * Math.cos(spectre_phase[i]);
			ret[i*2+1] = spectre_amplitude[i] * Math.sin(spectre_phase[i]);
		}
		return ret;
		
	}
	
	
	double[] spectreAmplitude(double[] x_fourier){
		double ret[] = new double[x_fourier.length/2];
		for(int i = 0 ; i < x_fourier.length ; i+=2){
			ret[i/2] = Math.sqrt(		x_fourier[i] * x_fourier[i] 
								+ 	x_fourier[i+1] * x_fourier[i+1]);
		}		
		return ret;
	}

	void reconstructSig(){
		short[] tmp = new short[sig.length];
		for(int i = 0 ; i < sig.length ; i++){
			tmp[i] = (short)(sig[i]/2.16);
		}

		setSignal(tmp, samplingFrequency);		
	}

	double[] hamming(int start, int size){
		double c1 = (double) 0.54 ;
		double c2 = (double) 0.46 ;
		double[] hamming = new double[size] ;
		for (int i = 0 ; i < size ; i++){
			hamming[i] = c1 - (c2 * Math.cos((double) 2 * Math.PI * i / (size-1))) ;
		}
		return hamming;
	}
	
	
	public SoundSignal filtrage_par_superposition_de_periodes(int nb_samples){
		SoundSignal ret = new SoundSignal();
		long[] sig = new long[nb_samples];
		Long max = Long.MIN_VALUE,
				   min = Long.MAX_VALUE;
		for(int i = 0 ; i < signal.length  ; i++){
			/*if(sig[i%nb_samples] <  + signal[i] && signal[i] > 0){ 
				System.out.println("overflow");
				break;
			}
			*/
			//sig[i%nb_samples].add(BigInteger.valueOf(signal[i]));
			sig[i%nb_samples] += signal[i];
			if(i > signal.length-nb_samples -1){
				max = (max > sig[i%nb_samples]) ? max : sig[i%nb_samples];
				min = (min < sig[i%nb_samples]) ? min : sig[i%nb_samples];
			}
		}

		double factor = (double)(max-min)/(Short.MAX_VALUE-Short.MIN_VALUE);
		short[] retSig = new short[nb_samples];
		for(int i = 0 ; i < sig.length ; i++){
			//if(i > 8 && i < 64)	System.out.println(min + " "  + factor+ " " + sig[i] + " " +(sig[i] - min ) / factor);
			retSig[i] =(short)( (sig[i] - min ) / factor );
		}
		ret.setSignal(retSig, samplingFrequency);
		return ret;
	}

	
	public void filtrage_frequence(int frequence, int marge){
			  short value = signal[0]; 
			  final int smoothing = 10;
			  for (int i = 1; i < signal.length; i++){
			    short currentValue = signal[i];
			    value += (currentValue - value) / smoothing;
			    signal[i] = value;
			  }
			
		/*
		int window_offset = (int) (0.008 * samplingFrequency);
		int window_size = (int) (0.032 * samplingFrequency);
		
		sig = new double[signal.length];
		
		for(int i = 0 ; i < signal.length - window_size ; i += window_offset){
			double[] hamming = hamming(i, window_size);
			for(int j = 0 ; j < window_size ; j ++){
				sig[i + j] += (signal[ i + j ] * hamming[j]);
			}
			
			FFT fft = new FFT(fftOrder); 
			double[] x = new double [fftOrder*2];
			
			for (int j = 0 ; j < fftOrder ; j++){
				if(j < window_size)
					x[j*2] = sig[i + j]; 
				else
					x[j*2] = 0; 
			
				x[( j)*2+1] = 0;
			}
			
			fft.transform(x, false);
			int c = 0;
			for(int j = 0 ; j < x.length ; j +=2){
				if( ! (x[j]*samplingFrequency/fftOrder >= frequence - marge) || !(x[j]*samplingFrequency/fftOrder <= frequence + marge) ){
					//x[j] = 0;
					x[j+1] /= samplingFrequency;
				}
				else{
					c++;
				}
			}
			//System.out.println(c);
			
			//Réalisation de la transformée de Fourrier Inverse
			fft.transform(x,true); //le signal fenêtré final se trouve en x[i*2]
			
			for(int j = 0 ; j < fftOrder ; j++){
				if(j < window_size){
					sig[i + j] = x[j*2];
				}
			}
		}
		reconstructSig();
		*/
	
	}
	
	
	
	

}



