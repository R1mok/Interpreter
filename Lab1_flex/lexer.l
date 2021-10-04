%option noyywrap
%option c++

%%


ftp\:\/\/[a-z]{1,20}@([a-z]{1,20}\.)+[a-z]{1,5} {return 1;}
.* {return 0;}
%%
