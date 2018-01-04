#!/bin/bash

# This script was used to generate synthetic requirements for stress testing in the seminar paper 
# "OpenReq dependency engine: proof of concept and performance" by Iivo Raitahila

# Call this script to create a set of models automatically. Change the parameters below to customize the set

OUTPUTDIR=synthetic-models
# set SEED=$RANDOM to produce a random set
SEED=1

declare -a requirements=(
	"100" 
	"500"
	"750"
	"1000"
	"1500"
	"2000" 
)

declare -a dependencyrates=(
	"0" 
	"10"
	"20"
	"50"
	"75"
)

declare -a subfeaturerates=(
	"0" 
	"10"
	"20"
	"50"
	"75"
)

declare -a subfeaturecounts=(
	"0"
	"2" 
)

declare -a attributecounts=(
	"0"
	"2" 
	"5"
)

if [ ! -d "$OUTPUTDIR" ]; then
  mkdir $OUTPUTDIR
fi

for requirement in "${requirements[@]}"
do
	for dependencyrate in "${dependencyrates[@]}"
	do
		for subfeaturerate in "${subfeaturerates[@]}"
		do
			for subfeaturecount in "${subfeaturecounts[@]}"
			do
				# skip some unnecessary combinations
				if [ $subfeaturerate == 0 ] && [ $subfeaturecount -gt 0 ]; then
					continue
				fi

				if [ $subfeaturerate -gt 0 ] && [ $subfeaturecount == 0 ]; then
					continue
				fi

				for attributecount in "${attributecounts[@]}"
				do
					./synthetic-model-generator.sh $requirement $dependencyrate $subfeaturerate $subfeaturecount $attributecount $SEED \
						> $OUTPUTDIR/$requirement.$dependencyrate.$subfeaturerate.$subfeaturecount.$attributecount.json
					SEED=$((SEED+1))
					echo $SEED
				done
			done
		done
	done
done

