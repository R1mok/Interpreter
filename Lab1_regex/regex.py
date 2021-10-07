# coding=utf8
# the above tag defines encoding for this document and is for Python 2.x compatibility

import re
import time
regex = r"(sms\:(\+[0-9]{11})(\,\+[0-9]{11})*\;\?(body\=[a-zA-Z%?!,.0-9]{1,64})?)|((tel|fax)\:(\+[0-9]{11})(\,\+[0-9]{11})*\;\?)"

fd = open("textfile1.txt", "r")
test_str = fd.read()
fd.close()

m = dict()

matches = re.finditer(regex, test_str, re.MULTILINE)
first = time.time()
for matchNum, match in enumerate(matches, start=1):
	if match.group() != None:
		Str = match.group(0)
		Str = Str[4:]
		i = 0
		outStr = ""
		while Str[i] != ';':
			outStr += Str[i]
			i = i + 1
		outList = re.split(",", outStr)
		for i in range(len(outList)):
			if outList[i] in m:
				m[outList[i]] = m[outList[i]] + 1
			else:
				m[outList[i]] = 1
second = time.time()
for key in m:
	print(key + ": " + str(m[key]))
print("==================")
print(second - first)

# Note: for Python 2.7 compatibility, use ur"" to prefix the regex and u"" to prefix the test string and substitution.

