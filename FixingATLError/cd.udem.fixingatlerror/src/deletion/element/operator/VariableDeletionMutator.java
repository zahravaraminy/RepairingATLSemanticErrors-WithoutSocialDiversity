package deletion.element.operator;

import java.util.List;

import org.eclipse.m2m.atl.core.emf.EMFModel;
import witness.generator.MetaModel;
import anatlyzer.atl.model.ATLModel;
import anatlyzer.atlext.ATL.Rule;
import anatlyzer.atlext.ATL.RuleVariableDeclaration;
import cd.udem.fixingatlerror.CommonFunctionOperators;
import deletion.operator.AbstractDeletionMutator;
import jmetal.core.Solution;

public class VariableDeletionMutator extends AbstractDeletionMutator {

	@Override
	public List<Object> generateMutants(EMFModel atlModel, MetaModel inputMM, MetaModel outputMM, String outputFolder,
			ATLModel wrapper,
			Solution solution,CommonFunctionOperators cp) {
		
	return	this.genericDeletion(atlModel, outputFolder, Rule.class, RuleVariableDeclaration.class, "variables",
				true,wrapper
				,solution, outputMM,cp);
	}

	@Override
	public String getDescription() {
		return "Deletion of Variable";
	}	
}