package cd.udem.fixingatlerror;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.m2m.atl.core.ATLCoreException;
import org.eclipse.m2m.atl.core.ModelFactory;
import org.eclipse.m2m.atl.core.emf.EMFModel;
import org.eclipse.m2m.atl.core.emf.EMFModelFactory;
import org.eclipse.m2m.atl.core.emf.EMFReferenceModel;
import org.eclipse.m2m.atl.engine.parser.AtlParser;
import jmetal.core.Solution;
import jmetal.metaheuristics.nsgaII.NSGAIIpostprocessing;
import jmetal.util.PseudoRandom;
import transML.exceptions.transException;
import witness.generator.MetaModel;
import anatlyzer.atl.analyser.Analyser;
import anatlyzer.atl.analyser.namespaces.GlobalNamespace;
import anatlyzer.atl.util.ATLSerializer;
import anatlyzer.atl.util.ATLUtils.ModelInfo;
import anatlyzer.atlext.ATL.ATLFactory;
import anatlyzer.atlext.ATL.InPattern;
import anatlyzer.atlext.ATL.MatchedRule;
import anatlyzer.atlext.ATL.Module;
import anatlyzer.atlext.ATL.ModuleElement;
import anatlyzer.atlext.ATL.OutPattern;
import anatlyzer.atlext.ATL.OutPatternElement;
import anatlyzer.atlext.ATL.Rule;
import anatlyzer.evaluation.models.ModelGenerationStrategy;
import evaluation.mutator.AbstractMutator;
import modification.feature.operator.BindingModificationMutatorpost;
import modification.feature.operator.NavigationModificationMutatorpost;
import modification.invocation.operator.CollectionOperationModificationMutator;
import modification.invocation.operator.abstractclass.IteratorModificationMutator;
import modification.invocation.operator.PredefinedOperationModificationMutatorpost;
import modification.type.operator.abstractclass.ArgumentModificationMutatorpost;
import modification.type.operator.abstractclass.CollectionModificationMutatorpost;
import modification.type.operator.abstractclass.InElementModificationMutatorpost;
import anatlyzer.atl.model.ATLModel;
public class Operationspostprocessing extends BaseTest {

	public ArrayList<ArrayList<String>> listpropertyname = new ArrayList<ArrayList<String>>();
	public ArrayList<ArrayList<Integer>> listpropertynamelocation = new ArrayList<ArrayList<Integer>>();
    public static String statecase=null;
    public ArrayList<ArrayList<Integer>> originalwrapper = new ArrayList<ArrayList<Integer>>();
	ATLModel model;
	Analyser analyser;
	ArrayList<MatchedRule> L;
	public ATLModel wrapper;
	private String atlFile;            // name of original transformation
	public EMFModel atlModel;         // model of the original transformation
	public static ATLModel  lastmodel;         // model of the original transformation
	public static EMFModel emfModel; 
	private GlobalNamespace namespace; // meta-models used by the transformation (union of inputMetamodels and outputMetamodels)
	private List<String> inputMetamodels  = new ArrayList<String>(); // input metamodels (IN)
	private List<String> outputMetamodels = new ArrayList<String>(); // output metamodels (OUT)
    private HashMap<String, ModelInfo> aliasToPaths = new HashMap<String, ModelInfo>();
	private ResourceSet rs;
	//private Report report;
	public static String str;
	private long initTime;
	private MetaModel iMetaModel = null, oMetaModel = null;
	private ModelGenerationStrategy.STRATEGY modelGenerationStrategy;
	 private EMFModel atlModel3;
	int populationsize=100;
	double populationsizedouble=100.0;
	// temporal folders
	private String folderMutants;
	private String folderModels;
	private String folderTemp;

	// configuration options
	private boolean generateMutants = false;
	private boolean generateTestModels = false;
	private boolean alwaysCheckRuleConflicts = true;
	
	private EPackage pack;

	
	private EMFModel loadTransformationModel (String atlTransformationFile) throws ATLCoreException {
		ModelFactory      modelFactory = new EMFModelFactory();
		
		EMFReferenceModel atlMetamodel = (EMFReferenceModel)modelFactory.getBuiltInResource("ATL.ecore");
		AtlParser         atlParser    = new AtlParser();		
		EMFModel          atlModel     = (EMFModel)modelFactory.newModel(atlMetamodel);
		atlParser.inject (atlModel, atlTransformationFile);	
		atlModel.setIsTarget(true);				
		
		return atlModel;
	}
	public List<EPackage> retPackMM(Resource resourceMM)
	 {
		ResourceSet resourceSet=resourceMM.getResourceSet();
		List<EPackage> metamodel = new ArrayList<EPackage>();
		for (EObject obj : resourceMM.getContents()) {
			if (obj instanceof EPackage) {
				EPackage.Registry.INSTANCE.put		(((EPackage)obj).getNsURI(), ((EPackage)obj).getEFactoryInstance().getEPackage());
				resourceSet.getPackageRegistry().put(((EPackage)obj).getNsURI(), ((EPackage)obj).getEFactoryInstance().getEPackage());
				metamodel.add((EPackage)obj);
			}
		}
		return metamodel;
	 }
	public Resource retPackResouceMM(String MMPath)
	 {	 	
		
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
		URI fileURI = URI.createFileURI(MMPath);//ecore.getFullPath().toOSString());		
		Resource resource = resourceSet.getResource(fileURI, true);	
		return resource;
	 }


	public void Testeroperate (String trafo, String temporalFolder) throws ATLCoreException, transException {
	
		Settingpostprocessing s=new Settingpostprocessing();
		this.atlFile  = trafo;
		this.atlModel = this.loadTransformationModel(trafo);
		this.wrapper = new ATLModel(this.atlModel.getResource());
		this.folderMutants = temporalFolder + "mutants" + File.separator;
		this.folderModels  = temporalFolder + "testmodels" + File.separator;
		this.folderTemp    = temporalFolder + "temp" + File.separator;
		//For PNML
		 final String MMRootPath     = s.gettargetmetamodel();
		 List<EPackage>   metamodels= retPackMM(retPackResouceMM(MMRootPath));
			for (EPackage p: metamodels) {
				 this.iMetaModel = new MetaModel(p); 
			 }
			//for PetriNet
			final String MMRootPath2     = s.getsourcemetamodel();
			
			List<EPackage>   metamodels2= retPackMM(retPackResouceMM(MMRootPath2));
			// List<EPackage> metamodels2 = EMFUtils.loadEcoreMetamodel("file:///D:\\java\\ATLproject\\neonssmallprojects\\anatlyzer.evaluation.mutants\\trafo\\PetriNet.ecore");
			 for (EPackage p: metamodels2) {
				 this.oMetaModel = new MetaModel(p); 
			 }
		 this.iMetaModel.setName (s.getfirstecorename());
		 this.oMetaModel.setName (s.getsecondecorename());
		 String temporalcover=temporalFolder+ "cover" + File.separator;
		 ExtractPropertyname(this.wrapper);
			
	}
	private void ExtractPropertyname(ATLModel wrapper) {
		
		 int row=0;
		 this.listpropertyname.clear();
		 this.listpropertynamelocation.clear();
		 this.originalwrapper.clear();
		 listpropertyname = new ArrayList<ArrayList<String>>();
		 listpropertynamelocation = new ArrayList<ArrayList<Integer>>();
		 originalwrapper = new ArrayList<ArrayList<Integer>>();
		 
		for (Rule rule : (List<Rule>)wrapper.allObjectsOf(Rule.class)) {
			for (OutPatternElement outElement : rule.getOutPattern().getElements()) {
				ArrayList<String> row2 = new ArrayList<String>();
				ArrayList<Integer> row3 = new ArrayList<Integer>();
				
				ArrayList<Integer> row5 = new ArrayList<Integer>();
				
				for(int j=0;j<outElement.getBindings().size();j++) 
				{
					row2.add(outElement.getBindings().get(j).getPropertyName());
					row5.add(0);
					String[] array=outElement.getBindings().get(j).getLocation().split(":", 2);
					
					row3.add(Integer.parseInt(array[0]));
				}
				row=row+1;
				
			this.listpropertyname.add(row2);
			//if(row3.size()>0)
			this.listpropertynamelocation.add(row3);
			
			this.originalwrapper.add(row5);
			}	
		}
		
	}
	private /*static*/ long index = 1;
	private String getValidNameOfFile (String outputFolder) throws IOException {
		String outputfile = null;
		String aux        = null;
		aux = File.separatorChar + "BestSolution" + ".atl";
		outputfile = outputFolder + aux;
		return outputfile;
	}
	
	private String getValidNameOfFile2 (String outputFolder) throws IOException {
		String outputfile = null;
		String aux        = null;
		aux = File.separatorChar + "BestSolution2" + ".atl";
		outputfile = outputFolder + aux;
		return outputfile;
	}
	
	public void save (EMFModel atlModel, String outputFolder) throws IOException {
		try {
			// save atl file
			String atl_transformation = this.getValidNameOfFile(outputFolder);
			AtlParser atlParser       = new AtlParser();
			atlParser.extract(atlModel, atl_transformation);
			
			// compile transformation
			String asm_transformation = atl_transformation.replace(".atl", ".asm");
		} 
		catch (ATLCoreException e) {}
	
		
	}
	

	public void save2 (EMFModel atlModel, String outputFolder) throws IOException {
		try {
			// save atl file
			String atl_transformation = this.getValidNameOfFile2(outputFolder);
			AtlParser atlParser       = new AtlParser();
			atlParser.extract(atlModel, atl_transformation);
			
			// compile transformation
			String asm_transformation = atl_transformation.replace(".atl", ".asm");
		} 
		catch (ATLCoreException e) {}
	
		
	}
	
	Operationspostprocessing() throws Exception 
    {
		//Here I have to read and convert the ATL rule
		
		Settingpostprocessing s=new Settingpostprocessing();
	    Testeroperate(NSGAIIpostprocessing.inputfaultytransformation, null);
			
		
		}
	
	public Operationspostprocessing(String atlfilepath, EMFModel atlModel2, ATLModel atlModel4) throws Exception 
    {
		
		Setting s=new Setting();
		
//			typing(atlfilepath, new Object[] {CLASS_METAMODEL,REL_METAMODEL }, 
//					   new String[] { "PetriNet", "PNML" }, true);
			typing(atlfilepath, new Object[] {s.getsourcemetamodel(),s.gettargetmetamodel() }, 
					   new String[] { s.getsecondecorename(), s.getfirstecorename()}, true,atlModel2,atlModel4);
			
		
		 analyser = getAnalyser();
		 model=analyser.getATLModel();
    }
	
	public   List<Object> executeOperations(int num,CoSolutionpostprocessing s2,int sizeoperation, Solution solution, int sumfirstfit, int sumsecondfit, int sumthirdfit, FileWriter csvWriter)
	 {
		
		    Settingpostprocessing s=new Settingpostprocessing();	
			List<Object> comments = null;
			System.out.println(num);
				try {
			
			switch (num) {
			case 1:
				statecase="case7";
				System.out.println("modify1");
				AbstractMutator[] operators = {
						
						new ArgumentModificationMutatorpost(),
					 
						};
				 comments = null;
				for (AbstractMutator operator : operators) {
					
					comments=operator.generateMutants(atlModel, this.iMetaModel, this.oMetaModel, this.folderMutants,this.wrapper,solution,null);
	
				
					}
			
				 this.wrapper=(ATLModel) comments.get(0);
					this.atlModel=(EMFModel) comments.get(1);
					 break;
				
			case 2:
				statecase="case2";
				AbstractMutator[] operators2 = {
					new PredefinedOperationModificationMutatorpost(),
						
					};
				comments = null;
				for (AbstractMutator operator : operators2) {
				
					comments=operator.generateMutants(atlModel, this.iMetaModel, this.oMetaModel, this.folderMutants,this.wrapper,solution,null);
	
					}
				
				 this.wrapper=(ATLModel) comments.get(0);
					this.atlModel=(EMFModel) comments.get(1);
				 break;
			case 3:
				statecase="case10";
				AbstractMutator[] operators3 = {
						
						new BindingModificationMutatorpost(),// in bood
						
					};
				comments = null;
				for (AbstractMutator operator : operators3) {
					
					comments=operator.generateMutants(atlModel, this.iMetaModel, this.oMetaModel, this.folderMutants,this.wrapper,solution,null);
	
			        
					}
				
			
				 this.wrapper=(ATLModel) comments.get(0);
					this.atlModel=(EMFModel) comments.get(1);
				 break;
			case 4:
				statecase="case4";
				System.out.println("modify4");
				AbstractMutator[] operators4 = {
						 new CollectionModificationMutatorpost(),// in bod
						};
		        comments = null;
				for (AbstractMutator operator : operators4) {
				
					comments=operator.generateMutants(atlModel, this.iMetaModel, this.oMetaModel, this.folderMutants,this.wrapper,solution,null);
	
					
					}
				
				 this.wrapper=(ATLModel) comments.get(0);
				 this.atlModel=(EMFModel) comments.get(1);
				 break;
			case 5:
				statecase="case5";
				System.out.println("modify5");
				AbstractMutator[] operators5 = {
						new CollectionOperationModificationMutator(),
				};
				comments = null;
				for (AbstractMutator operator : operators5) {
					
					comments=operator.generateMutants(atlModel, this.iMetaModel, this.oMetaModel, this.folderMutants,this.wrapper,solution,null);
				
					}
				
				 this.wrapper=(ATLModel) comments.get(0);
				this.atlModel=(EMFModel) comments.get(1);
				 break;
				 
			case 6:
				 
				statecase="case6";
				AbstractMutator[] operators6 = {
						 new InElementModificationMutatorpost(),
			
				};
				comments = null;
				for (AbstractMutator operator : operators6) {
					
					comments=operator.generateMutants(atlModel, this.iMetaModel, this.oMetaModel, this.folderMutants,this.wrapper,solution,null);
	
				
					}
				 this.wrapper=(ATLModel) comments.get(0);
				this.atlModel=(EMFModel) comments.get(1);
				 break;
				 
			case 7:
				 
				
				statecase="case7";
				AbstractMutator[] operators7 = {
						new ArgumentModificationMutatorpost(),
						
				};
				comments = null;
				for (AbstractMutator operator : operators7) {
					
					comments=operator.generateMutants(atlModel, this.iMetaModel, this.oMetaModel, this.folderMutants,this.wrapper,solution,null);
				
					}
				 this.wrapper=(ATLModel) comments.get(0);
				 this.atlModel=(EMFModel) comments.get(1);
				 break;
				 
			case 8:
				 
				
				statecase="case8";
				AbstractMutator[] operators8 = {
						// deletion
						
						// new BindingCreationMutator(),			
				};
				comments = null;
				for (AbstractMutator operator : operators8) {
					
					comments=operator.generateMutants(atlModel, this.iMetaModel, this.oMetaModel, this.folderMutants,this.wrapper,solution,null);
				
					}
				 this.wrapper=(ATLModel) comments.get(0);
				 this.atlModel=(EMFModel) comments.get(1);
				 break;
				
       case 9:
				 
				
				statecase="case9";
				System.out.println("modify9");
				AbstractMutator[] operators9 = {
						new NavigationModificationMutatorpost(),
						
				};
				comments = null;
				for (AbstractMutator operator : operators9) {
					
					comments=operator.generateMutants(atlModel, this.iMetaModel, this.oMetaModel, this.folderMutants,this.wrapper,solution,null);
	
				
					}
				 this.wrapper=(ATLModel) comments.get(0);
				 this.atlModel=(EMFModel) comments.get(1);
				 break;
				 
       case 10:
			 
			
			statecase="case3";
			System.out.println("modify10");
			AbstractMutator[] operators10 = {
					 new IteratorModificationMutator(),
					
			};
			comments = null;
			for (AbstractMutator operator : operators10) {
				
				comments=operator.generateMutants(atlModel, this.iMetaModel, this.oMetaModel, this.folderMutants,this.wrapper,solution,null);

			
				}
			 this.wrapper=(ATLModel) comments.get(0);
			 this.atlModel=(EMFModel) comments.get(1);
			 break;
				
			}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
					}
		try {
			if(sizeoperation==20 ) {
				
				
			this.save(atlModel,
					NSGAIIpostprocessing.BestSolutionLocation);
				
			}
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return comments;
	 }
	
	public  void Createrule() throws IOException
	 {
		Module module = analyser.getATLModel().allObjectsOf(Module.class).get(0);
		
	// template for creating a new ATL Rule
			ATLFactory atlFactory = ATLFactory.eINSTANCE;
			MatchedRule newRule = atlFactory.createMatchedRule();
			newRule.setName("TestRule");
			InPattern iP = atlFactory.createInPattern();
			OutPattern oP = atlFactory.createOutPattern();
			newRule.setInPattern(iP);
			newRule.setOutPattern(oP);
			module.getElements().add(newRule);
        
	 }
	
	
	public  void changeBinding() throws IOException
	 {
		Module module = analyser.getATLModel().allObjectsOf(Module.class).get(0);
		L=new ArrayList<MatchedRule>();
	// L Contains all the MatchedRules
			for (ModuleElement e : module.getElements()) {
				if (e instanceof MatchedRule) {
					L.add((MatchedRule)e);
				}
			}
			System.out.println("Taille de L= "+L.size());
			int r=PseudoRandom.randInt(0,L.size());
		
			MatchedRule mr=L.get(r);
			String s =mr.getOutPattern().getElements().get(0).getBindings().get(1).getPropertyName();
			mr.getOutPattern().getElements().get(0).getBindings().get(0).setPropertyName(s);
		
			/*	MatchedRule mr=L.get(1);
			String s =mr.getOutPattern().getElements().get(0).getBindings().get(1).getPropertyName();
			mr.getOutPattern().getElements().get(0).getBindings().get(0).setPropertyName(s);
		*/
			
	 }
	
	//Done : supprimer n'importe quel Bindind de n'importe quelle Regle
	public  void DeleteBinding(CoSolution S) throws IOException
	 {
		System.out.println("Debut Delete Binding");
	 int rMr=0;
	 int rEl = 0;
	 int rBind=0;
	 L=new ArrayList<MatchedRule>();
		
		Module module = analyser.getATLModel().allObjectsOf(Module.class).get(0);
		
		// L Contains all the MatchedRules
			for (ModuleElement e : module.getElements()) {
				if (e instanceof MatchedRule) {
					L.add((MatchedRule)e);
				}
			}
			System.out.println("Taille de L= "+L.size());
	if(L.size()!=0)
	{		rMr=PseudoRandom.randInt(0,L.size()-1);
		
			MatchedRule mr=L.get(rMr);
	
			
	if(!(mr.getOutPattern().getElements()==null))		
	{		
			rEl=PseudoRandom.randInt(0,mr.getOutPattern().getElements().size()-1);
			if ((rEl!=0 ||!(mr.getOutPattern().getElements().get(rEl).getBindings().size()==0)))
	{		rBind=PseudoRandom.randInt(0,mr.getOutPattern().getElements().get(rEl).getBindings().size()-1);
			//module.getElements()
			System.out.println("rMr= "+rMr);
			System.out.println("rBind= "+rEl);
			System.out.println("rEl= "+rBind);
			
			mr.getOutPattern().getElements().get(rEl).getBindings().remove(rBind);
		
			System.out.println("Fin Delete Binding");
	}	
	}
}	

	ATLSerializer.serialize(S.getOp().getAnalyser().getATLModel(),  "examples/class2rel/trafo/newcl2rel4.atl");
	 }
	
	public static  int random(int min, int max)
	{
		
    	int x=(int)(Math.random()*100);
    	if(max-min<=1 || max==0)
    		return 0;
    	while (x<min || x>max)
    	{
    		x=(int)(Math.random()*100);
    		//System.out.println(x+".");	
    	}
    	return x;
    }
	
}
