#!/bin/sh
# author Mikko Raatikainen, 2019
#
# Test script for testing diagnoses. Prints out the number of proposed removals in each diagnosis
#

# Projects to be executed. Note, selecting all projects will result that the execution takes hours if not days.
projects=("QTPLAYGROUND" "QTWB"  "QTSOLBUG"  "QTSYSADM"  "QTJIRA"  "QSR"  "QDS"  "QTVSADDINBUG"  "QTWEBSITE"  "AUTOSUITE"  "PYSIDE"  "QTCOMPONENTS"  "QTIFW"  "QBS"  "QTMOBILITY"  "QTQAINFRA"  "QT3DS" "QTCREATORBUG" "QTBUG")

projects=("QBS")
	

URL=localhost
#URL="217.172.12.199"
# https://api.openreq.eu/milla

# Print the heading line 
printf "ID\t Layer\t  Reqs in layer \t Consistency\t Diag1\tDiag2\tDiag3_req\tDiag3_rel\n"

	
for project_i in "${projects[@]}"
do
	# Get the issue IDs
	reqIDs=`cat data/$project_i.json | jq .requirements[].id`
	reqsIDArray=($(echo $reqIDs | sed "s/\"//g"))

	# Example ID for testing the script and quick testing. Uncomment the following line line:
	# reqsIDArray=("QBS-1030" "QBS-1050" "QBS-1070")
	
	# For each requirement
	for Req_j in "${reqsIDArray[@]}"
	do	
		layers_json=`curl -s -X GET --header 'Accept: text/plain' 'http://'$URL':9203/getTransitiveClosureOfRequirement?requirementId='$Req_j'&layerCount=55' | jq  '.layers'`
		max_layers=`echo $layers_json | jq length`	
		
		cumulative_count=0
		
		# If there is layers, do the loop
		if [[ $max_layers -gt 1 ]]; then
	
			# Do for issues' each layer
			for (( layer_k=1; layer_k < $max_layers; ++layer_k ))
			do		
				
				count_deps_in_layer=`echo $layers_json | jq  '."'$layer_k'"' | jq length`
				cumulative_count=$((cumulative_count+count_deps_in_layer))
				printf "$Req_j\t$layer_k\t $cumulative_count\t"
				
				# Has an earlier loop for the issue has resulted in timeout?
				if [ "$consistency" != "timeout" ]; then
				
					start=`date +%s%N`
					consistency_resp=`timeout 3 curl -s -X GET --header 'Accept: text/plain' 'http://localhost:9203/getConsistencyCheckForRequirement?requirementId='$Req_j'&layerCount='$layer_k'&analysisOnly=false'`
					
					# Did it go successful or timeout
					if [ "$?" = "0" ]; then
						end=`date +%s%N`
						resp=`echo "${consistency_resp#*response:}"`
						consistency=`echo $resp | jq '.response[0].Consistent'`
												
						if [ "$consistency" = "true" ]; then
							printf "true\n"	
						else
							printf "false\t "	
							# if consistency is false parse out what's wrong.
							diag1_req=`echo $resp | jq '.response[1].Diagnosis.DiagnosisRequirements' | jq length`
							diag2_rel=`echo $resp | jq '.response[2].Diagnosis.DiagnosisRelationships' | jq length`
							diag3_req=`echo $resp | jq '.response[3].Diagnosis.DiagnosisRequirements' | jq length`
							diag3_rel=`echo $resp | jq '.response[3].Diagnosis.DiagnosisRelationships' | jq length `
							
							printf "$diag1_req \t $diag2_rel \t $diag3_req \t $diag3_rel \n"
						fi
		
					else
						# timeout happens the first time
						consistency="timeout"
						printf "timeout\tTO\n"
					fi
				else			
					# For timeout, print only the layer count for subsequent layers
					printf "timeout\tTO\n"	
						
				fi
				
			done
			consistency=""
		fi

	done

done

