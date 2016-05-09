#include <iostream>
#include <math.h>
#include "tgaimage.h"
#include <limits>
#include <time.h>
#include <assert.h>
#include <sstream>
#include <vector>

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



double const pi = 3.1415926535;


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

    class robotPosition{
    public:
        int x, y, angle;
        robotPosition(int x, int y, int angle): x(x), y(y), angle(angle) {}

    };

    vector<robotPosition> evaluate(){

        int minv = INT32_MAX;
        int*** true_values = new int**[width];


        for(int i = 0 ; i < width ; i++){
            true_values[i] = new int*[height];
            for(int j = 0 ; j < height ; j++){
                true_values[i][j] = new int[360];
                int x1 = i,
                    x2 = i,
                    y1 = j - dist_inter_mic/2,
                    y2 = j + dist_inter_mic/2;
                for(int d = 0 ; d < 360 ; d++){
                    double angle = d * pi / 180;



                    int x1r = x1 * cos(angle) - y1 * sin(angle),
                        x2r = x2 * cos(angle) - y2 * sin(angle),
                        y1r = y1 * cos(angle) + x1 * sin (angle),
                        y2r = y2 * cos(angle) + x2 * sin (angle);
             /*
                    xM1 = x1r;
                    xM2 = x2r;
                    yM1 = y1r;
                    yM2 = y2r;
              */
                    //   to_tga("dump/dump" + to_string(i) +" " + to_string(j) +" " + to_string(d) +".tga");
                    int val = eval(x1r, y1r, x2r, y2r);
                    minv = min(minv, val);
                    true_values[i][j][d] = val;
                }
                to_tga("dump/dump" + to_string(i) +" " + to_string(j)  +".tga");

            }
        }

        cout << minv << endl;
        vector<robotPosition> ret;
        for(int i = 0 ; i < width ; i++){
            for(int j = 0 ; j < height ; j++){
                for(int d = 0 ; d < 360 ; d++){
                    if(true_values[i][j][d] == minv){
                        ret.push_back(robotPosition(i, j, d));
                    }
                }
            }
        }
        return ret;
    }

    TGAImage to_tga(string name){
        TGAImage img(width, height, TGAImage::RGB);
        int minv = INT32_MAX;
        int** true_values = new int*[width];

        for(int i = 0 ; i < width ; i++){
            true_values[i] = new int[height];
            for(int j = 0 ; j < height ; j++){
                int val = eval(i, j,xM1, yM1);
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

        ofstream os;
        os.open(name + ".desc");
        os << "M1: " << xM1 << " " << yM1 << endl
           << "M2: " << xM2 << " " << yM2 << endl
           << "d1: " << decal1 << endl
           << "d2: " << decal2 << endl
           << "d3: " << decal3 << endl;

        os << "mins: " <<endl;
/*
        for(robotPosition r : evaluate()){
            double angle =  r.angle * pi / 180;
            int x1 = r.x * cos(angle) - (r.y - dist_inter_mic/2) * sin(angle),
                x2 = r.x * cos(angle) - (r.y + dist_inter_mic/2) * sin(angle),
                y1 = (r.y - dist_inter_mic/2) * cos(angle) + r.x * sin (angle),
                y2 = (r.y + dist_inter_mic/2) * cos(angle) + r.x * sin (angle);
            img.set(x1, y1, TGAColor(0, 120, 120, 1));
            img.set(x2, y2, TGAColor(0, 120, 120, 1));

            os << r.x << " " << r.y << " " << angle << " " << x1 << ", " << y1 << "    " << x2 << ", " << y2 <<endl;

        }
*/
        os.close();



        img.write_tga_file(name.c_str());
        return img;
    }



    int eval(int xbase, int ybase, int i2, int j2) const{

        int dist_base1 =  sqrt((xH1 - xbase) *(xH1 - xbase)  + (yH1 - ybase) * (yH1 - ybase));
        int dist_micro1 = sqrt((xH1 - i2)    *(xH1 - i2)     + (yH1 - j2)    * (yH1 - j2));

        int dist_base2 =  sqrt((xH2 - xbase) *(xH2 - xbase)  + (yH2 - ybase) * (yH2 - ybase));
        int dist_micro2 = sqrt((xH2 - i2)    *(xH2 - i2)     + (yH2 - j2)    * (yH2 - j2));

        int dist_base3 =  sqrt((xH3 - xbase) *(xH3 - xbase)  + (yH3 - ybase) * (yH3 - ybase));
        int dist_micro3 = sqrt((xH3 - i2)    *(xH3 - i2)     + (yH3 - j2)    * (yH3 - j2));


        int diff1 = dist_base1 - dist_micro1;

        int diff2 = dist_base2 - dist_micro2;

        int diff3 = dist_base3 - dist_micro3;

        int diff_base = sqrt((xbase - i2)*(xbase - i2) + (ybase - j2)*(ybase - j2));

        return   (1*( abs(diff1 - decal1))
                 + 1*(abs(diff2 - decal2))
                 + 1*(abs(diff3 - decal3))
                 + 0*((diff_base - dist_inter_mic) * (diff_base - dist_inter_mic)));

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
    t.xH3 = width-1;
    t.yH3 = 0;
    t.xH2 = width-1;
    t.yH2 = height-1;

    //on déduit les décalages (:O un truc vraiment calculé )
    t.calcule_decalages();

    return t;
}


terrain generation_terrain_vraies_donnees(){
    terrain t;
    t.xH1 = 0;
    t.yH1 = height/2;

    t.xH3 = width-1;
    t.yH3 = 0;

    t.xH2 = width-1;
    t.yH2 = height-1;

    int data[] = {   26, 108, 42, 118, 26, -24, -9,
                  114, 134, 133, 146, 25, -26 , 2 ,
                  106, 55, 99, 77, -16, -9, 17,
                  width-89, 80, width-97, 60, -6, 9, -10,
                  width-75, 53, width-97, 60, -28, 8, 26,
                  width - 7, 8, width - 5, 28, -5, -26, 23};

    for(int i = 0 ; i < 6 ; i++){
        t.xM1 = data[i*7];
        t.yM1 = data[i*7+1];

        t.xM2 = data[i*7+2];
        t.yM2 = data[i*7+3];

        t.decal1 = data[i*7+4];
        t.decal2 = data[i*7+5];
        t.decal3 = data[i*7+6];


       t.evaluate();
        //t.calcule_decalages();
        t.to_tga("vraies_donnees/vraies_donneesP" + to_string(i+1) + ".tga");
    }
    return t;
}
int H[][2] = {
               {0, height/2},
               {width-1, height-1},
               {width-1, 0}

             } ;

int energy(int x, int y,int alpha){
    int mic2mic = 22;
/*
    int H[][2] = {
                   {0, height/2},
                   {width-1, 0},
                   {width-1, height-1}
                 } ;

    int D[] = { -16 * speed_of_sound / samplingRate,
                -9 * speed_of_sound / samplingRate,
                17 * speed_of_sound / samplingRate} ;

*/

    double Ax = x + mic2mic * cos((double)alpha/180*3.1416)/2;
    double Ay = y + mic2mic * sin((double)alpha/180*3.1416)/2;
    double Bx = x - mic2mic * cos((double)alpha/180*3.1416)/2;
    double By = y - mic2mic * sin((double)alpha/180*3.1416)/2;

    terrain t;

    t.decal1 = 25.*speed_of_sound/samplingRate;
    t.decal2 = -26.*speed_of_sound/samplingRate;
    t.decal3 =  2.*speed_of_sound/samplingRate;
    t.xH1 = H[0][0]; //flemme
    t.yH1 = H[0][1];
    t.xH2 = H[1][0];
    t.yH2 = H[1][1];
    t.xH3 = H[2][0];
    t.yH3 = H[2][1];

    return t.eval(Ax, Ay, Bx, By);
/*
    int v = 0;
    for (int i = 0 ; i < 3 ; i++){
        int dA = sqrt((H[i][0]-Ax)*(H[i][0]-Ax) + (H[i][1]-Ay)*(H[i][1]-Ay));
        int dB = sqrt((H[i][0]-Bx)*(H[i][0]-Bx) + (H[i][1]-By)*(H[i][1]-By));
        v += abs(D[i] - abs(dB-dA));
    }
    return v;
    */
}


void drawplot(){

    FILE* f = fopen("plot", "w");
    string s;
    int mx, my, ma, mm = INT32_MAX;
    int** sum = new int*[width];
    for(int x = 0 ; x < width ; x++){
        sum[x] = new int[height];
        for(int y = 0 ; y < height ; y++){
            int m = INT32_MAX;
            int z = 150;
            int e;

            sum[x][y] = 0;

            for(int a = 0 ; a < 360 ; a++){

                e = energy(x, y, a);

                sum[x][y] += abs(e);
                //s += to_string(x) + " " + to_string(y) + " " + to_string(a) + " " + to_string(e) + "\n";

                if (abs(e) < m){
                    m = abs(e);
                    z = a;
                    if(m < mm){
                        mx = x;
                        my = y;
                        ma = a;
                        mm = m;
                    }
                }
            }
            s += to_string(x) + " " + to_string(y) + " " + to_string(z) + " " + to_string(m) + "\n";

        }
    }
    cout <<"minimum global: " << mx << " " << my << " " << ma << " " << mm;
    fwrite( s.c_str(), s.size(), sizeof(char), f);
    fclose(f);
}

int main(int argc, char *argv[]){

    drawplot();

    /*
    //int** values = (int**) malloc(sizeof(int**) * width);
    srand(time(NULL));
    generation_terrain_vraies_donnees();
    //for(int i = 0 ; i < 100 ; i++)
    //generation_terrain_au_pif().to_tga("pif/pif_test" + to_string(i) + ".tga");
     return 0;
*/

}


