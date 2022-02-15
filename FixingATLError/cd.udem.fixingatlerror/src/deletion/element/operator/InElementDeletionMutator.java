package deletion.element.operator;

import java.util.List;

import org.eclipse.m2m.atl.core.emf.EMFModel;

import witness.generator.MetaModel;
import anatlyzer.atl.model.ATLModel;
import anatlyzer.atlext.ATL.InPattern;
import anatlyzer.atlext.ATL.PatternElement;
import cd.udem.fixingatlerror.CommonFunctionOperators;
import deletion.operator.AbstractDeletionMutator;
import jmetal.core.Solution;

public class InElementDeletionMutator extends AbstractDeletionMutator {

	@Override
	public List<Object> generateMutants(EMFModel atlModel, MetaModel inputMM, MetaModel outputMM, String outputFolder,
			ATLModel wrapper,
			Solution solution,CommonFunctionOperators cp) {
		System.out.println("Inelement deletion");
		return this.genericDeletion(atlModel, outputFolder, InPattern.class, PatternElement.class, "elements",true,wrapper
				,solution, outputMM,cp);
	}

	@Override
	public String getDescription() {
		return "Deletion of InPattern Element";
	}	
}

