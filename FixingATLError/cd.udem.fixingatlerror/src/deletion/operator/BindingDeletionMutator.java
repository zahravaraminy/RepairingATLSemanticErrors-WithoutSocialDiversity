package deletion.operator;

import java.util.List;
import org.eclipse.m2m.atl.core.emf.EMFModel;
import anatlyzer.atl.model.ATLModel;
import anatlyzer.atlext.ATL.Binding;
import anatlyzer.atlext.ATL.OutPatternElement;
import cd.udem.fixingatlerror.CommonFunctionOperators;
import jmetal.core.Solution;
import witness.generator.MetaModel;

public class BindingDeletionMutator extends AbstractDeletionMutator {

	@Override
	public List<Object> generateMutants(EMFModel atlModel, MetaModel inputMM, MetaModel outputMM, String outputFolder,ATLModel wrapper,Solution solution,CommonFunctionOperators cp) {
		List<Object> comments;
		 comments=this.genericDeletion(atlModel, outputFolder, OutPatternElement.class, Binding.class, "bindings",wrapper,solution,outputMM,cp);
		return comments;
	}

	@Override
	public String getDescription() {
		return "Deletion of Binding";
	}
}
