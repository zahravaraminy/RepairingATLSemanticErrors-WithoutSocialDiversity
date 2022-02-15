package modification.type.operator.abstractclass;

import java.util.List;
import org.eclipse.m2m.atl.core.emf.EMFModel;
import witness.generator.MetaModel;
import anatlyzer.atl.model.ATLModel;
import anatlyzer.atlext.ATL.OutPatternElement;
import cd.udem.fixingatlerror.Class2Rel;
import cd.udem.fixingatlerror.CommonFunctionOperators;
import jmetal.core.Solution;

public class OutElementModificationMutatorThreeStep extends AbstractTypeModificationMutatorThreeStep {
	
	@Override
	public List<Object> generateMutants(EMFModel atlModel, MetaModel inputMM, MetaModel outputMM, String outputFolder,ATLModel wrapper,Solution solution,
			CommonFunctionOperators cp) {
	
		Class2Rel.typeoperation="outelement";
		List<Object> comments;
		comments=
		this.genericTypeModification(atlModel, outputFolder, OutPatternElement.class, "type", new MetaModel[] {outputMM},wrapper,solution,inputMM,outputMM,cp);
		Class2Rel.typeoperation=null;
		return comments;
	 
	}
	
	@Override
	public String getDescription() {
		return "OutPattern Element Modification";
	}
}
