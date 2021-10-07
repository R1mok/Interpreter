import random
import string

def Gen(count):
	while count > 0:
		text = ""
		randHeadder = random.randint(1, 100)
		if randHeadder < 37:
			text += "sms:"
		elif randHeadder < 67 and randHeadder >= 37:
			text += "tel:"
		elif randHeadder < 95 and randHeadder >= 67:
			text += "fax:"
		elif randHeadder >= 95:
			text += "trash:"
		randCountOfNumbers = random.randint(1, 3)
		numbers = ""
		while randCountOfNumbers > 0:
			rand = random.randint(1, 100)
			number = "+"
			while len(number) != 12:
				if rand > 95:
					number += random.choice(string.ascii_lowercase + string.digits)
				else: 
					number += str(random.randint(1,9))
			if randCountOfNumbers > 1:
				number += ","
			else:
				number += ";"
			numbers += number
			randCountOfNumbers = randCountOfNumbers - 1
		text += numbers
		randText = random.randint(1,5)
		rand = random.randint(1,100)
		rand2 = random.randint(1, 64)
		if randHeadder < 32:
			if rand < 60:
				text += "?body="
				while rand2 > 0:
					text += random.choice(string.ascii_lowercase + string.digits + '%' + ',' + '.' + '?' + '!')	
					rand2 = rand2 - 1
			elif rand >= 61 and rand < 95:
				text += "?"
			elif rand >= 95 and rand < 98:
				n = random.randint(64,80)
				while n > 0:
					text += random.choice(string.ascii_lowercase + string.digits + '%' + ',' + '.' + '?' + '!')
					n = n - 1
		elif randHeadder >= 32:
			if rand < 95:
				text += "?"
			else:
				while rand2 > 0:
					text += random.choice(string.ascii_lowercase + string.digits + '%' + ',' + '.' + '?' + '!')
					rand2 = rand2 - 1
		text += '\n'
		mfile.write(text)
		count = count - 1


n = int(input())
mfile = open("textfile1.txt", "w")		
Gen(n)
mfile.close()
print("Write in file succesfull")
