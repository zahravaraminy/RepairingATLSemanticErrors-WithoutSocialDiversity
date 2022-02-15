package deletion.operator;

import java.util.List;

import org.eclipse.m2m.atl.core.emf.EMFModel;
import anatlyzer.atl.model.ATLModel;
import anatlyzer.atlext.OCL.OclExpression;
import anatlyzer.atlext.OCL.OperationCallExp;
import cd.udem.fixingatlerror.CommonFunctionOperators;
//import anatlyzer.evaluation.tester.Tester;
import jmetal.core.Solution;
import witness.generator.MetaModel;

public class ArgumentDeletionMutator extends AbstractDeletionMutator {

	@Override
	public List<Object> generateMutants(EMFModel atlModel, MetaModel inputMM, MetaModel outputMM, String outputFolder,ATLModel wrapper,
			Solution solution,CommonFunctionOperators cp) {
		// deletion of arguments in calls to called rules and helpers
		
		return this.genericDeletion(atlModel, outputFolder, OperationCallExp.class, OclExpression.class, "arguments", true,wrapper
				,solution, outputMM,cp);
	}

	@Override
	public String getDescription() {
		return "Deletion of Argument";
	}	
}
