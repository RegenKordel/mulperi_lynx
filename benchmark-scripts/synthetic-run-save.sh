#!/bin/bash

# This script was used to generate synthetic requirements for stress testing in the seminar paper 
# "OpenReq dependency engine: proof of concept and performance" by Iivo Raitahila

# Call this script to send the models to Mulperi (only save). 
# Modify the parameters below and run ./synthetic-run-save.sh > results.txt

# Mulperi and SpringCaas are configured to log the execution times to files that are gathered after the saving:
#	long start = System.nanoTime();
#	//all code here        
#	long duration = (System.nanoTime() - start) / 1000000; //nanoseconds to milliseconds
#	PrintWriter writer = new PrintWriter("mulperi-execution.txt", "UTF-8");
#	writer.println(duration);
#	writer.close();

FILES=synthetic-models/*
MULPERIURL=http://localhost:8091/models/mulson
MULPERILOG=/home/iivorait/workspace/mulperi/mulperi-execution.txt
SPRINGCAASLOG=/home/iivorait/workspace/SpringCaaS/springcaas-execution.txt
CHOCOLOG=/home/iivorait/workspace/SpringCaaS/choco-execution.txt

for file in $FILES
do
	echo -n "$file "
	MULPERIOUTPUT=$(curl -s $MULPERIURL -d @$file --header "Content-Type: application/json")

	# wait for one second for Mulperi and SpringCaas to write the execution times
	sleep 1
	MULPERITIME=$(cat $MULPERILOG)
	SPRINGCAASTIME=$(cat $SPRINGCAASLOG)
	CHOCOTIME=$(cat $CHOCOLOG)
	echo -n "$MULPERITIME $SPRINGCAASTIME $CHOCOTIME "

	# the sed expression removes newlines from any error messages	
	echo -n $MULPERIOUTPUT | sed ':a;N;$!ba;s/\n/ /g'

	echo ""
done


