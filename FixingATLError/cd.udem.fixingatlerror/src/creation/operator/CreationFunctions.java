package creation.operator;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.m2m.atl.core.emf.EMFModel;
import anatlyzer.atl.model.ATLModel;
import anatlyzer.atlext.ATL.Binding;
import anatlyzer.atlext.ATL.OutPatternElement;
import anatlyzer.atlext.ATL.Rule;
import cd.udem.fixingatlerror.CommonFunctionOperators;
import evaluation.mutator.AbstractMutator;
import jmetal.core.Solution;
import jmetal.metaheuristics.nsgaII.NSGAIIThreeStep;
import witness.generator.MetaModel;

public class CreationFunctions extends AbstractMutator {

	ATLModel wrapper;

	public CreationFunctions(ATLModel wrapper) {
		this.wrapper = wrapper;

	}

	public List<Object> applyChange(int randomInt, OutPatternElement outPatternElement, List<Object> ReturnResult,
			int randomInt2, String comment, ATLModel wrapper, EMFModel atlModel, String str) {
		// TODO Auto-generated method stub

		comment = "\n-- MUTATION \"" + this.getDescription() + "\" " + str + " in " + toString(outPatternElement)
				+ " (line " + outPatternElement.getLocation() + " of original transformation)\n";

		System.out
				.println("\n-- MUTATION \"" + this.getDescription() + "\" " + str + " in " + toString(outPatternElement)
						+ " (line " + outPatternElement.getLocation() + " of original transformation)\n");
		NSGAIIThreeStep.numop = NSGAIIThreeStep.numop + 1;

		ReturnResult.set(0, wrapper);
		ReturnResult.set(1, atlModel);
		ReturnResult.add(comment);

		return ReturnResult;

	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Creation of Binding";
	}

	@Override
	public List<Object> generateMutants(EMFModel atlModel, MetaModel inputMM, MetaModel outputMM, String outputFolder,
			ATLModel wrapper, Solution solution,CommonFunctionOperators cp) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Binding> getExistingBinding(List<Rule> modifiable, int randomInt, int randomInt2) {
		// TODO Auto-generated method stub
		EStructuralFeature feature = this.wrapper
				.source(modifiable.get(randomInt).getOutPattern().getElements().get(randomInt2)).eClass()
				.getEStructuralFeature("bindings");
		List<Binding> realbindings = (List<Binding>) this.wrapper
				.source(modifiable.get(randomInt).getOutPattern().getElements().get(randomInt2)).eGet(feature);
		return realbindings;
	}

	public boolean existNonExistingattributeOutpattern(ArrayList<EStructuralFeature> featurelist, Solution solution,
			int row) {
		boolean foundAttr = false; // choosr LHS is not propertyname
		for (int i = 0; i < featurelist.size(); i++)
			if (!solution.getCoSolutionThreeStep().getOp().listpropertyname.get(row)
					.contains(featurelist.get(i).getName())) {
				foundAttr = true;

			}
		// TODO Auto-generated method stub
		return foundAttr;
	}

	public String findOutpattern(ATLModel wrapper, int indexoutpattern) {
		// TODO Auto-generated method stub
		List<OutPatternElement> listoutpattern = (List<OutPatternElement>) wrapper.allObjectsOf(OutPatternElement.class);
		EStructuralFeature featureDefinition = wrapper.source(listoutpattern.get(indexoutpattern)).eClass()
				.getEStructuralFeature("type");
		EObject object2modify_src = wrapper.source(listoutpattern.get(indexoutpattern));
		EObject oldFeatureValue = (EObject) object2modify_src.eGet(featureDefinition);
		
		
		return toString(oldFeatureValue);
	}

}
