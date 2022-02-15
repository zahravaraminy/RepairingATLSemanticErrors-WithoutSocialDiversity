package modification.type.operator.abstractclass;

import java.util.List;
import org.eclipse.m2m.atl.core.emf.EMFModel;
import witness.generator.MetaModel;
import anatlyzer.atl.model.ATLModel;
import anatlyzer.atlext.OCL.CollectionType;
import cd.udem.fixingatlerror.Class2Rel;
import cd.udem.fixingatlerror.CommonFunctionOperators;
import jmetal.core.Solution;

public class CollectionModificationMutatorThreeStep extends AbstractTypeModificationMutatorThreeStep {
	
	@Override
	public List<Object> generateMutants(EMFModel atlModel, MetaModel inputMM, MetaModel outputMM, String outputFolder,ATLModel wrapper,Solution solution,CommonFunctionOperators cp) {
		Class2Rel.typeoperation="collectionelement";
		List<Object> comments;
		comments=
		 this.genericTypeModification(atlModel, outputFolder, CollectionType.class, "elementType", new MetaModel[] {inputMM, outputMM},wrapper,solution,inputMM,outputMM,cp);
		Class2Rel.typeoperation=null;
		return comments;
	}
	
	@Override
	public String getDescription() {
		return "Collection Type Modification";
	}
}
