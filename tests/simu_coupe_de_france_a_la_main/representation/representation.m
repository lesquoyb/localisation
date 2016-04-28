syms Gx Gy Dx Dy rh1 rh2 rh3 x y G2x G2y;



h1x = 0;
h1y = 88;

h2x = 240;
h2y = 175;

h3x = 240;
h3y = 0;



eqsG1 = [   (Gx - h1x)^2 + (Gy - h1y)^2 == rh1^2
            (Gx - h2x)^2 + (Gy - h2y)^2 == rh2^2
            (Gx - h3x)^2 + (Gy - h3y)^2 == rh3^2
];

eqsG2 = [ (G2x - h1x)^2 + (G2y - h1y)^2 == (rh1 + 21)^2
          (G2x - h2x)^2 + (G2y - h2y)^2 == (rh2 - 18)^2
          (G2x - h3x)^2 + (G2y - h3y)^2 == (rh3 - 8)^2
          (G2x - Gx)^2 + (G2y - Gy)^2 == 25^2
];

S = solve([eqsG2; eqsG1])
for i = S
    i
end
 
 
%a = solve([h1, h2, h3] == sol, [Gx Gy rh1])
%pos = [h1 + p1, h1 + p2, h1 + p3, h1 + p4, h1 + p5, h1 + p6];

%a = solve( 'Gx^2 + (-88 + Gy)^2 + (-240 + Gx)^2 + Gy^2 +  (-240 + Gx)^2 + (-175 + Gy)^2 = (21 + rh1)^2 + (-18 + rh2)^2 + (-8 + rh3)^2', [rh1, rh2, rh3, Gx, Gy])

%ezplot('Gx^2 + (-88 + Gy)^2 + (-240 + Gx)^2 + Gy^2 +  (-240 + Gx)^2 + (-175 + Gy)^2 = (21 + rh1)^2 + (-18 + rh2)^2 + (-8 + rh3)^2')

%plot(char((3*Gx^2 - 960*Gx + 3*Gy^2 - 526*Gy - rh2^2 + 36*rh2 - rh3^2 + 16*rh3 + 153181)^(1/2) - 21))
%figure;
%plot(Gx^2 + Gy^2 == 25)
%solve(h1 == sol(1) + sol(2) + sol(3))
%ezplot((x -Gx)^2 + (y - Gy)^2 == 25^2, [-240 240])
%ezplot(Gx^2 + Gy^2 == 1)











