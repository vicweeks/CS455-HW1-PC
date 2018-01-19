# Makefile for CS455 HW1-PC

PACKAGE = cs455/overlay/
NODE = $(PACKAGE)node/
WIREFORMATS = $(PACKAGE)wireformats/

JFLAGS = -g
JC = javac
JVM = java
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	$(NODE)Registry.java \
	$(NODE)MessagingNode.java \
	$(WIREFORMATS)Protocol.java

default: classes

all: 

classes: $(CLASSES:.java=.class)

clean:
	$(RM) $(NODE)*.class
	$(RM) $(WIREFORMATS)*.class
	$(RM) *.class
