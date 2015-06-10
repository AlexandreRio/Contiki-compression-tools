all:
	gcc -m32 compress.c -o compress-x86

code-stat:
	./run.sh --stat

code-table:
	./run.sh --table

stat:
	./stats.sh

clean:
	rm *.json.compress *.json.decompress *.class *.o compress
