# generate wav file containing sine waves
# FB36 - 20120617
import math, wave, array
import sys, getopt

duration = 3  # seconds
freq = 440  # of cycles per second (Hz) (frequency of the sine waves)
volume = 100  # percent
data = array.array('h')  # signed short integer (-32768 to 32767) data
sampleRate = 44100  # of samples per second (standard)
numChan = 1  # of channels (1: mono, 2: stereo)
dataSize = 2  # 2 bytes because of using signed short integers => bit depth = 16
numSamplesPerCyc = int(sampleRate / freq)
numSamples = sampleRate * duration


def main(argv):
    shortOpts = "'f:F:d:s:"
    longOpts = ['formula=', 'frequency=', 'samplerate=', 'duration=']
    try:
        opts, args = getopt.getopt(argv, shortOpts, longOpts)
    except getopt.GetoptError as e:
        print(e.msg)
        print('wav.py -f <formula> [-F <frequency> -d <duration> -s <sampleRate>]')
        sys.exit(2)

    for opt, arg in opts:
        if opt == '-h':
            print('wav.py -f <formula> [-F <frequency> -d <duration> -s <sampleRate>] \n')
            print('The letter \'x\' must be used as the variable letter')
            sys.exit()
        elif opt in('-f', '--formula'):
            global formula
            formula = arg
        elif opt in ('-F', '--frequency'):
            global freq
            freq = arg
        elif opt in ('-s', '--samplerate'):
            global sampleRate
            sampleRate = opt
        elif opt in ('-d', '--duration'):
            global duration
            duration = arg

    print('Parameters : \n')
    print('- duration : ' + str(duration) + '\n')
    print('- frequency : ' + str(freq) + '\n')
    print('- sample rate : ' + str(sampleRate) + '\n')

    formula = formula.replace("sin", "math.sin")
    formula = formula.replace("cos", "math.cos")
    formula = formula.replace("tan", "math.tan")
    formula = formula.replace("abs", "math.abs")
    formula = formula.replace("pi", "math.pi")
    print(formula)

    for i in range(numSamples):
        x = (i % numSamplesPerCyc) / numSamplesPerCyc
        sample = 32767 * float(volume) / 100
        sample *= eval(formula)
        data.append(int(sample))

    f = wave.open('SineWave_' + str(freq) + 'Hz.wav', 'w')
    f.setparams((numChan, dataSize, sampleRate, numSamples, "NONE", "Uncompressed"))
    f.writeframes(data.tostring())
    f.close()

    k=input("press close to exit")

if __name__ == "__main__":
   main(sys.argv[1:])






