package modification.invocation.operator.abstractclass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.m2m.atl.core.ATLCoreException;
import org.eclipse.m2m.atl.core.emf.EMFModel;

import witness.generator.MetaModel;
import anatlyzer.atl.model.ATLModel;
import anatlyzer.atlext.OCL.OperationCallExp;
import cd.udem.fixingatlerror.Class2Rel;
import cd.udem.fixingatlerror.CommonFunctionOperators;
import jmetal.core.Solution;

public class PredefinedOperationModificationMutator extends AbstractInvocationModificationMutator {
	
	@Override
	public List<Object> generateMutants(EMFModel atlModel, MetaModel inputMM, MetaModel outputMM, String outputFolder,ATLModel wrapper,Solution solution,CommonFunctionOperators cp) {
		Class2Rel.typeoperation="Predefined";
		List<Object> comment=null;
		try {
			comment=
			this.genericAttributeModification(atlModel, outputFolder, OperationCallExp.class, "operationName", outputMM, true,wrapper,solution);
		} catch (ATLCoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Class2Rel.typeoperation=null;
		return comment;
		
	}

	@Override
	public String getDescription() {
		return "Operation Call Modification";
	}
	
	@Override
	protected TYPE getReturnType(String operation) {
		if      (returns_boolean.contains(operation)) return TYPE.BOOLEAN;
		else if (returns_ocltype.contains(operation)) return TYPE.OCLTYPE; 
		else if (returns_number.contains (operation)) return TYPE.NUMBER;
		else if (returns_string.contains (operation)) return TYPE.STRING;
		else return TYPE.UNDEFINED;
	}

	@Override
	protected TYPE getParamType(String operation) {
		if      (param_none.contains   (operation)) return TYPE.NONE;
		else if (param_ocltype.contains(operation)) return TYPE.OCLTYPE; 
		else if (param_number.contains (operation)) return TYPE.NUMBER;
		else if (param_string.contains (operation)) return TYPE.STRING;
		else return TYPE.UNDEFINED;
	}
	
	private final static List<String> returns_boolean = Arrays.asList ( new String[]{"oclIsUndefined", "oclIsKindOf", "oclIsTypeOf", "startsWith", "endsWith"} ); 
	private final static List<String> returns_string  = Arrays.asList ( new String[]{"toString", "concat", "trim"} ); 
	private final static List<String> returns_ocltype = Arrays.asList ( new String[]{"oclType"} ); 
	private final static List<String> returns_number  = Arrays.asList ( new String[]{"max", "min", "exp", "log", "floor", "size", "toInteger", "toReal", "indexOf", "lastIndexOf", "abs"} ); 
	private final static List<String> param_none      = Arrays.asList ( new String[]{"oclIsUndefined", "toString", "oclType", "floor", "size", "toInteger", "toReal", "trim", "abs"} ); 
	private final static List<String> param_string    = Arrays.asList ( new String[]{"concat", "startsWith", "endsWith", "indexOf", "lastIndexOf"} );
	private final static List<String> param_ocltype   = Arrays.asList ( new String[]{"oclIsKindOf", "oclIsTypeOf"} );
	private final static List<String> param_number    = Arrays.asList ( new String[]{"max", "min", "exp", "log"} );
	
	@Override
	protected List<String> getOperationReturning(TYPE type) {
		if      (type==TYPE.BOOLEAN) return returns_boolean;
		else if (type==TYPE.STRING)  return returns_string;
		else if (type==TYPE.OCLTYPE) return returns_ocltype;
		else if (type==TYPE.NUMBER)  return returns_number;
		else return new ArrayList<String>();
	}

	@Override
	protected List<String> getOperationReceiving(TYPE type) {
		if      (type==TYPE.NONE)    return param_none;
		else if (type==TYPE.STRING)  return param_string;
		else if (type==TYPE.OCLTYPE) return param_ocltype;
		else if (type==TYPE.NUMBER)  return param_number;
		else return new ArrayList<String>();
	}
}
