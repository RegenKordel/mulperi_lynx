#!/bin/bash

# This script was used to generate synthetic requirements for stress testing in the seminar paper 
# "OpenReq dependency engine: proof of concept and performance" by Iivo Raitahila

# Call this script to run configuration benchmarking (save and configure). 
# Modify the parameters below and run ./synthetic-run-configure.sh > results.txt

# The execution time files are generated similarly as in the example in synthetic-run.sh

FILES=synthetic-models/*
#note: only host and port
MULPERIADDRESS=http://localhost:8091
MULPERILOG=/home/iivorait/workspace/mulperi/mulperi-execution.txt
SPRINGCAASLOG=/home/iivorait/workspace/SpringCaaS/springcaas-execution.txt
CHOCOLOG=/home/iivorait/workspace/SpringCaaS/choco-execution2.txt

# example configuration JSON:
#{
#    "featureSelections": [
#        {
#            "type": "R5"
#         },
#        {
#             "type": "R4",
#             "attributes": [
#                {
#                    "name":"priority",
#                    "value":"3"
#                }
#                      ]
#        }
#    ],
#    "calculationConstraints": []
#}

for file in $FILES
do
	# example file: synthetic-models-subfeatures/100.0.20.2.5.json becomes 100
	NUMOFREQS=$(echo $file | cut -d '/' -f 2 | cut -d '.' -f 1)
	INCREASE=$((NUMOFREQS/10))

	# send model to Mulperi for model ID
	MODELID=$(curl -s "$MULPERIADDRESS/models/mulson" -d @$file --header "Content-Type: application/json")
	CONFURL="$MULPERIADDRESS/models/$MODELID/configurations"

	# first requirement preselected
	JSONSTART="{\"featureSelections\": [ { \"type\": \"R1\" }"
	JSONEND=" ], \"calculationConstraints\": [] }"

	# with 100 the sequence is 1 11 21 31 41 51 61 71 81 91
	for (( c=1; c<=$NUMOFREQS; c += $INCREASE ))
	do
		ts=$(date +%s%N)
		#MULPERIOUTPUT = HTTP code 200 on success or 400/409 on error
		MULPERIOUTPUT=$(curl -s $CONFURL -d "$JSONSTART$JSONEND" --header "Content-Type: application/json" --write-out %{http_code} --silent --output /dev/null)
		tt=$((($(date +%s%N) - $ts)/1000000))	

		# wait for one second for Mulperi and SpringCaas to write the execution times
		sleep 1
		MULPERITIME=$(cat $MULPERILOG)
		SPRINGCAASTIME=$(cat $SPRINGCAASLOG)
		CHOCOTIME=$(cat $CHOCOLOG)

		echo  "$file $c $tt $MULPERITIME $SPRINGCAASTIME $CHOCOTIME $MULPERIOUTPUT $MODELID" 

		# error handling
		#if [ $MULPERIOUTPUT != 200 ]; then
		#	curl -s $CONFURL -d "$JSONSTART$JSONEND" --header "Content-Type: application/json"
		#	echo $JSONSTART$JSONEND
		#	exit
		#fi
	
		# select next request's requirements
		REQSTART=$((c+1))
		REQEND=$((c+INCREASE))
		for (( i=$REQSTART; i<=$REQEND; i++ ))
		do
			JSONSTART=$JSONSTART", { \"type\": \"R$i\" }"
		done
	done
done






