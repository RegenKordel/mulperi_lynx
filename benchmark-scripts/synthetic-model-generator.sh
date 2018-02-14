#!/bin/bash

# This script was used to generate synthetic requirements for stress testing in the seminar paper 
# "OpenReq dependency engine: proof of concept and performance" by Iivo Raitahila

# Call from synthetic-test-cases.sh to create a specific scenario

# usage: ./synthetic-model-generator.sh numberOfRequirements dependencyRate optionalSubfeatureRate optionalSubfeatureCount attributeCount seed > testmodel.json
#	numberOfRequirements = how many requirements will be generated
#	dependencyRate = probability between 0 to 100 that a requirement depends on some other requirement
#	optionalsubfeatureRate = probability between 0 to 100 that a requirement has optional subfeatures
#	optionalSubfeatureCount = how many subfeatures a requirement have if none
#	attributeCount = how many attributes do each feature have
#	seed = the random number generator can be seeded to make a reproducible simulation
# example: ./synthetic-model-generator.sh 20 15 15 2 3 1337


NUM_OF_REQS=$1
DEPENDENCY_RATE=$2
SUBFEATURE_RATE=$3
SUBFEATURE_COUNT=$4
ATTRIBUTE_COUNT=$5
RANDOM=$6

echo "["
 
for (( i=1; i<=$NUM_OF_REQS; i++ ))
do
	HAS_DEPENDENCIES=$(( RANDOM % 100 ))
	HAS_SUBFEATURES=$(( RANDOM % 100 ))

	echo "{"
		echo "\"requirementId\": \"R$i\","

		# add subfeatures
	
		if [ $SUBFEATURE_RATE -gt $HAS_SUBFEATURES ] && [ $((i + SUBFEATURE_COUNT)) -le $NUM_OF_REQS ]
		then
			echo "\"subfeatures\": ["
			for (( j=1; j<=$SUBFEATURE_COUNT; j++ ))
			do
				while : ; do
					SUBFEATURE=$(( ( RANDOM % $NUM_OF_REQS ) + 1 ))
					[[ $SUBFEATURE -le $i ]] || break # check that the subfeature becomes later than the feature thus preventing any loops
				done
				echo "{"
				echo "\"types\": [\"R$SUBFEATURE\"],"
				echo "\"role\": \"subfeature$j\","
				echo "\"cardinality\": \"0-1\""
				echo "}"
				if [ $j -lt $SUBFEATURE_COUNT ]
				then
					echo ","
				fi
			done
			echo "],"
		fi

		# add dependencies
		if [ $DEPENDENCY_RATE -gt $HAS_DEPENDENCIES ] 
		then
			DEPENDS_ON=$(( ( RANDOM % $NUM_OF_REQS ) + 1 ))
			echo "\"relationships\": ["
			echo "{"
			echo "\"targetId\": \"R$DEPENDS_ON\","
			echo "\"type\": \"requires\""
			echo "}"
			echo "],"
		fi

		# add attributes
		if [ $ATTRIBUTE_COUNT -ge 1 ] 
		then
			echo "\"attributes\": ["
			for (( j=1; j<=$ATTRIBUTE_COUNT; j++ ))
			do
				echo "{"
				echo "\"name\": \"attribute$j\","
				echo "\"values\": [\"1\", \"2\"],"
				echo "\"defaultValue\": \"1\""
				echo "}"
				if [ $j -lt $ATTRIBUTE_COUNT ]
				then
					echo ","
				fi
			done
			echo "],"
		fi

		echo "\"name\": \"Synthetic requirement nro $i\""
	echo "}"

	if [ $i -lt $NUM_OF_REQS ]
	then
		echo ","
	fi
done

echo "]"

