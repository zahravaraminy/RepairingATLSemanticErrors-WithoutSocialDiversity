package modification.type.operator.abstractclass;

import java.util.List;
import org.eclipse.m2m.atl.core.emf.EMFModel;
import witness.generator.MetaModel;
import anatlyzer.atl.model.ATLModel;
import anatlyzer.atlext.OCL.OclContextDefinition;
import cd.udem.fixingatlerror.CommonFunctionOperators;
import jmetal.core.Solution;

public class HelperContextModificationMutatorpost extends AbstractTypeModificationMutator {
	
	@Override
	public List<Object> generateMutants(EMFModel atlModel, MetaModel inputMM, MetaModel outputMM, String outputFolder
			,ATLModel wrapper,
			Solution solution,CommonFunctionOperators cp) {
		
		return this.genericTypeModification(atlModel, outputFolder, OclContextDefinition.class, "context_", new MetaModel[] {inputMM, outputMM},
				true,wrapper, solution,inputMM,outputMM);

	}
	
	@Override
	public String getDescription() {
		return "Helper Context Type Modification";
	}
}
