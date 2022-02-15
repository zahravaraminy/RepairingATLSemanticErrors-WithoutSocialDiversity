package modification.type.operator.abstractclass;

import java.util.List;
import org.eclipse.m2m.atl.core.emf.EMFModel;
import witness.generator.MetaModel;
import anatlyzer.atl.model.ATLModel;
import anatlyzer.atlext.OCL.Operation;
import cd.udem.fixingatlerror.CommonFunctionOperators;
import jmetal.core.Solution;

public class HelperReturnModificationMutator extends AbstractTypeModificationMutator {
	
	@Override
	public List<Object> generateMutants(EMFModel atlModel, MetaModel inputMM, MetaModel outputMM, String outputFolder,
			ATLModel wrapper,
			Solution solution,CommonFunctionOperators cp) {
		return this.genericTypeModification(atlModel, outputFolder, Operation.class, "returnType", new MetaModel[] {inputMM, outputMM},
				true,wrapper, solution,inputMM,outputMM);
}
	
	@Override
	public String getDescription() {
		return "Helper Return Type Modification";
	}
}
