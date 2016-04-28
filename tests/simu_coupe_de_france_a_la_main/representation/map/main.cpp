#include <iostream>
#include <math.h>
#include "tgaimage.h"
#include <limits>
#include <time.h>
#include <assert.h>
#include <sstream>

using namespace std;

const int   width = 240,
            height = 175,
            white = 255,
            black = 0,
            samplingRate = 44100,
            speed_of_sound = 34000,
            xH1 = 0,
            yH1 = 88,
            xH2 = 240,
            yH2 = 0,
            xH3 = 240,
            yH3 = 175,
            magic_factor = 1;



int eval(int xbase, int ybase, int i2, int j2, int decalage1, int decalage2, int decalage3){

    int dist_base1 = sqrt((xH1 - xbase) *(xH1 - xbase)  + (yH1 - ybase) * (yH1 - ybase));
    int dist_micro1 = sqrt((xH1 - i2) *(xH1 - i2)  + (yH1 - j2) * (yH1 - j2));

    int dist_base2 = sqrt((xH2 - xbase) *(xH2 - xbase)  + (yH2 - ybase) * (yH2 - ybase));
    int dist_micro2 = sqrt((xH2 - i2) *(xH2 - i2)  + (yH2 - j2) * (yH2 - j2));

    int dist_base3 = sqrt((xH3 - xbase) *(xH3 - xbase)  + (yH3 - ybase) * (yH3 - ybase));
    int dist_micro3 = sqrt((xH3 - i2) *(xH3 - i2)  + (yH3 - j2) * (yH3 - j2));


    int diff1 = dist_base1 - dist_micro1;

    int diff2 = dist_base2 - dist_micro2;

    int diff3 = dist_base3 - dist_micro3;

    return     abs(diff1 - decalage1)
             * abs(diff2 - decalage2)
             * abs(diff3 - decalage3);

}

int decalage(int xbase, int ybase, int xH, int yH){

}

class terrain{
public:
 int    xM1, yM1,
        xM2, yM2,
        xH1, yH1,
        xH2, yH2,
        xH3, yH3,
        decal1,
        decal2,
        decal3;

    TGAImage to_tga(string name)const{
        TGAImage img(width, height, TGAImage::RGB);
        int minv = 999;
        for(int i = 0 ; i < width ; i++){
            for(int j = 0 ; j < height ; j++){
                int val = eval(i, j,xM1, yM1, decal1, decal2, decal3);
                img.set(i,j, TGAColor((unsigned char) max(min(val, white), black), (unsigned char) max(min(val, white), black), (unsigned char) max(min(val, white), black), 1));
            }
            cout << endl;
        }

        img.set(xM1, yM1, TGAColor(255, 0, 0, 1) );
        img.set(xM2, yM2, TGAColor(0, 255, 0, 1));
        img.set(xH1, yH1, TGAColor(0, 0, 255, 1));
        img.set(xH2, yH2, TGAColor(0, 0, 255, 1));
        img.set(xH3, yH3, TGAColor(0, 0, 255, 1));
        img.write_tga_file(name.c_str());
        ofstream os;
        os.open(name + ".desc");
        //ostringstream oss;
        os << "M1: "<< xM1 << " " << yM1 << endl
           << "M2: " << xM2 << " " << yM2 << endl
           << "d1: " << decal1 <<endl
            << "d2: " << decal2 << endl
            << "d3: " << decal3 <<endl;
       // os << oss.str();//, oss.str().size());
        os.close();
        return img;
    }


};

terrain generation_terrain_au_pif(){
    terrain t;
    int dist_inter_mic = 22;

    //On pif les micros
    t.xM1 = random() % width;
    t.yM1 = random() % height; //micro 1: pif total

    //on pif les signes
    t.yM2 = random()%2 ? 1: -1;

    t.xM2 = random()%2 ? 1: -1;
    t.xM2 *= random() % dist_inter_mic+1; // entre 0 et 22 puisque 22 cm entre les deux
    t.xM2 += t.xM1;
    int sign = t.yM2;
    for(int i = 0 ; i <= dist_inter_mic ; i++){//on déduit y en mode bourin
        int valy = t.yM1 + sign*i;
        int val = (t.xM2 - t.xM1)*(t.xM2 - t.xM1) + ( valy - t.yM1 )*(valy - t.yM1 );
        if( val >= dist_inter_mic*dist_inter_mic || i == dist_inter_mic){//pas terrible, mais on ne peut pas être précis en fait
            t.yM2 = valy;
            break;
        }
    }

    //on ne pif pas les enceintes parce que quand même, faut pas abuser (mais on le fera surement après)
    t.xH1 = 0;
    t.yH1 = height/2;
    t.xH2 = width-1;
    t.yH2 = 0;
    t.xH3 = width-1;
    t.yH3 = height-1;

    //on déduit les décalages (:O un truc vraiment calculé )
    t.decal1 =  (sqrt( (t.xH1 - t.xM2)*(t.xH1 - t.xM2)  + (t.yH1 - t.yM2)*(t.yH1 - t.yM2) )
                -sqrt( (t.xH1 - t.xM1)*(t.xH1 - t.xM1)  + (t.yH1 - t.yM1)*(t.yH1 - t.yM1) )
                   )*samplingRate/speed_of_sound;

    t.decal2 =      (sqrt( (t.xH2 - t.xM2)*(t.xH2 - t.xM2)  + (t.yH2 - t.yM2)*(t.yH2 - t.yM2) )
                    -sqrt( (t.xH2 - t.xM1)*(t.xH2 - t.xM1)  + (t.yH2 - t.yM1)*(t.yH2 - t.yM1) ))*samplingRate/speed_of_sound;

    t.decal3 =      (sqrt( (t.xH3 - t.xM2)*(t.xH3 - t.xM2)  + (t.yH3 - t.yM2)*(t.yH3 - t.yM2) )
                -sqrt( (t.xH3 - t.xM1)*(t.xH3 - t.xM1)  + (t.yH3 - t.yM1)*(t.yH3 - t.yM1) ))*samplingRate/speed_of_sound;

    return t;
}

int main(int argc, char *argv[]){

    //int** values = (int**) malloc(sizeof(int**) * width);
    srand(time(NULL));
    for(int i = 0 ; i < 300 ; i++)
    generation_terrain_au_pif().to_tga("pif/pif_test" + to_string(i) + ".tga");
     return 0;


}


