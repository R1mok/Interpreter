%option noyywrap

%{
#include <stdlib.h>
int k = 0;
%}

%%

sms\:\+[0-9]{11}(\,\+[0-9]{11})*\;\?(body\=([a-zA-z0-9\%\,\.\!\?]{1,64}))? {}
fax\:\+[0-9]{11}(\,\+[0-9]{11})*\;\? {}
tel\:\+[0-9]{11}(\,\+[0-9]{11})*\;\? {}
.* {k++; printf("bad\n");}

%%

int main (int argc, char* argv[]){
	yyin = fopen(argv[1], "r");
	yylex();
	printf("%d\n", k);
	fclose(yyin);
return 0;
}
