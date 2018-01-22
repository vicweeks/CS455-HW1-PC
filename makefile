# Makefile for CS455 HW1-PC

PACKAGES = \
	cs455/overlay/node \
	cs455/overlay/routing \
	cs455/overlay/transport \
	cs455/overlay/util \
	cs455/overlay/wireformats

JC = javac
JVM = java

SRC = ./

JFLAGS = -g 

.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	$(NODE)Registry.java \
	$(NODE)MessagingNode.java \
	$(WIREFORMATS)Protocol.java

default: classes

all: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) $(NODE)*.class
	$(RM) $(WIREFORMATS)*.class
	$(RM) $(ROUTING)*.class
	$(RM) $(UTIL)*.class
	$(RM) $(TRANSPORT)*.class

