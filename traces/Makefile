# For creating version dependent folders for each neuroidnet instance

SHELL=bash
DATETIME=$(shell date +%Y-%m-%d_%T)
TRACEDIR:=trace_$(DATETIME)
JAVARUN=$(HOME)/classes

hello:
	echo $(TRACEDIR); \
	ls ../..; \
	help

new-trace: jars
	echo $(DATETIME); \
	mkdir $(TRACEDIR); \
	cd $(TRACEDIR); \
	cp $(JAVARUN)/neuroidnet.jar  $(JAVARUN)/javautils.jar .; \
	cp ../Makefile.trace Makefile; 
	cd ..; cp scripts/*.bsh traces/$(TRACEDIR)/.; 
	cd $(TRACEDIR); \
	echo -e "0a\n//$(TRACEDIR)\n// --------------------\n.\nw\nq" | ed preamble.bsh; 
	rm -f trace; \
	ln -s $(TRACEDIR) trace

jars: $(JAVARUN)/neuroidnet.jar $(JAVARUN)/javautils.jar

neuroinet.jar:
	cd $(JAVARUN); \
	jar cf neuroidnet.jar neuroidnet/

javautils.jar:
	cd $(JAVARUN); \
	jar cf javautils.jar edu/
