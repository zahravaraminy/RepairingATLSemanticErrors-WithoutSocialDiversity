package modification.type.operator.abstractclass;

import java.util.List;
import org.eclipse.m2m.atl.core.emf.EMFModel;
import witness.generator.MetaModel;
import anatlyzer.atl.model.ATLModel;
import anatlyzer.atlext.OCL.OperationCallExp;
import cd.udem.fixingatlerror.Class2Rel;
import cd.udem.fixingatlerror.CommonFunctionOperators;
import jmetal.core.Solution;


public class ArgumentModificationMutator extends AbstractTypeModificationMutator {
	
	@Override
	public List<Object> generateMutants(EMFModel atlModel, MetaModel inputMM, MetaModel outputMM, String outputFolder,ATLModel wrapper,Solution solution,CommonFunctionOperators cp) {
	
		Class2Rel.typeoperation="argu";
		List<Object> comments;
		comments=
		this.genericTypeModification(atlModel, outputFolder, OperationCallExp.class, "arguments", new MetaModel[] {inputMM, outputMM},wrapper,solution,inputMM , outputMM);
		Class2Rel.typeoperation=null;
		return comments;
	
	}
	
	
	@Override
	public String getDescription() {
		return "Argument Type Modification";
	}
}
