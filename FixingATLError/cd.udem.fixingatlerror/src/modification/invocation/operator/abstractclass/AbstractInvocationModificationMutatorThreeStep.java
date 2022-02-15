package modification.invocation.operator.abstractclass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.m2m.atl.core.emf.EMFModel;

import witness.generator.MetaModel;
import modification.operator.AbstractModificationMutator;
import modification.operator.AbstractModificationMutatorThreeStep;
import cd.udem.fixingatlerror.Operations;
import cd.udem.fixingatlerror.OperationsThreeStep;

public abstract class AbstractInvocationModificationMutatorThreeStep extends AbstractModificationMutatorThreeStep {
	
	/**
	 * It returns the return type of an operation.
	 * @param operation
	 * @return
	 */
	protected abstract TYPE getReturnType(String operation);
	
	/**
	 * It returns the parameter type of an operation.
	 * @param operation
	 * @return
	 */
	protected abstract TYPE getParamType (String operation);
	
	/**
	 * It returns the list of operations with the given return type.
	 * @param returnType
	 * @return
	 */
	protected abstract List<String> getOperationReturning (TYPE type);
	
	/**
	 * It returns the list of operations with the given parameter type.
	 * @param returnType
	 * @return
	 */
	protected abstract List<String> getOperationReceiving (TYPE type);

	/**
	 * It returns the list of operations that replace a given one: an operation with compatible return and parameter
	 * types, an operation with incompatible return and parameter types, an operation with compatible return type
	 * and incompatible parameter type, and an operation with incompatible return type and compatible parameter type.
	 * @param object2modify not used
	 * @param currentAttributeValue name of operation to replace
	 * @param metamodel no used
	 */   
	@Override
	protected List<Object> replacements(EMFModel atlModel, EClass oldFeatureValue,EObject object2modify, String currentAttributeValue, MetaModel metamodel) {		
		List<Object> replacements = new ArrayList<Object>();
		List<String> options       = new ArrayList<String>();
		List<String> temporarylist = null ;
		
		if(OperationsThreeStep.statecase.equals("case5"))
		 temporarylist    = Arrays.asList ( new String[]{"isEmpty", "notEmpty", "includes", "excludes", "includesAll", "excludesAll","size", "sum", "count", "indexOf","union", "intersection"} ); 
	//	if(OperationsThreeStep.statecase.equals("case2"))
	//		 temporarylist    = Arrays.asList ( new String[]{"oclIsKindOf", "startsWith", "endsWith", "concat", "trim", "max","min", "exp", "log", "floor","size", "toInteger"
	//				 , "toReal", "indexOf","lastIndexOf", "abs"} ); 
		
		if(OperationsThreeStep.statecase.equals("case3"))
			 temporarylist    = Arrays.asList ( new String[]{"select", "collect", "reject", "forAll", "isUnique"} );
			
		// obtain operations to be used as replacements
		TYPE returnType = getReturnType(currentAttributeValue);
		TYPE paramType  = getParamType (currentAttributeValue);	
		if (returnType!=TYPE.UNDEFINED && paramType!=TYPE.UNDEFINED) {
			options=( compatibleOperation   (returnType, paramType, currentAttributeValue) ); // compatible return type and compatible parameter type
	//		options.add( incompatibleOperation1(returnType, paramType, currentAttributeValue) ); // compatible return type and incompatible parameter type
	//		options.add( incompatibleOperation2(returnType, paramType, currentAttributeValue) ); // incompatible return type and compatible parameter type
	//		options.add( incompatibleOperation3(returnType, paramType, currentAttributeValue) ); // incompatible return type and incompatible parameter type

			// create list of replacements (name of selected features)  
			for (String option : options) 
				if (option!=null) 
					replacements.add(option);
		}

	//	for (String option : temporarylist) 
	//		if (option!=null) 
	//			replacements.add(option);
//		System.out.println(object2modify.eClass().getName() + ": " + currentAttributeValue + " -> " + replacements);
		
		return replacements;	   
	}
	
	protected enum TYPE { UNDEFINED, NONE, ANY, BOOLEAN, NUMBER, COLLECTION, STRING, OCLTYPE, EXPRESSION } 

	/**
	 * It returns an operation with compatible return type and compatible parameter type. 
	 * @param returnType
	 * @param paramType
	 * @param operation
	 * @return
	 */
	private List<String> compatibleOperation (TYPE returnType, TYPE paramType, String operation) {
		// select set of operations with compatible return type
		List<String> operations_r = getOperationReturning(returnType);
		// select set of operations with compatible parameter type
		List<String> operations_p  = getOperationReceiving(paramType);
		// select operations in both sets
		List<String> operations = new ArrayList<String>();
		for (String op : operations_r)
			if (operations_p.contains(op) && !op.equals(operation))
				
				/*if((operation.equals("select") || operation.equals("reject")) )
					{if(!op.equals("collect"))
					    operations.add(op);}
				else if (operation.equals("collect") && !op.equals("select") && !op.equals("reject") && !op.equals("sortedBy"))
					
						operations.add(op);
				else if (operation.equals("sortedBy") && !op.equals("collect") )
					
					operations.add(op);
				else if (operation.equals("flatten") && !op.equals("asBag") && !op.equals("asSequence") && !op.equals("asSet"))
					
					operations.add(op);
				else if(!operation.equals("select") && !operation.equals("reject") && !operation.equals("collect") && !operation.equals("sortedBy") && !operation.equals("flatten"))*/
				operations.add(op);
				
		if(operation.equals("abs") || operation.equals("lastIndexOf"))
			operations.add("oclIsUndefined");
		if(operation.equals("oclIsUndefined"))
		{
		//		operations.add("oclIsKindOf");
		//		operations.add("oclIsTypeOf");
		}
		if(operation.equals("exists") ||  operation.equals("forAll") 
				|| operation.equals("one") || operation.equals("isUnique"))
		{
				operations.add("collect");
				operations.add("sortedBy");
				
		}
		if(operation.equals("excludesAll") ||  operation.equals("includesAll") ){
			
			operations.add("includes");
			
		}
		
		if(operation.equals("flatten") ||  operation.equals("asBag") 
				|| operation.equals("asSequence") ||
				operation.equals("asSet") || operation.equals("isEmpty") ) {
			operations.add("includes");
			
		}
		if(operation.equals("sum") || operation.equals("indexOf")) {
			operations.add("union");
		}
		if(operation.equals("forAll")) {
			operations.add("select");
		}
		
		if(operation.equals("max") || operation.equals("min") ||  operation.equals("exp")
				||  operation.equals("log")) {
			operations.add("oclIsKindOf");
			operations.add("oclIsUndefined");
		}
		if(operation.equals("endsWith") || operation.equals("startsWidth") ||  operation.equals("floor")
				||  operation.equals("size") ||  operation.equals("toInteger") 
				|| operation.equals("toReal")) {
			operations.add("oclIsKindOf");
	//		operations.add("oclIsTypeOf");
			operations.add("oclIsUndefined");
		}
		return operations;
		// select one of the possible operations at random
		//return operations.size()==0? null : operations.get(new Random().nextInt(operations.size()));
	}

	/**
	 * It returns an operation with compatible return type and incompatible parameter type. 
	 * @param returnType
	 * @param paramType
	 * @param operation
	 * @return
	 */
	private String incompatibleOperation1 (TYPE returnType, TYPE paramType, String operation) {
		// select set of operations with compatible return type
		List<String> operations_r  = getOperationReturning(returnType);
		// select set of operations with incompatible parameter type
		List<String> operations_p = new ArrayList<String>();
		if (paramType != TYPE.NONE)       operations_p.addAll(getOperationReceiving(TYPE.NONE));
		if (paramType != TYPE.ANY)        operations_p.addAll(getOperationReceiving(TYPE.ANY));
		if (paramType != TYPE.BOOLEAN)    operations_p.addAll(getOperationReceiving(TYPE.BOOLEAN));
		if (paramType != TYPE.NUMBER)     operations_p.addAll(getOperationReceiving(TYPE.NUMBER));
		if (paramType != TYPE.COLLECTION) operations_p.addAll(getOperationReceiving(TYPE.COLLECTION));
		if (paramType != TYPE.STRING)     operations_p.addAll(getOperationReceiving(TYPE.STRING));
		if (paramType != TYPE.OCLTYPE)    operations_p.addAll(getOperationReceiving(TYPE.OCLTYPE));
		// select operations in both sets
		List<String> operations = new ArrayList<String>();
		for (String op : operations_r)
			if (operations_p.contains(op) && !op.equals(operation))
				operations.add(op);
		// select one of the possible operations at random
		return operations.size()==0? null : operations.get(new Random().nextInt(operations.size()));
	}

	/**
	 * It returns an operation with incompatible return type and compatible parameter type. 
	 * @param returnType
	 * @param paramType
	 * @param operation
	 * @return
	 */
	private String incompatibleOperation2 (TYPE returnType, TYPE paramType, String operation) {
		// select set of operations with compatible return type
		List<String> operations_r = new ArrayList<String>();
		if (returnType != TYPE.NONE)       operations_r.addAll(getOperationReturning(TYPE.NONE));
		if (returnType != TYPE.ANY)        operations_r.addAll(getOperationReturning(TYPE.ANY));
		if (returnType != TYPE.BOOLEAN)    operations_r.addAll(getOperationReturning(TYPE.BOOLEAN));
		if (returnType != TYPE.NUMBER)     operations_r.addAll(getOperationReturning(TYPE.NUMBER));
		if (returnType != TYPE.COLLECTION) operations_r.addAll(getOperationReturning(TYPE.COLLECTION));
		if (returnType != TYPE.STRING)     operations_r.addAll(getOperationReceiving(TYPE.STRING));
		if (returnType != TYPE.OCLTYPE)    operations_r.addAll(getOperationReceiving(TYPE.OCLTYPE));
		// select set of operations with incompatible parameter type
		List<String> operations_p  = getOperationReceiving(paramType);
		// select operations in both sets
		List<String> operations = new ArrayList<String>();
		for (String op : operations_p)
			if (operations_r.contains(op) && !op.equals(operation))
				operations.add(op);
		// select one of the possible operations at random
		return operations.size()==0? null : operations.get(new Random().nextInt(operations.size()));
	}

	/**
	 * It returns an operation with incompatible return type and incompatible parameter type. 
	 * @param returnType
	 * @param paramType
	 * @param operation
	 * @return
	 */
	private String incompatibleOperation3 (TYPE returnType, TYPE paramType, String operation) {
		// select set of operations with incompatible return type
		List<String> operations_r = new ArrayList<String>();
		if (returnType != TYPE.NONE)       operations_r.addAll(getOperationReturning(TYPE.NONE));
		if (returnType != TYPE.ANY)        operations_r.addAll(getOperationReturning(TYPE.ANY));
		if (returnType != TYPE.BOOLEAN)    operations_r.addAll(getOperationReturning(TYPE.BOOLEAN));
		if (returnType != TYPE.NUMBER)     operations_r.addAll(getOperationReturning(TYPE.NUMBER));
		if (returnType != TYPE.COLLECTION) operations_r.addAll(getOperationReturning(TYPE.COLLECTION));
		if (returnType != TYPE.STRING)     operations_r.addAll(getOperationReturning(TYPE.STRING));
		if (returnType != TYPE.OCLTYPE)    operations_r.addAll(getOperationReturning(TYPE.OCLTYPE));
		// select set of operations with incompatible parameter type
		List<String> operations_p = new ArrayList<String>();
		if (paramType != TYPE.NONE)       operations_p.addAll(getOperationReceiving(TYPE.NONE));
		if (paramType != TYPE.ANY)        operations_p.addAll(getOperationReceiving(TYPE.ANY));
		if (paramType != TYPE.BOOLEAN)    operations_p.addAll(getOperationReceiving(TYPE.BOOLEAN));
		if (paramType != TYPE.NUMBER)     operations_p.addAll(getOperationReceiving(TYPE.NUMBER));
		if (paramType != TYPE.COLLECTION) operations_p.addAll(getOperationReceiving(TYPE.COLLECTION));
		if (paramType != TYPE.STRING)     operations_p.addAll(getOperationReceiving(TYPE.STRING));
		if (paramType != TYPE.OCLTYPE)    operations_p.addAll(getOperationReceiving(TYPE.OCLTYPE));
		// select operations in both sets
		List<String> operations = new ArrayList<String>();
		for (String op : operations_r)
			if (operations_p.contains(op) && !op.equals(operation))
				operations.add(op);
		// select one of the possible operations at random
		return operations.size()==0? null : operations.get(new Random().nextInt(operations.size()));
	}
}
