
public class Sinus implements Functor{

	
	public double frequency;
	
	Sinus(int frequency){
		this.frequency = (double)frequency;
	}
	
	@Override
	public short function(int nb_sample, int fileFrequency) {
		return (short)(	  Math.sin( nb_sample * frequency / fileFrequency * Math.PI * 2 )
						* Short.MAX_VALUE);
	}
	
	

}
