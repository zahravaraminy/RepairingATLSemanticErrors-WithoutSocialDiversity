package modification.operator;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EDataTypeEList;
import org.eclipse.m2m.atl.core.ATLCoreException;
import org.eclipse.m2m.atl.core.ModelFactory;
import org.eclipse.m2m.atl.core.emf.EMFModel;
import org.eclipse.m2m.atl.core.emf.EMFModelFactory;
import org.eclipse.m2m.atl.core.emf.EMFReferenceModel;
import org.eclipse.m2m.atl.engine.parser.AtlParser;

import witness.generator.MetaModel;
import anatlyzer.atlext.ATL.MatchedRule;
import anatlyzer.atlext.ATL.Module;
import anatlyzer.atlext.ATL.Rule;
import anatlyzer.atlext.ATL.RuleWithPattern;
import cd.udem.fixingatlerror.CommonFunctionOperators;
//import anatlyzer.evaluation.mutators.ATLModel;
import anatlyzer.atl.model.ATLModel;
import evaluation.mutator.AbstractMutator;
import deletion.operator.AbstractDeletionMutator;
//import anatlyzer.evaluation.tester.Tester;
import jmetal.core.Solution;

public class ParentRuleModificationMutator extends AbstractMutator {

	private String m="C:/Users/varaminz/Downloads/Project/workspaceTEST34/anatlyzer.atl.tests.api/trafo2/zahra2mutants/finalresult.atl";
	 private EMFModel atlModel3;
	
	 private EMFModel loadTransformationModel (String atlTransformationFile) throws ATLCoreException {
			ModelFactory      modelFactory = new EMFModelFactory();
			
			EMFReferenceModel atlMetamodel = (EMFReferenceModel)modelFactory.getBuiltInResource("ATL.ecore");
			
			//EMFReferenceModel atlMetamodel = (EMFReferenceModel)modelFactory.getBuiltInResource("C:\\Users\\wael\\OneDrive\\workspaceATLoperations\\mutants\\anatlyzer.evaluation.mutants\\ATL.ecore");
			AtlParser         atlParser    = new AtlParser();		
			EMFModel          atlModel     = (EMFModel)modelFactory.newModel(atlMetamodel);
			atlParser.inject (atlModel, atlTransformationFile);	
			atlModel.setIsTarget(true);				
			
//			// Should we want to serialize the model.
//			String injectedFile = "file:/" + atlTransformationFile + ".xmi";
//			IExtractor extractor = new EMFExtractor();
//			extractor.extract(atlModel, injectedFile);
			
			return atlModel;
		}
		
	@Override
	public List<Object> generateMutants(EMFModel atlModel, MetaModel inputMM, MetaModel outputMM, String outputFolder,
			ATLModel wrapper,
			Solution solution,CommonFunctionOperators cp) {
		List<Object> ReturnResult = new ArrayList<Object>();
		//ATLModel  wrapper = new ATLModel(atlModel.getResource());
		
		// we will add a comment to the module, documenting the mutation 
		//Module module = AbstractDeletionMutator.getWrapper().getModule();
		try {
			this.atlModel3 = this.loadTransformationModel(this.m);
		} catch (ATLCoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.save(this.atlModel3, outputFolder);
		  wrapper = new ATLModel(this.atlModel3.getResource());
		Module module = wrapper.getModule();
		EDataTypeEList<String> comments = null;
		String comment=null;
		if (module!=null) {
			EStructuralFeature feature = wrapper.source(module).eClass().getEStructuralFeature("commentsBefore");	
			comments = (EDataTypeEList<String>)wrapper.source(module).eGet(feature);
		}
		
		List<MatchedRule> rule=(List<MatchedRule>)wrapper.allObjectsOf(MatchedRule.class);
		int randomInt=(int) (Math.random() * (rule.size()));
		// for each matched rule
		//for (MatchedRule rule : (List<MatchedRule>)wrapper.allObjectsOf(MatchedRule.class)) {
			if(rule.size()>0){
			
			// obtain current parent rule 
			EStructuralFeature feature   = wrapper.source(rule.get(randomInt)).eClass().getEStructuralFeature("superRule");
			System.out.println(feature);
			Object             superrule = wrapper.source(rule.get(randomInt)).eGet(feature);
			System.out.println(superrule);		
			// matched rules
			List<MatchedRule> parents = (List<MatchedRule>)wrapper.allObjectsOf(MatchedRule.class);
			int randomInt2=(int) (Math.random() * (parents.size()-1));
			
			// exclude itself, and other rules that would make an inheritance cycle
			List<MatchedRule> remove = new ArrayList<MatchedRule>();
			//for (MatchedRule parent : parents) {
				if(parents.size()>0){
				RuleWithPattern r = parents.get(randomInt2).getSuperRule();
				while (r!=null && r!=rule) r = r.getSuperRule();
				if (r==rule) remove.add(parents.get(randomInt2));
			}
			parents.remove(rule.get(randomInt));
			parents.removeAll(remove);
					
			// for each matched rule 
			//for (MatchedRule parent : parents) {
				if(parents.size()>0){
					System.out.println("ccccc");
					System.out.println(randomInt2);
					System.out.println(parents.size());
					System.out.println(randomInt);
					System.out.println(rule.size());
					
				// mutation: modify parent rule
				wrapper.source(rule.get(randomInt)).eSet(feature, wrapper.source(parents.get(randomInt2)));

				// mutation: documentation
				if (comments!=null) comments.add("\n-- MUTATION \"" + this.getDescription() + "\" " + toString(parents.get(randomInt2)) + " in " + toString(rule.get(randomInt)) + " (line " + rule.get(randomInt).getLocation() + " of original transformation)\n");
				comment="\n-- MUTATION \"" + this.getDescription() + "\" " + toString(parents.get(randomInt2)) + " in " + toString(rule.get(randomInt)) + " (line " + rule.get(randomInt).getLocation() + " of original transformation)\n";
				// save mutant
				this.save(this.atlModel3, outputFolder);
				 this.save2(this.atlModel3, outputFolder);
                
				// restore: remove added comment
			//	if (comments!=null) comments.remove(comments.size()-1);						
			}

			// restore original parent rule
			//wrapper.source(rule).eSet(feature, superrule);
		}
			return ReturnResult;
	}
	
	@Override
	public String getDescription() {
		return "Modification of Parent Rule";
	}
}
