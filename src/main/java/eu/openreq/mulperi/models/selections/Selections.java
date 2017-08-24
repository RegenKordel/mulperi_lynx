package eu.openreq.mulperi.models.selections;

import java.util.List;

public class Selections {
	private List<FeatureSelection> featureSelections;
	private List<CalculationConstraint> calculationConstraints;
	
	public Selections() {
	}
	
	public List<FeatureSelection> getFeatureSelections() {
		return featureSelections;
	}
	public void setFeatureSelections(List<FeatureSelection> featureSelections) {
		this.featureSelections = featureSelections;
	}
	public List<CalculationConstraint> getCalculationConstraints() {
		return calculationConstraints;
	}
	public void setCalculationConstraints(List<CalculationConstraint> calculationSelections) {
		this.calculationConstraints = calculationSelections;
	}
	
	
	
}
