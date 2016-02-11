freqOfTone = 12000; %audacity is showing freq = 2100hz (approx)
samplingFreq = 44100;
duration = 100; %the file properties is showing duration of 5s
t=[0: 1/samplingFreq: duration];
y=sin(2*pi*freqOfTone*t)';
wavwrite(y,samplingFreq ,'temp.wav')