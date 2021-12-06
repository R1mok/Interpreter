@echo off
REM Please adjust JFLEX_HOME and JFLEX_VERSION to suit your needs
REM (please do not add a trailing backslash)

if not defined JFLEX_HOME set JFLEX_HOME=C:\smc-git-code-b058839dba1b0c62de2a6ea5f31c54d57f075381-20210913T092731Z-001\smc-git-code-b058839dba1b0c62de2a6ea5f31c54d57f075381\examples\C++\Lab3\jflex-1.8.2
if not defined JFLEX_VERSION set JFLEX_VERSION=1.8.2

java -Xmx128m -jar "%JFLEX_HOME%\lib\jflex-full-%JFLEX_VERSION%.jar" %*
