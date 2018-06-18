// Chocon ruoan käsittelyä (CSP)

package eu.openreq.mulperi.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;

import eu.openreq.mulperi.models.release.Release;
import eu.openreq.mulperi.models.release.ReleasePlan;
import eu.openreq.mulperi.models.release.Requirement;

public class ReleaseCSPPlanner {

	private ReleasePlan releasePlan;

	private LinkedHashMap<String, Integer> reqID2Index;
	private LinkedHashMap<Integer, String> index2reqID; 

	private final int nReleases;
	private final int nRequirements;
	Req4Csp[] reqCSPs = null;
	Model model = null;

	public ReleaseCSPPlanner( ReleasePlan releasePlan) {
		this.releasePlan = releasePlan;
		nReleases = releasePlan.getReleases().size();
		nRequirements= releasePlan.getRequirements().size();
		reqID2Index = new LinkedHashMap<>(nRequirements);
		index2reqID = new LinkedHashMap<>(nRequirements);
		int index1 = 0;
		// Why the heck the ++??
		for (Requirement requirement : releasePlan.getRequirements()) {
			Integer index2 = Integer.valueOf(index1++);
			reqID2Index.put(requirement.getId(), index2);
			index2reqID.put(index2,requirement.getId());
		}
	}

	public final int getNReleases() {
		return nReleases;
	}

	public final int getNRequirements() {
		return nRequirements;
	}

	public void generateCSP () {
		model = new Model("ReleasePlanner");
		this.reqCSPs = new Req4Csp[nRequirements];
		for (Requirement requirement : releasePlan.getRequirements()) {
			Req4Csp req4csp = new Req4Csp(requirement, this, model);
			reqCSPs[reqID2Index.get(requirement.getId()).intValue()] = req4csp;
		}		

		//constraints for ensuring enough effort per release
		for (Release release: releasePlan.getReleases()) {
			ArrayList<IntVar> releaseEffortVars = new ArrayList<>(); 
			for (Requirement requirement : releasePlan.getRequirements()) {
				Req4Csp req4Csp = reqCSPs[reqID2Index.get(requirement.getId()).intValue()];
				IntVar effortVar = req4Csp.getEfforOfRelease(release.getId()-1);
				if (effortVar.getUB() >= 0) //do not add variable to be summed if the variable cannot be > 0 
					releaseEffortVars.add(effortVar);
			}
			if (releaseEffortVars.size() >0) {
				IntVar[] effortVarArray = releaseEffortVars.toArray(new IntVar[0]);
				model.sum(effortVarArray, "<=", release.getMaxCapacity()).post();
			}
		}

		//add requires-dependendencies
		// now we use a version that if A requires B, 
		// B must be included and assigned in the same or earlier release than A
		for (Requirement requirement : releasePlan.getRequirements()) {
			Req4Csp requiring = reqCSPs[reqID2Index.get(requirement.getId()).intValue()]; 
			for (String requiredId: requirement.getRequiresDependencies()) {
				int requiredIndex = reqID2Index.get(requiredId).intValue();
				Req4Csp required = reqCSPs[requiredIndex]; 
				model.ifThen(
						requiring.getIsIncluded(),
						model.and(
								model.arithm(required.getIsIncluded(), "=", 1),
								model.arithm(required.getAssignedRelease(), "<=", requiring.getAssignedRelease())));
			}
		}
	}

	public boolean isReleasePlanConsistent() {
		for (int ndx = 0; ndx < nRequirements; ndx++)
			reqCSPs[ndx].require(true);
		model.getSolver().reset();
		
		Solver solver = model.getSolver();
		return solver.solve();
	}
	
	public String getDiagnosis() {
		List<Req4Csp> allReqs = new ArrayList<>();
		for (int req = 0; req < nRequirements; req++)
			allReqs.add(reqCSPs[req]);

		List<Req4Csp> diagnosis = 
				fastDiag(allReqs, allReqs);
		StringBuffer sb = new StringBuffer();
		if(diagnosis.isEmpty())
			sb.append("(No Diagnosis found.)");
		else {
			sb.append("Diagnosis: ");
			for (Req4Csp reqB: diagnosis) {

				String reqId= reqB.getId();
				sb.append(reqId);
				sb.append(" ");
			}
		}
		return sb.toString();
	}


	public static class Req4Csp  {
		private BoolVar isIncluded;
		private IntVar assignedRelease;
		private IntVar[] effortInRelease;
		private boolean denyPosted = false;
		private boolean requirePosted = false;
		private Constraint requireCstr;
		private Constraint denyCstr;
		private Model model;
		private String id;

		Req4Csp(Requirement requirement, ReleaseCSPPlanner rcg, Model model) {
			this.model = model;
			id = requirement.getId();

			isIncluded = model.boolVar(requirement.getId()+"_in");

			requireCstr = model.arithm(isIncluded, "=", 1);
			denyCstr =  model.arithm(isIncluded, "=", 0);
			//TODO restore
			model.post(requireCstr);
			requirePosted = true;
			if (requirement.getAssignedRelease() == 0)
				assignedRelease = model.intVar(requirement.getId()+ "_assignedTo", 0, rcg.getNReleases() -1);
			else
				assignedRelease = model.intVar(requirement.getId()+ "_assignedTo", requirement.getAssignedRelease() -1);

			//create choco variables for representin effort in each release
			effortInRelease = new IntVar[rcg.getNReleases()];
			int [] effortDomain = new int[2];
			effortDomain[0] = 0;
			effortDomain[1] = requirement.getEffort();
			for (int releaseNdx = 0; releaseNdx <rcg.getNReleases(); releaseNdx ++ ) {
				String varNme = "req_" + requirement.getId()+ "_" + (releaseNdx +1);
				if (requirement.getAssignedRelease() == 0) //not assigned
					effortInRelease[releaseNdx] = model.intVar(varNme, effortDomain); //effort in release is 0 or the effort  
				else {//assigned to release
					if (requirement.getAssignedRelease() -1 == releaseNdx)
						effortInRelease[releaseNdx] = model.intVar(varNme, effortDomain); 
					else
						effortInRelease[releaseNdx] = model.intVar(varNme, 0); //domain is fixed 0 in other releases
				}
			}

			// Create constraints the enforce If the effort in assigned release
			//if release is assigned, connect only the affcted release
			if (requirement.getAssignedRelease() == 0) {
				for (int releaseNdx = 0; releaseNdx <rcg.getNReleases(); releaseNdx ++ ) {
					//effectively forces others to 0 because domain size is 2, and the non-0 gets forbidden
					//Could try if adding explicit constraints would be faster
					model.ifOnlyIf(
							model.and(model.arithm(isIncluded, "=", 1),
									model.arithm(assignedRelease, "=", releaseNdx)),
							model.arithm(effortInRelease[releaseNdx], "=", requirement.getEffort()));
				}
			} 
			else {
				model.ifThenElse(
						isIncluded,
						model.arithm(effortInRelease[requirement.getAssignedRelease()-1], "=", requirement.getEffort()),
						model.arithm(effortInRelease[requirement.getAssignedRelease()-1], "=", 0));
			}



		}
		protected IntVar getEfforOfRelease(int releaseNdx) {
			return effortInRelease[releaseNdx];
		}

		protected IntVar getAssignedRelease() {
			return assignedRelease;
		}

		protected BoolVar getIsIncluded() {
			return isIncluded;
		}

		protected void require (boolean include) {
			if (include) {
				if (requirePosted)
					return;
				requireCstr.post();
				requirePosted =true;
				if (denyPosted) {
					model.unpost(denyCstr);
					denyPosted = false;
				}
			}
			else { //not incude = deny
				if (denyPosted)
					return;
				denyCstr.post();
				denyPosted = true;
				if (requirePosted) {
					model.unpost(requireCstr);
					requirePosted = false;
				}

			}
		}

		protected void unRequire () {
			if (denyPosted) {
				model.unpost(denyCstr);
				denyPosted = false;
			}
			if (requirePosted) {
				model.unpost(requireCstr);
				requirePosted = false;
			}

		}


		public String getId() {
			return id;
		}

		public String toString() {
			return id;
		}

	}

	private void setRequirementsToList (List<Req4Csp> reqsToSet) {
		for (int i = 0; i< nRequirements; i++)
			reqCSPs[i].unRequire(); 			

		for (Req4Csp req: reqsToSet)
			req.require(true);
	}


	private boolean consistent(List<Req4Csp> constraints) {


		//System.out.print("consistent? ");

		if (constraints.size() == 0) {
			return true;
		}

		setRequirementsToList(constraints);
		//for (Req4Csp req :constraints)
		//	System.out.print(req.getId() +" ");
		Solver solver = model.getSolver();
		solver.reset();
		boolean result = solver.solve();
		//System.out.println("=" + result);
		return result;
	}

	/**
	 * Adapted from
	 * /JMiniZinc/at.siemens.ct.jminizinc.diag/src/main/java/at/siemens/ct/jmz/diag/FastDiag.java
	 * @param C
	 * @param AC
	 * @return
	 */
	private List<Req4Csp> fastDiag(List<Req4Csp> C, List<Req4Csp> AC) {

		if (C.isEmpty())
			return Collections.emptyList();
		if (consistent(C))
			return Collections.emptyList();

		List<Req4Csp> ACWithoutC = diffListsAsSets(AC, C);
		Boolean searchForDiagnosis =consistent(ACWithoutC);
		if (!searchForDiagnosis)
			return Collections.emptyList();
		return fd( Collections.emptyList(), C, AC);

	}

	/**
	 * Function that computes diagnoses in FastDiag
	 * Adapted from
	 * /JMiniZinc/at.siemens.ct.jminizinc.diag/src/main/java/at/siemens/ct/jmz/diag/FastDiag.java
	 * 
	 * @param D
	 *            A subset from the user constraints
	 * @param C
	 *            A subset from the user constraints
	 * @param AC
	 *            user constraints
	 * @return a diagnose
	 */
	private List<Req4Csp> fd(List<Req4Csp> D, List<Req4Csp> C, List<Req4Csp> AC) {


		boolean isConsistent = consistent(AC) ;
		int q = C.size();


		if (!D.isEmpty()) {
			if (isConsistent) {
				return Collections.emptyList();

			}

		}

		if (q == 1) {
			return new LinkedList<Req4Csp>(C);
		}

		int k = q / 2;
		List<Req4Csp> C1 = C.subList(0, k);
		List<Req4Csp> C2 = C.subList(k, q);

		List<Req4Csp> ACWithoutC2 = diffListsAsSets(AC, C2);
		List<Req4Csp> D1 = fd(C2, C1, ACWithoutC2);

		List<Req4Csp> ACWithoutD1 = diffListsAsSets(AC, D1);
		List<Req4Csp> D2 = fd(D1, C2, ACWithoutD1);



		return appendListsAsSets(D1, D2);
	}


	public static List<Req4Csp> appendListsAsSets(List<Req4Csp> CS1, List<Req4Csp> CS2) {
		//		System.out.print("appendListsAsSets: Cs1 =" + CS1 + ", CS2=" + CS2);
		List<Req4Csp> union = new ArrayList<>(CS1);
		if (CS2 == null)
			return union;

		for (Req4Csp c : CS2) {
			if (!union.contains(c)) {
				union.add(c);
			}
		}
		//		System.out.println(" : " + union);
		return union;
	}

	public static List<Req4Csp> diffListsAsSets(List<Req4Csp> ac, List<Req4Csp> c2) {
		//		System.out.print("diffListsAsSets: ac =" + ac + ", c2=" + c2);
		List<Req4Csp> diff = new ArrayList<Req4Csp>();
		for (Req4Csp requirement : ac) {
			if (!c2.contains(requirement)) {
				diff.add(requirement);
			}

		}
		//		System.out.println(" : " + diff);
		return diff;
	}

}
