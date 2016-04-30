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
            dist_inter_mic = 22;


int decalage(int x1, int y1, int x2, int y2, int xBase, int yBase){
    return ( sqrt( (xBase - x2)*(xBase - x2)  + (yBase - y2)*(yBase - y2) )
            -sqrt( (xBase - x1)*(xBase - x1)  + (yBase - y1)*(yBase - y1) ) )*samplingRate/speed_of_sound;
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
        int minv = 9999;
        int** true_values = new int*[width];

        for(int i = 0 ; i < width ; i++){
            true_values[i] = new int[height];
            for(int j = 0 ; j < height ; j++){
                int val = eval(i, j,xM1, yM1, decal1, decal2, decal3);
                minv = min(minv, val);
                true_values[i][j] = val;
                img.set(i,j, TGAColor((unsigned char) max(min(val, white), black), (unsigned char) max(min(val, white), black), (unsigned char) max(min(val, white), black), 1));
            }
        }

        img.set(xM1, yM1, TGAColor(255, 0, 0, 1) );
        img.set(xM2, yM2, TGAColor(0, 255, 0, 1));
        img.set(xH1, yH1, TGAColor(0, 0, 255, 1));
        img.set(xH2, yH2, TGAColor(0, 0, 255, 1));
        img.set(xH3, yH3, TGAColor(0, 0, 255, 1));
        for(int i = 0 ; i < width ; i++){
            for(int j = 0 ; j < height ; j++){
                if(true_values[i][j] == minv){
                    TGAColor c = img.get(i,j);
                    c.b = 255;
                    img.set(i,j,c);
                }
            }
        }
        img.write_tga_file(name.c_str());
        ofstream os;
        os.open(name + ".desc");

        os << "M1: " << xM1 << " " << yM1 << endl
           << "M2: " << xM2 << " " << yM2 << endl
           << "d1: " << decal1 << endl
           << "d2: " << decal2 << endl
           << "d3: " << decal3 << endl;

        os.close();
        return img;
    }



    int eval(int xbase, int ybase, int i2, int j2, int decalage1, int decalage2, int decalage3) const{

        int dist_base1 =  sqrt((xH1 - xbase) *(xH1 - xbase)  + (yH1 - ybase) * (yH1 - ybase));
        int dist_micro1 = sqrt((xH1 - i2) *(xH1 - i2)  + (yH1 - j2) * (yH1 - j2));

        int dist_base2 =  sqrt((xH2 - xbase) *(xH2 - xbase)  + (yH2 - ybase) * (yH2 - ybase));
        int dist_micro2 = sqrt((xH2 - i2) *(xH2 - i2)  + (yH2 - j2) * (yH2 - j2));

        int dist_base3 =  sqrt((xH3 - xbase) *(xH3 - xbase)  + (yH3 - ybase) * (yH3 - ybase));
        int dist_micro3 = sqrt((xH3 - i2) *(xH3 - i2)  + (yH3 - j2) * (yH3 - j2));


        int diff1 = dist_base1 - dist_micro1;

        int diff2 = dist_base2 - dist_micro2;

        int diff3 = dist_base3 - dist_micro3;

        int diff_base = sqrt((xbase - i2)*(xbase - i2) + (ybase - j2)*(ybase - j2));

        return   (1*( abs(diff1 - decalage1))
                 + 1*(abs(diff2 - decalage2))
                 + 1*(abs(diff3 - decalage3))
                 + 10*(abs(diff_base - dist_inter_mic)));

    }



    void calcule_decalages(){
        decal1 = (sqrt( (xH1 - xM2)*(xH1 - xM2)  + (yH1 - yM2)*(yH1 - yM2) )
                    -sqrt( (xH1 - xM1)*(xH1 - xM1)  + (yH1 - yM1)*(yH1 - yM1) ) )*samplingRate/speed_of_sound;

        decal2 = (       sqrt( (xH2 - xM2)*(xH2 - xM2)  + (yH2 - yM2)*(yH2 - yM2) )
                    -   sqrt( (xH2 - xM1)*(xH2 - xM1)  + (yH2 - yM1)*(yH2 - yM1) ))*samplingRate/speed_of_sound;

        decal3 =      (sqrt( (xH3 - xM2)*(xH3 - xM2)  + (yH3 - yM2)*(yH3 - yM2) )
                    -sqrt( (xH3 - xM1)*(xH3 - xM1)  + (yH3 - yM1)*(yH3 - yM1) ))*samplingRate/speed_of_sound;
    }

};

terrain generation_terrain_au_pif(){
    terrain t;

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
    t.calcule_decalages();

    return t;
}


terrain generation_terrain_vrais_donnees(){
    terrain t;
    t.xH1 = 0;
    t.yH1 = height/2;

    t.xH3 = width-1;
    t.yH3 = 0;

    t.xH2 = width-1;
    t.yH2 = height-1;

    t.xM1 = width - 7;
    t.yM1 = 8;

    t.xM2 = width - 4.5;
    t.yM2 = 28;

    t.calcule_decalages();

    //cout << t.decal1 << " " << t.decal2 << " " << t.decal3 << endl;
    return t;
}




int main(int argc, char *argv[]){

    //int** values = (int**) malloc(sizeof(int**) * width);
    srand(time(NULL));
    generation_terrain_vrais_donnees().to_tga("vraies_donnees.tga");
    //for(int i = 0 ; i < 100 ; i++)
    //generation_terrain_au_pif().to_tga("pif/pif_test" + to_string(i) + ".tga");
     return 0;


}


