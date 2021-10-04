%option noyywrap
%option c++

%x NUMBER_SMS NUMBER_OTH TAIL DO_NUMBER
%{
#include <stdlib.h>
#include <map>
std::map<std::string, int> m;
void inMap(std::string& s){
	if (m.count(s))
		m[s] += 1;
	else
		m[s] = 1;
}
%}

%%
<INITIAL>{
^(sms\:) BEGIN(NUMBER_SMS);
^(fax\:) BEGIN(NUMBER_OTH);
^(tel\:) BEGIN(NUMBER_OTH);
\n return 0;
. ;
}


<NUMBER_SMS>{
(\+[0-9]{11})/((\,\+[0-9]{11})*\;\?(body\=[a-zA-Z0-9\%\,\.\!\?]{1,64})?)\n {
std::string s;
for (int i = 0; i < 12; ++i) 
	s += yytext[i];
inMap(s); BEGIN(NUMBER_SMS);}

(\,\+[0-9]{11})/((\,\+[0-9]{11})*(\;\?(body\=[a-zA-Z0-9\%\,\.\!\?]{1,64})?))\n {
std::string s;
for (int i = 1; i < 13; ++i) 
	s += yytext[i];
inMap(s); BEGIN(NUMBER_SMS);}

\;\?(body\=[a-zA-Z0-9\%\,\.\!\?]{1,64})? {BEGIN(TAIL); return 1;}
. BEGIN(INITIAL);
\n {BEGIN(INITIAL); return 0;}

}

<NUMBER_OTH>{
(\+[0-9]{11})/((\,\+[0-9]{11})*(\;\?))\n {
std::string s;
for (int i = 0; i < 12; ++i) 
	s += yytext[i];
inMap(s); BEGIN(NUMBER_OTH);}
(\,\+[0-9]{11})/((\,\+[0-9]{11})*\;\?)\n {
std::string s;
for (int i = 1; i < 13; ++i) 
	s += yytext[i];
inMap(s); BEGIN(NUMBER_OTH);}
(\;\?) {BEGIN(TAIL); return 1;}
. BEGIN(INITIAL);
\n {BEGIN(INITIAL); return 0;}
}

<TAIL>{
. ;
\n BEGIN(INITIAL);
}



%%

#include <fstream>
int main (int argc, char* argv[]){
	std::ifstream fin(argv[1]);
	yyFlexLexer ftp(fin, std::cout);
	std::string s;
	int n = 20;
	while (n--){ ftp.yylex(); }
	for (auto elem : m){
		std::cout << elem.first << ": " << elem.second << std::endl;
	}
	fin.close();
return 0;
}

