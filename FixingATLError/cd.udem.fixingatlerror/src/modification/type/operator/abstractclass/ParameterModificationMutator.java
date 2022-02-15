package modification.type.operator.abstractclass;

import java.util.List;
import org.eclipse.m2m.atl.core.emf.EMFModel;
import witness.generator.MetaModel;
import anatlyzer.atl.model.ATLModel;
import anatlyzer.atlext.OCL.Parameter;
import cd.udem.fixingatlerror.CommonFunctionOperators;
import jmetal.core.Solution;

public class ParameterModificationMutator extends AbstractTypeModificationMutator {
	
	@Override
	public List<Object> generateMutants(EMFModel atlModel, MetaModel inputMM, MetaModel outputMM, String outputFolder,
			ATLModel wrapper,
			Solution solution,CommonFunctionOperators cp) {
		System.out.println("parameter modify");
	return	this.genericTypeModification(atlModel, outputFolder, Parameter.class, "type", new MetaModel[] {inputMM, outputMM},
				true,wrapper, solution,inputMM,outputMM);
	   
	
	}
	
	@Override
	public String getDescription() {
	//	Tester.str="parameter";
		return "Parameter Type Modification";
	}
}
