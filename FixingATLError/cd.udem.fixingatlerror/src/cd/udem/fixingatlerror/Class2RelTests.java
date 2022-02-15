package cd.udem.fixingatlerror;

import java.util.ArrayList;

import org.eclipse.m2m.atl.core.IModel;
import org.eclipse.m2m.atl.core.IReferenceModel;
import org.eclipse.m2m.atl.core.ModelFactory;
import org.eclipse.m2m.atl.core.emf.EMFModel;
import org.eclipse.m2m.atl.core.emf.EMFModelFactory;
import org.eclipse.m2m.atl.engine.parser.AtlParser;

import anatlyzer.atl.analyser.Analyser;
import anatlyzer.atl.util.ATLSerializer;
import anatlyzer.atlext.ATL.ATLFactory;
import anatlyzer.atlext.ATL.Binding;
import anatlyzer.atlext.ATL.CalledRule;
import anatlyzer.atlext.ATL.Helper;
import anatlyzer.atlext.ATL.InPattern;
import anatlyzer.atlext.ATL.LazyRule;
import anatlyzer.atlext.ATL.MatchedRule;
import anatlyzer.atlext.ATL.Module;
import anatlyzer.atlext.ATL.ModuleElement;
import anatlyzer.atlext.ATL.OutPattern;
import anatlyzer.atlext.ATL.OutPatternElement;
import jmetal.util.PseudoRandom;

public class Class2RelTests extends BaseTest {
	public   String TRANSFORMATION ;
	public String CLASS_METAMODEL;
	public String REL_METAMODEL;

	Class2RelTests(){
		 TRANSFORMATION = "examples/class2rel/trafo/class2relational.atl";

		 CLASS_METAMODEL  =  "examples/class2rel/metamodels/class.ecore";
	 REL_METAMODEL   = "examples/class2rel/metamodels/relational.ecore";
	
		
	}

	
	public static  void main(String[] args) throws Exception {
		new Class2RelTests().run();
		//run();
	}

	public   void run() throws Exception {
		// test to load transformation, modify, and write back 
		
		//Operations op=new Operations(); 
		//op.ModuleNamemodificationOperator();
		
		// load the model
//		typing(TRANSFORMATION, new Object[] { CLASS_METAMODEL, REL_METAMODEL }, 
//				   new String[] { "Class", "Rel" }, true);
		Analyser analyser = getAnalyser();
		
		// change name of transformation module
		Module module = analyser.getATLModel().allObjectsOf(Module.class).get(0);
		module.setName("cl2rel");
		
		ArrayList<MatchedRule> L=new ArrayList<MatchedRule>();
		
		for (ModuleElement e : module.getElements()) {
			if (e instanceof MatchedRule) {
				MatchedRule mr = (MatchedRule) e;
				mr.setName(mr.getName()+"_wextendeddd");
				System.out.println("W  "+mr.getOutPattern().getElements().get(0).getBindings().get(0).getPropertyName());
				
			} 
		}
		int rMr;
		 int rEl;
		 int rBind;
			for (ModuleElement e : module.getElements()) {
					if (e instanceof MatchedRule) {
						L.add((MatchedRule)e);
					}
				}
				rMr=PseudoRandom.randInt(0,L.size());
			
				MatchedRule mr=L.get(rMr);
				rEl=PseudoRandom.randInt(0,mr.getOutPattern().getElements().size());
				rBind=PseudoRandom.randInt(0,mr.getOutPattern().getElements().get(0).getBindings().size());
				System.out.println("Mr size="+L.size());
				System.out.println("rMr= "+rMr+" rEl= "+rEl+" rBind= "+rBind);
				///////////////////////////////

				// change binding with another one
				 mr=L.get(1);
				String s =mr.getOutPattern().getElements().get(0).getBindings().get(1).getPropertyName();
				mr.getOutPattern().getElements().get(0).getBindings().get(0).setPropertyName(s);
				mr.getOutPattern().getElements().get(0).getBindings().remove(0);

				// template for deleting an ATL rule
				//	module.getElements().remove(0);
				// template for creating a new ATL Rule
				ATLFactory atlFactory = ATLFactory.eINSTANCE;
				MatchedRule newRule = atlFactory.createMatchedRule();
				newRule.setName("TestRule");
				InPattern iP = atlFactory.createInPattern();
				Binding b=atlFactory.createBinding();
				OutPattern oP = atlFactory.createOutPattern();
				newRule.setInPattern(iP);
				newRule.setOutPattern(oP);
				atlFactory.createSimpleInPatternElement().setId("www");
				module.getElements().add(newRule);
				// write back the model to text file
				ATLSerializer.serialize(analyser.getATLModel(),  "examples/class2rel/trafo/class2relational_modified.atl");

	}
	
	
	
	
	
}