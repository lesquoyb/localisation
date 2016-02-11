	
#include <sndfile.hh>
#include <iostream>
#include <cmath>
int main()
{
	const int format=SF_FORMAT_WAV | SF_FORMAT_PCM_16;
	//  const int format=SF_FORMAT_WAV | SF_FORMAT_FLOAT;
	const int channels=1;
	const int sampleRate=48000;
	const char* outfilename="foo.wav";
			 
	SndfileHandle outfile(outfilename, SFM_WRITE, format, channels, sampleRate);

	if (not outfile) return -1;
					  
	// prepare a 3 seconds buffer and write it
	const int size = sampleRate*3;
	float sample[size];
	float current=0.;
	for (int i=0; i<size; i++) sample[i]=sin(float(i)/size*M_PI*1500);
	outfile.write(&sample[0], size);
	return 0;
}
