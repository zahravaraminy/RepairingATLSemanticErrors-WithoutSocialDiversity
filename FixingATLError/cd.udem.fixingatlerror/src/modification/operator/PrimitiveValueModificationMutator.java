package modification.operator;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.m2m.atl.core.emf.EMFModel;

import witness.generator.MetaModel;
import anatlyzer.atl.model.ATLModel;
import anatlyzer.atlext.OCL.BooleanExp;
import anatlyzer.atlext.OCL.IntegerExp;
import anatlyzer.atlext.OCL.RealExp;
import anatlyzer.atlext.OCL.StringExp;
import cd.udem.fixingatlerror.Class2Rel;
import cd.udem.fixingatlerror.CommonFunctionOperators;
import jmetal.core.Solution;

public class PrimitiveValueModificationMutator extends AbstractModificationMutator {

	@Override
	public List<Object> generateMutants(EMFModel atlModel, MetaModel inputMM, MetaModel outputMM, String outputFolder,
			ATLModel wrapper,
			Solution solution,CommonFunctionOperators cp) {
		Class2Rel.typeoperation="primitive";
		List<Object> comment1;
		List<Object> comment2;
		List<Object> comment3;
		List<Object> comment4=null;
		comment1=this.genericAttributeModification(atlModel, outputFolder, BooleanExp.class, "booleanSymbol", outputMM,
				 wrapper,
				solution);
		comment2=this.genericAttributeModification(atlModel, outputFolder, StringExp.class,  "stringSymbol",  outputMM,
				wrapper,
				solution);
		comment3=this.genericAttributeModification(atlModel, outputFolder, IntegerExp.class, "integerSymbol", outputMM,
				wrapper,
				solution);
		comment4=this.genericAttributeModification(atlModel, outputFolder, RealExp.class,    "realSymbol",    outputMM,
				wrapper,
				solution);
		Class2Rel.typeoperation=null;
	//	return new StringBuilder().append(comment1).append(comment2).append(comment3).append(comment4).toString();
		return comment1;
	}

	@Override
	public String getDescription() {
		return "Primitive Value Modification";
	}

	@Override
	protected List<Object> replacements(EMFModel atlModel, EClass oldFeatureValue,EObject object2modify, String currentAttributeValue, MetaModel metamodel) {
		List<Object> replacements = new ArrayList<Object>();
		if      (object2modify instanceof BooleanExp) replacements.add( !((BooleanExp)object2modify).isBooleanSymbol() );
		else if (object2modify instanceof StringExp)  replacements.add( ((StringExp)  object2modify).getStringSymbol().isEmpty()? "dummy" : "" );
		else if (object2modify instanceof IntegerExp) replacements.add( ((IntegerExp) object2modify).getIntegerSymbol()+1 );
		else if (object2modify instanceof RealExp)    replacements.add( ((RealExp)    object2modify).getRealSymbol()+1 );
		return replacements;
	}
}
